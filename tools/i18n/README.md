# i18n

A CLI for managing Android `strings.xml` translations, built on Bun, citty and the Vercel AI SDK. It can call any
OpenAI-compatible API.

## Setup

```bash
cd tools/i18n
bun install
```

Configure the API in `tools/i18n/.env` (never committed):

```
OPENAI_API_KEY=your_api_key
OPENAI_BASE_URL=https://api.openai.com/v1
```

Modules, languages, glossary, model and prompt are configured in `config.yml`.

## Commands

```bash
bun run i18n --help                                      # list all commands; <command> --help shows its options
bun run i18n add hello_world "Hello, World!"             # add an English entry and translate it to every language
bun run i18n add greeting "Welcome" -m ui --skip-translate
bun run i18n set hello_world "سلام دنیا!" -l values-fa   # edit one language by hand
bun run i18n remove hello_world greeting                 # remove from every language
bun run i18n list --missing                              # list entries with missing translations
bun run i18n list --match 'memory'                       # search by key or English text
bun run i18n translate-missing --dry-run                 # count missing entries
bun run i18n translate-missing -l values-ar,values-ja    # fill in specific languages (comma separated)
bun run i18n retranslate -l values-ja --match '\bmemory\b'
bun run i18n retranslate -l values-ja --key some_key,other_key
bun run i18n unused                                      # find unreferenced entries and extra translations
bun run i18n unused --delete
bun run i18n test-connection
```

From the repository root you can use `bun tools/i18n/src/cli.ts <command>`.

## Implementation notes

- When reading and writing `strings.xml`, only the affected `<string>` elements change; comments, `<plurals>`,
  `<string-array>`, whitespace and order are preserved. New entries are appended to the end of the file.
- Values are the raw XML inside the element (`&amp;` and `\'` are kept as is). Input to `add`/`set` is treated as plain text and
  `&`, `<`, `>` and quotes are escaped automatically; add `--raw` to write XML directly.
- Entries with `translatable="false"` are excluded from translation and missing counts.
- Translations are normalized (bare quotes and `&` escaped) and checked to have the same format specifiers, number of `\n` and
  inline tags as the source. Failing entries are retried; entries that still fail are not written, are listed at the end, and
  the command exits with a non-zero status.
- The glossary for the target language is appended to the end of the prompt.
- `unused` scans every file in `source_patterns` for `R.string.xxx` / `@string/xxx` references (across modules); keys built
  dynamically cannot be detected, so confirm before deleting.

## Tests

```bash
bun test                                           # offline unit tests
I18N_LIVE=1 bun test test/translator.live.test.ts  # calls the real API
bun run typecheck
bun run fmt                                        # format with oxfmt (fmt:check only checks)
```