// Executes the actual shared resource generators in a Java-only Gradle fixture.
// This deliberately does not claim to compile, reobfuscate, or release a mod.
const fs = require('node:fs');
const path = require('node:path');
const cp = require('node:child_process');
const core = path.resolve(__dirname, '..');
const base = path.dirname(core);
const output = path.join(core, 'build/postfix-verification');
const fixture = path.join(output, 'resource-fixture');
fs.mkdirSync(fixture, {recursive:true});
const targets = {
  'FarmersDelight-Legacy':['1.6.4','1.7.10','1.8.9','1.12.2'],
  'Corn-Delight-Legacy':['1.7.10','1.12.2'],
  'Oceans-Delight-Legacy':['1.7.10','1.12.2'],
  'End-s-Delight':['1.7.10','1.12.2'],
};
const quoted = s => "'"+s.replaceAll('\\','/').replaceAll("'","\\'")+"'";
let settings="rootProject.name='postfix-resource-verification'\n";
for (const [repo, versions] of Object.entries(targets)) {
  for (const mc of versions) {
    const name=repo+'-'+mc.replaceAll('.','_');
    const project=path.join(base,repo,'versions',mc);
    fs.mkdirSync(path.join(project,'build'),{recursive:true});
    const script="plugins { id 'java' }\next.minecraftTarget="+quoted(mc)+"\nlayout.buildDirectory=file('build/postfix-resources')\napply from: '../../build-logic/legacy.gradle'\n";
    fs.writeFileSync(path.join(project,'build/postfix-resource-fixture.gradle'),script);
    settings+=`include ${quoted(name)}\nproject(${quoted(':'+name)}).projectDir=file(${quoted(project)})\nproject(${quoted(':'+name)}).buildFileName='build/postfix-resource-fixture.gradle'\n`;
  }
}
fs.writeFileSync(path.join(fixture,'settings.gradle'),settings);
fs.writeFileSync(path.join(fixture,'build.gradle'),'// Resource verification only.\n');
const java=process.env.JAVA21_HOME || process.env.JAVA_HOME;
const env={...process.env,JAVA_HOME:java,GRADLE_USER_HOME:process.env.GRADLE_USER_HOME || path.join(output,'gradle-home')};
const args=['-classpath',path.join(core,'gradle/wrapper/gradle-wrapper.jar'),'org.gradle.wrapper.GradleWrapperMain','--settings-file',path.join(fixture,'settings.gradle'),'processResources','--offline','--continue','--stacktrace'];
console.log('Resource-only Gradle fixture: '+args.slice(3).join(' '));
const r=cp.spawnSync(path.join(java,'bin/java.exe'),args,{cwd:fixture,env,stdio:'inherit'});
if(r.error) throw r.error;
process.exitCode=r.status;
