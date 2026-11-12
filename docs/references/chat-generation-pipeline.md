# Message generation pipeline

This document describes the full data flow from the moment the user sends a message until the AI reply is complete, including
the core classes and processing stages involved.

## Core classes

| Class                      | Responsibility |
|----------------------------|----------------|
| `ChatService`              | Entry point and orchestration layer; manages all sessions and exposes the public operations |
| `ConversationSession`      | State container of a single conversation (reference count, generation job, processing state) |
| `GenerationHandler`        | Core generation logic; drives the step loop and tool calls |
| `InputMessageTransformer`  | Transformation pipeline applied to the message list before it is sent to the API |
| `OutputMessageTransformer` | Transformation pipeline applied to the message list after streamed chunks arrive |

---

## Full generation flow

```
User sends a message
    │
    ▼
ChatService.sendMessage()
    ├── cancel the previous job (cancel + join)
    ├── finishInterruptedPendingTools()  // complete tool output interrupted last time
    ├── preprocessUserInputParts()       // apply the assistant's regex replacements to the user text
    ├── append UIMessage(USER) to Conversation.messageNodes
    └── handleMessageComplete()
            │
            ▼
        GenerationHandler.generateText()   ← Flow<GenerationChunk>
            │  (loop of at most maxSteps=256 rounds)
            │
            ├─ [no pending tool] generateInternal()
            │       ├── build internalMessages
            │       │       ├── system message (system prompt + memories + tool.systemPrompt)
            │       │       ├── limitContext() trims history in steps according to contextMessageLimit
            │       │       └── InputTransformers pipeline
            │       ├── build TextGenerationParams
            │       └── call the provider
            │               ├── stream=true → providerImpl.streamText() emits chunk by chunk
            │               └── stream=false → providerImpl.generateText() returns once
            │
            ├─ on every chunk → OutputTransformers.transforms() (live)
            │                 → OutputTransformers.visualTransforms() → emit GenerationChunk
            │
            ├─ generation finished → OutputTransformers.visualTransforms()
            │                      → OutputTransformers.onGenerationFinish()
            │                      → set message.finishedAt
            │
            ├─ check the latest message for tools not yet executed
            │       ├── no tool → break (generation ends)
            │       ├── tool needs approval → set ToolApprovalState.Pending
            │       │                        → emit → break (wait for the user's approval)
            │       └── tool approved → execute the tool (see below)
            │
            ├─ tool execution
            │       ├── Denied   → output {"error": "denied by user"}
            │       ├── Answered → use the answer provided by the user
            │       └── Auto / Approved → toolDef.execute(args)
            │               ├── output over 32 KB and shell access available → truncate and write to a file
            │               └── CancellationException must propagate (never swallow it)
            │
            └─ write the results back to messages → emit → continue with the next step
                    (tool results are inlined in the parts of the ASSISTANT message; no TOOL role message is created)

    ▼
onCompletion (flow finished or cancelled)
    ├── cancelLiveUpdateNotification()
    ├── finishReasoning() on every message (safety net)
    └── if the app is not in the foreground → sendGenerationDoneNotification()

    ▼
onSuccess
    ├── saveConversation()
    ├── generateTitle()      (async, uses titleModel)
    └── generateSuggestion() (async, uses suggestionModel)
```

---

## Stage 1: user message preprocessing

**Entry point**: `ChatService.preprocessUserInputParts()`

Applies `Assistant.replaceRegexes(scope=USER)` to the user's `UIMessagePart.Text`, i.e. the regex replacement rules with
`AffectScope.USER` from the assistant configuration, used to normalize or redact input text. Non-text parts (images,
documents, ...) are not processed.

---

## Stage 2: input message transformation pipeline

**When**: after `generateInternal()` has built `internalMessages`, before sending to the API.

Transformers run in order (`fold`); each receives the output of the previous one:

| Order | Transformer                    | Description |
|-------|--------------------------------|-------------|
| 1     | `TimeReminderTransformer`      | Injects the current time/date into the system message |
| 2     | `PromptInjectionTransformer`   | Injects prompts from ModeInjection and triggered Lorebook entries |
| 3     | `PlaceholderTransformer`       | Replaces `{{placeholder}}` placeholders in messages |
| 4     | `DocumentAsPromptTransformer`  | Converts document attachments into text injected into the message |
| 5     | `OcrTransformer`               | Runs OCR on image parts and appends the recognized text |
| 6     | `TemplateTransformer`          | Renders messages with the Pebble template engine (time/date/role variables available) |
| 7     | `WorkspaceReminderTransformer` | If the conversation is linked to a workspace, injects a hint with the workspace path |

`PromptInjectionTransformer` supports these injection positions:

- `BEFORE_SYSTEM_PROMPT` / `AFTER_SYSTEM_PROMPT` — before or after the system message
- `TOP_OF_CHAT` — before the first user message
- `BOTTOM_OF_CHAT` — before the last message
- `AT_DEPTH` — at the Nth message counting back from the latest

---

## Stage 3: output message transformation pipeline

Called at three points:

