// Run from the Farmer's Delight repository: node scripts/verify-postfix.cjs
// Source/catalog checks only. Native lookup and production checks are separate.
const fs = require('node:fs');
const path = require('node:path');
const cp = require('node:child_process');
const base = path.resolve(__dirname, '../..');
const out = path.resolve(__dirname, '../build/postfix-verification');
fs.mkdirSync(out, {recursive: true});
const specs = [
  ['FarmersDelight-Legacy', 'farmersdelight'],
  ['Corn-Delight-Legacy', 'corn_delight'],
  ['Oceans-Delight-Legacy', 'oceansdelight'],
  ['End-s-Delight', 'ends_delight'],
];
const read = f => fs.readFileSync(f, 'utf8').replace(/^\uFEFF/, '');
const json = f => JSON.parse(read(f));
function files(root, suffix) {
  if (!fs.existsSync(root)) return [];
  return fs.readdirSync(root, {withFileTypes: true}).flatMap(e => {
    const p = path.join(root, e.name);
    return e.isDirectory() ? files(p, suffix) : p.endsWith(suffix) ? [p] : [];
  });
}
const results = [];
function check(label, pass, details) {
  results.push({label, pass: !!pass, details});
  if (!pass) console.error('FAIL', label, details);
}
const snapshot = [];
for (const [name, ns] of specs) {
  const root = path.join(base, name);
  const git = args => cp.execFileSync('git', ['-c', `safe.directory=${root.replaceAll('\\', '/')}`, '-c', 'core.quotePath=false', '-C', root, ...args], {encoding:'utf8'});
  snapshot.push({name, root, remote:git(['remote','get-url','origin']).trim(), head:git(['rev-parse','HEAD']).trim(), status:git(['status','--short'])});
  if (ns === 'farmersdelight') continue;
  const translations = {};
  for (const resource of ['src/main/resources', 'src/generated/resources']) {
    for (const f of files(path.join(root, resource, 'assets', ns, 'lang'), '.json')) {
      const locale = path.basename(f, '.json').toLowerCase();
      translations[locale] = {...translations[locale], ...json(f)};
    }
  }
  const catalog = json(path.join(root, 'common/src/main/resources', ns, ns === 'ends_delight' ? 'content.json' : 'foods.json'));
  const expected = catalog.map(x => `${x.kind === 'block' ? 'block' : 'item'}.${ns}.${x.id}`);
  if (ns === 'corn_delight') expected.push(...['corn_crop','corn_crate','corn_kernel_bag','wild_corn'].map(id=>'block.'+ns+'.'+id));
  if (ns === 'oceansdelight') expected.push('block.oceansdelight.guardian_soup','item.oceansdelight.guardian');
  const missing = expected.filter(k => !translations.en_us?.[k]);
  check(`${ns}: English registered catalog coverage`, !missing.length, {keys:expected.length, missing});
  if (ns === 'ends_delight') {
    check('succulent English item name', translations.en_us['item.ends_delight.chorus_succulent'] === 'Succulent Chorus Fruit');
    const missingLocales = Object.entries(translations).filter(([,v])=>!v['item.ends_delight.chorus_succulent']).map(([k])=>k);
    check('succulent translated item aliases', !missingLocales.length, {missingLocales});
  }
  for (const mc of ['1.12.2','1.7.10']) {
    const langRoot = path.join(root,'versions',mc,process.argv.includes('--fixture')?'build/postfix-resources/resources/main/assets':'build/resources/main/assets',ns,'lang');
    const localeChecks = [];
    for (const [locale, values] of Object.entries(translations)) {
      const nativeLocale = mc==='1.12.2' ? locale : locale.slice(0,3)+locale.slice(3).toUpperCase();
      const f = path.join(langRoot,nativeLocale+'.lang');
      if (!fs.existsSync(f)) {localeChecks.push({locale, missingFile:true}); continue;}
      const entries = new Map(read(f).split(/\r?\n/).filter(l=>l && !l.startsWith('#')).map(l=>[l.slice(0,l.indexOf('=')),l.slice(l.indexOf('=')+1)]));
      const mismatches = Object.entries(values).filter(([key,value]) => {
        let nativeKey = key.startsWith('block.') ? 'tile.'+key.slice(6) : key;
        if (/^(tile|item)\./.test(nativeKey)) nativeKey=nativeKey.replace(/(?:\.name)+$/,'')+'.name';
        return entries.get(nativeKey)!==String(value);
      }).map(([k])=>k);
      localeChecks.push({locale,entries:entries.size,mismatches,malformed:[...entries.keys()].filter(k=>k.endsWith('.name.name'))});
    }
    check(`${ns} ${mc}: generated locales match source`, localeChecks.every(x=>!x.missingFile && !x.mismatches.length && !x.malformed.length), localeChecks);
  }
}
fs.writeFileSync(path.join(out,'repository-state.json'),JSON.stringify(snapshot,null,2));
fs.writeFileSync(path.join(out,'resource-checks.json'),JSON.stringify(results,null,2));
console.log(`${results.filter(x=>x.pass).length}/${results.length} source/generated resource checks passed`);
process.exitCode=results.every(x=>x.pass)?0:1;
