package vectorwing.farmersdelight.legacy;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.chunk.storage.RegionFile;
import org.apache.logging.log4j.LogManager;

/** One-time, offline conversion from a world's own mapping; never reinterpret live effects. */
final class PotionSaveMigration {
    private static final String BACKUPS = "potion-id-migrations";

    static void migrate(File directory, Properties current) {
        Path root = directory.toPath().toAbsolutePath().normalize();
        File snapshot = root.resolve("data/legacy-potion-ids.properties").toFile();
        File pending = root.resolve("data/legacy-potion-migration.properties").toFile();
        try {
            if (pending.isFile()) commit(root, snapshot, pending);
            Properties saved = PotionIdRegistry.read(snapshot);
            final Map<Integer, Integer> changes = new HashMap<Integer, Integer>();
            for (String key : saved.stringPropertyNames()) {
                if (key.equals("schema")) continue;
                String replacement = current.getProperty(key);
                if (replacement == null || Integer.parseInt(replacement) < 0)
                    throw new IOException("Missing saved potion allocation " + key);
                int previous = Integer.parseInt(saved.getProperty(key));
                int next = Integer.parseInt(replacement);
                if (previous >= 0 && previous != next) changes.put(previous, next);
            }
            if (changes.isEmpty()) {
                if (!saved.equals(current)) PotionIdRegistry.write(snapshot, current);
                return;
            }

            LogManager.getLogger("farmersdelight").info("Migrating saved potion IDs in {}: {}", root, changes);
            final Path run = root.resolve("data/" + BACKUPS + "/" + UUID.randomUUID());
            Files.createDirectories(run);
            PotionIdRegistry.write(run.resolve("source.properties").toFile(), saved);
            PotionIdRegistry.write(run.resolve("target.properties").toFile(), current);
            final List<Path> converted = new ArrayList<Path>();
            final Path backupRoot = root.resolve("data/" + BACKUPS);
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) {
                    return path.equals(backupRoot) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                }
                @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                    if (!attributes.isRegularFile()) return FileVisitResult.CONTINUE;
                    String name = path.getFileName().toString();
                    Path relative = root.relativize(path);
                    boolean changed = false;
                    if (name.matches("r\\.-?\\d+\\.-?\\d+\\.mc[ar]"))
                        changed = region(path, run, relative, changes);
                    else if (name.endsWith(".dat") || name.endsWith(".dat_old"))
                        changed = data(path, run, relative, changes);
                    if (changed) converted.add(relative);
                    return FileVisitResult.CONTINUE;
                }
            });
            Properties manifest = new Properties();
            manifest.setProperty("count", Integer.toString(converted.size()));
            for (int i = 0; i < converted.size(); i++) {
                Path path = converted.get(i);
                manifest.setProperty("file." + i, path.toString());
                manifest.setProperty("sha256." + i, hash(run.resolve("converted").resolve(path)));
            }
            PotionIdRegistry.write(run.resolve("manifest.properties").toFile(), manifest);
            Properties journal = new Properties();
            journal.setProperty("run", run.getFileName().toString());
            // No original is replaced until every conversion and its backup is durable.
            PotionIdRegistry.write(pending, journal);
            commit(root, snapshot, pending);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot migrate saved potion IDs in " + root
                    + ". World loading stopped; migration backups and any pending transaction were retained.", exception);
        }
    }

    private static void commit(Path root, File snapshot, File pending) throws IOException {
        String id = properties(pending.toPath()).getProperty("run", "");
        if (!id.matches("[0-9a-f-]{36}")) throw new IOException("Invalid potion migration transaction");
        Path run = root.resolve("data/" + BACKUPS + "/" + id);
        Properties source = PotionIdRegistry.read(run.resolve("source.properties").toFile());
        Properties target = PotionIdRegistry.read(run.resolve("target.properties").toFile());
        Properties saved = PotionIdRegistry.read(snapshot);
        Properties manifest = properties(run.resolve("manifest.properties"));
        int count = Integer.parseInt(manifest.getProperty("count"));
        if (!saved.equals(target)) {
            if (!saved.equals(source)) throw new IOException("World mapping changed during potion migration");
            for (int i = 0; i < count; i++) {
                Path staged = child(run.resolve("converted"), manifest.getProperty("file." + i));
                if (!hash(staged).equals(manifest.getProperty("sha256." + i)))
                    throw new IOException("Damaged staged potion migration file: " + staged);
                child(root, manifest.getProperty("file." + i));
            }
            for (int i = 0; i < count; i++) {
                Path destination = child(root, manifest.getProperty("file." + i));
                Path staged = child(run.resolve("converted"), manifest.getProperty("file." + i));
                replace(staged, destination);
            }
            // Committing the mapping last makes replay safe even for swaps and repeated changes.
            PotionIdRegistry.write(snapshot, target);
        }
        Files.delete(pending.toPath());
        LogManager.getLogger("farmersdelight").info("Potion ID migration complete: {} files; backups in {}", count, run);
    }

    private static Path child(Path root, String relative) throws IOException {
        if (relative == null) throw new IOException("Missing potion migration path");
        Path result = root.resolve(relative).normalize();
        if (!result.startsWith(root) || result.equals(root)) throw new IOException("Invalid potion migration path: " + relative);
        return result;
    }

    private static Properties properties(Path path) throws IOException {
        Properties result = new Properties();
        try (FileInputStream input = new FileInputStream(path.toFile())) { result.load(input); }
        return result;
    }

    private static void replace(Path source, Path destination) throws IOException {
        Path temporary = Files.createTempFile(destination.getParent(), "potion-migration-", ".tmp");
        try {
            Files.copy(source, temporary, StandardCopyOption.REPLACE_EXISTING);
            sync(temporary);
            Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } finally { Files.deleteIfExists(temporary); }
    }

    private static Path stage(Path source, Path run, Path relative) throws IOException {
        Path backup = run.resolve("original").resolve(relative);
        Path target = run.resolve("converted").resolve(relative);
        Files.createDirectories(backup.getParent());
        Files.createDirectories(target.getParent());
        Files.copy(source, backup);
        sync(backup);
        return target;
    }

    private static void sync(Path path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw")) { file.getFD().sync(); }
    }

    private static String hash(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(path)) {
                byte[] buffer = new byte[65536];
                int length;
                while ((length = input.read(buffer)) != -1) digest.update(buffer, 0, length);
            }
            StringBuilder value = new StringBuilder();
            for (byte b : digest.digest()) value.append(String.format("%02x", b & 255));
            return value.toString();
        } catch (NoSuchAlgorithmException exception) { throw new AssertionError(exception); }
    }

    private static NBTTagCompound readData(Path source, boolean gzip) throws IOException {
        try (InputStream input = Files.newInputStream(source)) {
            if (gzip) return CompressedStreamTools.readCompressed(input);
            return CompressedStreamTools.read(new DataInputStream(new BufferedInputStream(input)));
        }
    }

    private static boolean data(Path source, Path run, Path relative, Map<Integer, Integer> changes) throws IOException {
        int first, second;
        try (InputStream input = Files.newInputStream(source)) { first = input.read(); second = input.read(); }
        boolean gzip = first == 31 && second == 139;
        // Other mods may use .dat for non-NBT files.
        if (!gzip && first != 10) return false;
        NBTTagCompound value = readData(source, gzip);
        if (!convert(value, changes)) return false;
        Path target = stage(source, run, relative);
        try (FileOutputStream output = new FileOutputStream(target.toFile())) {
            if (gzip) CompressedStreamTools.writeCompressed(value, output);
            else CompressedStreamTools.write(value, new DataOutputStream(output));
        }
        if (!value.equals(readData(target, gzip))) throw new IOException("Potion NBT verification failed: " + source);
        sync(target);
        return true;
    }

    private static boolean region(Path source, Path run, Path relative, Map<Integer, Integer> changes) throws IOException {
        Path target = null;
        RegionFile output = null;
        try (RandomAccessFile input = new RandomAccessFile(source.toFile(), "r")) {
            if (input.length() == 0) return false;
            if (input.length() < 8192) throw new IOException("Incomplete region header: " + source);
            for (int index = 0; index < 1024; index++) {
                input.seek(index * 4L);
                int offset = input.readInt();
                if (offset == 0) continue;
                int sector = offset >>> 8, sectors = offset & 255;
                if (sector < 2 || sectors == 0 || (sector + (long)sectors) * 4096 > input.length())
                    throw new IOException("Invalid region chunk offset: " + source + " chunk " + index);
                input.seek(sector * 4096L);
                int length = input.readInt();
                int compression = input.readUnsignedByte();
                if (length <= 1 || length > sectors * 4096 - 4 || (compression != 1 && compression != 2))
                    throw new IOException("Invalid region chunk data: " + source + " chunk " + index);
                byte[] bytes = new byte[length - 1];
                input.readFully(bytes);
                InputStream compressed = new ByteArrayInputStream(bytes);
                NBTTagCompound chunk;
                try (DataInputStream stream = new DataInputStream(compression == 1
                        ? new GZIPInputStream(compressed) : new InflaterInputStream(compressed))) {
                    chunk = CompressedStreamTools.read(stream);
                }
                if (!convert(chunk, changes)) continue;
                if (output == null) {
                    target = stage(source, run, relative);
                    Files.copy(source, target);
                    output = new RegionFile(target.toFile());
                }
                int x = index & 31, z = index >> 5;
                try (DataOutputStream stream = output.getChunkDataOutputStream(x, z)) { CompressedStreamTools.write(chunk, stream); }
                // Native RegionFile swallows some I/O/oversize errors. Verify before committing.
                try (DataInputStream stream = output.getChunkDataInputStream(x, z)) {
                    if (stream == null || !chunk.equals(CompressedStreamTools.read(stream)))
                        throw new IOException("Potion region verification failed: " + source + " chunk " + index);
                }
            }
        } finally { if (output != null) output.close(); }
        if (target != null) sync(target);
        return target != null;
    }

    private static boolean convert(NBTBase tag, Map<Integer, Integer> changes) {
        boolean changed = false;
        if (tag instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound)tag;
            for (String key : compound.func_150296_c()) {
                NBTBase child = compound.getTag(key);
                if ((key.equals("ActiveEffects") || key.equals("CustomPotionEffects")) && child instanceof NBTTagList) {
                    NBTTagList effects = (NBTTagList)child;
                    if (effects.func_150303_d() == 10) for (int i = 0; i < effects.tagCount(); i++) {
                        NBTTagCompound effect = effects.getCompoundTagAt(i);
                        int type = effect.func_150299_b("Id");
                        if (type < 1 || type > 4) continue;
                        int previous = type == 1 ? effect.getByte("Id") & 255 : effect.getInteger("Id");
                        Integer next = changes.get(previous);
                        if (next == null) continue;
                        if (type == 1 && next <= 127) effect.setByte("Id", next.byteValue());
                        else if (type == 2) effect.setShort("Id", next.shortValue());
                        else effect.setInteger("Id", next);
                        changed = true;
                    }
                }
                changed |= convert(child, changes);
            }
        } else if (tag instanceof NBTTagList) {
            NBTTagList list = (NBTTagList)tag;
            if (list.func_150303_d() == 10) {
                for (int i = 0; i < list.tagCount(); i++) changed |= convert(list.getCompoundTagAt(i), changes);
            } else if (list.func_150303_d() == 9) {
                int count = list.tagCount();
                for (int i = 0; i < count; i++) {
                    NBTBase child = list.removeTag(0);
                    changed |= convert(child, changes);
                    list.appendTag(child);
                }
            }
        }
        return changed;
    }

    private PotionSaveMigration() {}
}