| When                                  | Method                 | Description |
|---------------------------------------|------------------------|-------------|
| Streamed chunk arrives (live)         | `transforms()`         | Real transformation, used for the stored message |
| Streamed chunk arrives (UI display)   | `visualTransforms()`   | Visual transformation that does not affect storage (e.g. streaming think tag conversion) |
| Generation fully finished             | `onGenerationFinish()` | Final post-processing, e.g. saving base64 images to disk |

Current output transformers:

| Transformer                         | transforms | visualTransform | onGenerationFinish |
|-------------------------------------|------------|-----------------|--------------------|
| `ThinkTagTransformer`               | —          | ✓ (`<think>` → reasoning part) | ✓ (final extraction) |
| `Base64ImageToLocalFileTransformer` | —          | —               | ✓ (base64 → local file) |
| `RegexOutputTransformer`            | ✓ (assistant OUTPUT regex replacements) | — | — |

---

## Stage 4: tool system

### Tool registration order

`handleMessageComplete()` builds the tool list in this order:

1. **Search tools** (`createSearchTools`) — when `settings.enableWebSearch = true`
2. **Local tools** (`localTools.getTools(assistant.localTools)`) — enabled per assistant:
  - `JavascriptEngine`: runs JS snippets
  - `TimeInfo`: gets the current time
  - `Clipboard`: reads and writes the clipboard
  - `Tts`: text to speech
  - `AskUser`: asks the user a question (needs approval)
  - `ScreenTime`: gets screen time usage
3. **Conversation tools** (`createConversationTools`) — when `enableRecentChatsReference = true`, query past conversations
4. **Workspace tools** (`createWorkspaceToolsIfReady`) — injected when the workspace shell is ready, including `workspace_shell`
5. **Skill tools** (`createSkillTools`) — skills enabled for the assistant
6. **MCP tools** — tools of every connected MCP server, named `mcp__{serverName}__{toolName}`
7. **Memory tools** (`buildMemoryTools`, built into GenerationHandler) — when `enableMemory = true`, add, edit and delete memories

### Tool approval state machine

```
Auto (default)
    │ toolDef.needsApproval() == true
    ▼
Pending ──── user action ────► Approved → execute the tool
                         ──► Denied   → return a denial error
                         ──► Answered → use the text provided by the user as the result
```

Approval is triggered by `ChatService.handleToolApproval()`, which updates the state and calls `handleMessageComplete()` again.
When `GenerationHandler` detects a tool with `canResumeExecution`, it skips generation for that round and goes straight to tool
execution.

### Tool output truncation

When the workspace shell tool is available and a tool's output exceeds **32 KB**, the output is truncated:

- the first 4 KB stay in the message
- the full output is written to `filesDir/tool_outputs/{toolCallId}.txt`
- the message includes a hint with the shell command to read it

`search_web` is never truncated: its size depends on the result count set by the user, and the result list UI and citation
links rely on the complete JSON.

---

## Stage 5: session lifecycle

`ConversationSession` manages its memory lifecycle with an **atomic reference count**:

- `acquire()` / `release()` — called when a UI page opens or closes
- `refCount == 0 && !isGenerating` — after a 5 second idle timeout, `ChatService.removeSession()` cleans up the session
- `setJob()` — sets the generation job; it is set back to null when done and triggers the idle check

---

## Stage 6: background notifications

| Notification           | Trigger                                   | Channel |
|------------------------|-------------------------------------------|---------|
| Live Update (ongoing)  | While generating with the app in the background | `CHAT_LIVE_UPDATE_NOTIFICATION_CHANNEL_ID` |
| Generation complete    | Generation finished with the app in the background | `CHAT_COMPLETED_NOTIFICATION_CHANNEL_ID` |

The Live Update content follows the current generation state:

- running a tool → shows the tool name and an input preview
- reasoning → shows a snippet of the reasoning
- writing the reply → shows a snippet of the text

---

## Key file paths

```
app/src/main/java/me/rerere/rikkahub/
├── service/
│   ├── ChatService.kt              # orchestration entry point
│   └── ConversationSession.kt      # conversation state container
└── data/ai/
    ├── GenerationHandler.kt        # core generation logic
    ├── transformers/
    │   ├── Transformer.kt          # interfaces and extension functions
    │   ├── PromptInjectionTransformer.kt
    │   ├── TemplateTransformer.kt
    │   ├── TimeReminderTransformer.kt
    │   ├── ThinkTagTransformer.kt
    │   ├── RegexOutputTransformer.kt
    │   ├── DocumentAsPromptTransformer.kt
    │   ├── OcrTransformer.kt
    │   ├── Base64ImageToLocalFileTransformer.kt
    │   ├── PlaceholderTransformer.kt
    │   └── WorkspaceReminderTransformer.kt
    └── tools/
        ├── SearchTools.kt
        ├── ConversationTools.kt
        ├── WorkspaceTools.kt
        ├── SkillsTools.kt
        ├── MemoryTools.kt
        └── local/
            └── LocalTools.kt       # local tool registry
```