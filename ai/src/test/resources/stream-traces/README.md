# Stream trace fixtures

Each directory contains two files:

- `events.jsonl`: one serialized `SseEvent` per line, recorded after the HTTP client has split the SSE frames and
  before the provider decodes them. Never record request headers such as Authorization.
- `expected.json`: a stable semantic snapshot after `StreamChunkDecoder` and `StreamChunkHandler` have processed
  the stream. Volatile fields such as timestamps and random message IDs are left out of the snapshot.

When adding or updating a trace, keep the `id`, `event` and `data` returned by the provider, and review
`expected.json` by hand before the first commit. Regular unit tests only replay these files offline and never use
the network.

After recording a new trace, regenerate the semantic snapshots from the `ai` module directory:

```bash
UPDATE_STREAM_TRACE_SNAPSHOTS=true ../gradlew testDebugUnitTest \
  --tests me.rerere.ai.provider.stream.StreamTraceReplayTest
```

Snapshots keep tool call IDs, the full metadata, reasoning text, tool names and arguments, and token usage. Image
data stays in `events.jsonl`; the snapshot only records the MIME type, the decoded byte count and the SHA-256, so
large Base64 blobs are not stored twice. Gemini image traces also compare the provider's raw signature with the
decoded metadata character by character, to verify that the signature is not altered during stream conversion.