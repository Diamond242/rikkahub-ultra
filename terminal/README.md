# terminal

Terminal emulator core used by the workspace terminal page. The UI part (`TerminalView`) still uses the upstream
`terminal-view` aar.

## Origin

`src/main/java` and `src/test/java` come from the `terminal-emulator` module of
[termux-app](https://github.com/termux/termux-app), tag `v0.118.0` (commit `6e2689f55295fa444be8ac8592c527c2c5ef3253`),
released under the Apache License 2.0; see [LICENSE](LICENSE) for the full text. The package name stays
`com.termux.terminal` to remain binary compatible with the `terminal-view` aar.

## Differences from upstream

- The native part does not use upstream's `termux.c`; it uses `src/main/cpp/termux_pty.cpp` instead, compiled with 16 KB
  page alignment
- `TerminalSession` no longer manages a local process directly; it sends and receives data through `TerminalTransport` so
  remote shells can be plugged in. The local pty process moved to `LocalPtyTransport` (the fd is held by a
  `ParcelFileDescriptor` instead of reflecting the private field of `FileDescriptor`); the process starts on a background
  thread and prints the reason in the terminal if it fails to start; before exiting it waits for the reader thread to drain
  the last output; `getPid()` / `getCwd()`, which only make sense for local processes, were removed
- Writes from the main thread never block on a queue nobody reads: input during startup is buffered first (at most 4096 bytes;
  a chunk that does not fit is dropped as a whole) and sent after startup; once the transport stops accepting input, the
  writer thread keeps draining the queue until the session is cleaned up
- Closing a local terminal kills every process in the session, not just the session leader: when the leader is PRoot, the
  shell and its jobs are traced processes, and killing only PRoot would leave them running untraced