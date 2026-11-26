<div align="center">
  <img src="docs/brand/icon.png" alt="RikkaHub Ultra" width="120" />
  <h1>RikkaHub <img src="https://img.shields.io/badge/-Ultra-16A34A?style=flat-square" alt="Ultra" height="22" /></h1>

A native Android LLM chat client that lets you switch between providers — now with full Persian support,
free and Iranian providers, and a friendlier setup.

**English** | [فارسی](README.fa.md)

[Download](https://github.com/Diamond242/rikkahub-ultra/releases/latest) ·
[Documentation](https://vahid.top/rikkahub-ultra) ·
[Release notes](CHANGELOG.md) ·
[Support the project](#-support-the-project)
</div>

> RikkaHub Ultra is a fork of [RikkaHub](https://github.com/rikkahub/rikkahub) by its original authors, maintained by
> [Vahid Borandeh](https://vahid.top/). It installs as a separate app (`top.vahid.rikkahubultra`), next to the original.

## ✨ Features

Items marked **🆕 Ultra** were added in this fork; everything else comes from the original RikkaHub.

### Core (from RikkaHub)

- 🎨 Material 3 Expressive design and 🌙 dark mode
- 🔄 Multiple AI providers: custom API / URL / models (any OpenAI, Google or Anthropic compatible API), plus ChatGPT and Grok account sign-in
- 🖼️ Multimodal input (images, text documents, PDF, DOCX)
- 📦 Workspace: a proot-based Linux agent environment with a terminal
- 🎬 Media generation (image and video)
- 🖥️ Built-in web server to use your chats from any device's browser
- 🛠️ MCP and Skills support, tool calling with approvals
- 📝 Markdown rendering (code highlighting, LaTeX, tables, Mermaid)
- 🔍 Web search (Exa, Tavily, Zhipu, LinkUp, Brave, Perplexity and more)
- 🧩 Prompt variables (model name, time, …), lorebooks and prompt injections
- 🤳 QR code export and import for providers
- 🤖 Assistant customization and 🧠 file-based memory
- 📝 AI translation and text-to-speech
- 💾 Backup and sync (WebDAV / S3)
- 🌐 Custom HTTP headers and request bodies
- 💌 SillyTavern character card import

### Added in Ultra

- 🆕 **Ultra** · **Complete Persian translation** of the app (1800+ strings) and the web UI, full right-to-left layout and the Vazirmatn font
- 🆕 **Ultra** · **In-app language picker** (follows the system language by default)
- 🆕 **Ultra** · Hard-coded Chinese texts moved to translatable resources, English texts polished
- 🆕 **Ultra** · **15 extra providers**: free, trial and Iranian ones (Groq, NVIDIA NIM, Mistral, Cohere, Z.ai, SiliconFlow Global, LLM7, Pollinations, Cerebras, SambaNova, Anthropic, Ollama, AvalAI, GapGPT, Metis)
- 🆕 **Ultra** · Provider badges and filters: *Free*, *Free quota*, *Trial*, *Paid*, *Works without VPN*
- 🆕 **Ultra** · **First-run welcome dialog** that sets up a free provider in about a minute
- 🆕 **Ultra** · Step-by-step **API key guides** with screenshots, and a key-test button
- 🆕 **Ultra** · **Fetch models** with clear errors, **Add all free models**, free-quota notes and a *Free* badge for `:free` models
- 🆕 **Ultra** · Model sorting: default, **by quality** (LMArena scores) or **manual drag order**, remembered across restarts; favorites in every mode
- 🆕 **Ultra** · Model picker: recent models, quick filters (free, no VPN, vision, reasoning, tools) and context-size badges
- 🆕 **Ultra** · **Human-readable error messages** with a shortcut to the provider settings
- 🆕 **Ultra** · Settings search, a clear *Settings* entry in the drawer, simpler chat defaults and a calmer message jumper
- 🆕 **Ultra** · Named built-in assistants (*Simple* / *Smart*, Smart by default) with an info button; visible delete action for assistants
- 🆕 **Ultra** · Correct icon mirroring in right-to-left layouts
- 🆕 **Ultra** · **Updates from this repository**: the app checks the GitHub releases here and shows localized release notes
- 🆕 **Ultra** · New About page with release notes, developer, docs and license, and a new minimal robot icon
- 🆕 **Ultra** · New donate page (crypto with QR codes and network guides) and our own sponsor list

## 📲 Install (Android 8.0+)

1. Open the [latest release](https://github.com/Diamond242/rikkahub-ultra/releases/latest) on your phone.
2. Download the APK for your phone. Almost every modern phone uses **arm64-v8a**; pick `armeabi-v7a` only for very old devices, and `x86_64` for emulators.
3. Open the downloaded file. If Android asks, allow **Install unknown apps** for your browser or file manager (Settings › Apps › Special app access › Install unknown apps), then go back and tap **Install**.
4. If **Google Play Protect** shows a warning, tap **More details › Install anyway**. The app is open source and built from this repository.
5. Open RikkaHub Ultra. The welcome dialog helps you set up a free provider; you can add your own API keys later in *Settings › Providers*.

Updating: install a newer APK from the same source over the existing app — your data stays. The app also tells you when a new release is available.

> RikkaHub Ultra uses a different app id from the original RikkaHub, so both can be installed side by side. Data is not shared between them; use *Settings › Backup* to move your chats.

## 📚 Documentation

- Documentation: <https://vahid.top/rikkahub-ultra>
- Developer website: <https://vahid.top/>
- Release notes: [CHANGELOG.md](CHANGELOG.md)

## 💚 Support the project

If RikkaHub Ultra is useful to you, you can support its development. Every address below is also in the app
(*Settings › Donate*) with QR codes and copy buttons. **Always send on the network shown next to each address** —
coins sent on another network may be lost.

| Coin | Network | Address |
|------|---------|---------|
| USDT / TRX | TRON (TRC20) | `TEK8V2mfzFhj7S83zp3bgJEPTMj6D45qhZ` |
| BTC | Bitcoin (SegWit) | `bc1qd6k2zap20pqxu2kj9ez9mhsl40d6acjd5nn79p` |
| ETH / USDT | Ethereum (ERC20) | `0x1D1bBD5751625Dd505081Bd3ca8F755952c4eb09` |
| BNB / USDT | BNB Smart Chain (BEP20) | `0x1D1bBD5751625Dd505081Bd3ca8F755952c4eb09` |
| SOL / USDC | Solana | `DdzSTaEhdR5MT6YVfmkZCXfjZbLtNxDVb5qeZ6Uibps5` |

How to send: in your wallet or exchange choose **Withdraw / Send**, pick the coin, select the matching network,
paste the address, check the first and last characters, and send a small test amount first if you are unsure.

Want to appear in the in-app sponsor list? [Open a sponsor listing request](https://github.com/Diamond242/rikkahub-ultra/issues/new?template=sponsor-listing.yml).

## 🛠️ Development

This project is developed with [Android Studio](https://developer.android.com/studio) using Kotlin, Jetpack Compose,
Material 3 Expressive, Koin, Room, DataStore, Navigation 3, OkHttp, Coil and kotlinx.serialization.

```powershell
./gradlew :app:assembleDebug        # GitHub-flavor debug APK
./gradlew testDebugUnitTest         # unit tests
```

> [!TIP]
> You need a `google-services.json` file in the `app` folder to build the app.

## 📄 License

RikkaHub Ultra is licensed under the [GNU AGPL-3.0](LICENSE), like the original
[RikkaHub](https://github.com/rikkahub/rikkahub). All credit for the original app goes to its authors.

## 🙏 Credits

- [RikkaHub](https://github.com/rikkahub/rikkahub) and its contributors
- [Vazirmatn](https://github.com/rastikerdar/vazirmatn) font (OFL)
- [Termux terminal emulator](https://github.com/termux/termux-app) (Apache 2.0)