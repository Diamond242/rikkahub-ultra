# trace

Generates `events.jsonl` files that the `ai` module can replay, from real provider SSE responses. The CLI only records `id`,
`event`, `data` and `retryMillis` after SSE framing; it never records request headers or API keys.

Supported providers:

- `openai-responses`
- `openai-chat`
- `claude`
- `google-generateContent`
- `google-interactions`

## Install

```bash
cd tools/trace
bun install
cp .env.example .env
```

Fill in the keys you need in `.env`:

```dotenv
OPENAI_API_KEY=...
ANTHROPIC_API_KEY=...
GEMINI_API_KEY=...
DEEPSEEK_API_KEY=...
```

`.env` is excluded by the subproject's `.gitignore`. A single trace can also name a different environment variable with
`apiKeyEnv`.

## Recording traces

Edit the providers, models and request bodies to record directly in `traces.yml`.

List the cases after the configuration is parsed:

```bash
bun run trace -- traces.yml --list
```

Inspect the actual (redacted) request without using the network:

```bash
bun run trace -- traces.yml --case openai-responses-tool --dry-run
```

Record one or all traces:

```bash
bun run trace -- traces.yml --case openai-responses-tool
bun run trace -- traces.yml
```

Existing files are not overwritten by default. When you really want to record again, use:

```bash
bun run trace -- traces.yml --case openai-responses-tool --force
```

The CLI writes to a temporary file first and atomically replaces the target `events.jsonl` only after the request and SSE
parsing have fully succeeded.

## YAML format

```yaml
version: 1
defaults:
  outputRoot: ../../ai/src/test/resources/stream-traces/generated
  timeoutMs: 120000
  headers: {}

traces:
  - name: responses-tool
    provider: openai-responses
    model: gpt-5.6
    apiKeyEnv: OPENAI_API_KEY
    # Optional: override the provider's default authentication, e.g. Bearer for OpenRouter Anthropic Messages.
    auth:
      header: Authorization
      scheme: Bearer
    baseUrl: https://api.openai.com/v1
    # endpoint may be omitted, or be a path relative to baseUrl, or a full URL.
    endpoint: /responses
    # output may be omitted; it defaults to outputRoot/provider/name/events.jsonl.
    output: ../../ai/src/test/resources/stream-traces/openai-responses/responses-tool/events.jsonl
    headers: {}
    timeoutMs: 120000
    body:
      store: false
      input:
        - role: user
          content: Call the weather tool for Paris.
      tools:
        - type: function
          name: weather
          description: Get weather for a city.
          parameters:
            type: object
            properties:
              city:
                type: string
            required: [city]
            additionalProperties: false
```

The CLI adds the authentication headers each provider needs and forces `stream: true` for OpenAI, Claude and Google
Interactions requests. `google-generateContent` uses `:streamGenerateContent?alt=sse` and the model name is only used in the
URL; `google-interactions` uses `/interactions` and writes the model name into the request body. Interactions also allows
omitting `model` and naming an agent directly in `body.agent`.

After recording, you still need to create the matching `expected.json` by hand and add the directory to
`StreamTraceReplayTest`.