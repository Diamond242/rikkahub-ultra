# فهرست سرویس‌ها و APIهای پشتیبانی‌شده در RikkaHub

> این فهرست مستقیماً از روی کد شاخهٔ `fa-translation` استخراج شده است (۷ آبان ۱۴۰۵ / 2026-10-28).
> - **طرح رایگان:** هر جا در کد ذکر نشده باشد و در مستندات رسمی هم بررسی نشده باشد، «نامشخص» نوشته شده است.
> - **دسترسی از ایران:** همهٔ یادداشت‌های این ستون حدسی‌اند و باید آزمایش شوند («بررسی شود»). سرویس‌های آمریکایی معمولاً IP ایران را مسدود می‌کنند، سرویس‌های چینی گاهی شماره تلفن چینی یا احراز هویت می‌خواهند، و پرداخت بین‌المللی برای کاربران ایرانی مانع اصلی است. برای این موارد، تنظیم پروکسی داخل برنامه (`proxyUrl`) وجود دارد.

## خلاصهٔ شمارش

| دسته | تعداد |
|---|---|
| انواع پروتکل LLM (OpenAI Chat/Responses، Gemini/Vertex/Interactions، Anthropic، ChatGPT-OAuth، Grok-OAuth) | ۵ نوع / ۸ حالت API |
| ارائه‌دهندگان LLM پیش‌فرض (DefaultProviders) | ۲۰ |
| ارائه‌دهندگان پیشنهادی (RecommendedProviders؛ ۳ مورد تکراری) | ۴ (۱ مورد جدید: NaAPI) |
| حساب‌های OAuth برای LLM | ۲ (ChatGPT، Grok) |
| موتورهای جستجو و خواندن وب | ۲۰ |
| ارائه‌دهندگان TTS (تبدیل متن به گفتار) | ۱۲ |
| ارائه‌دهندگان ASR/STT (تبدیل گفتار به متن) | ۵ |
| ارائه‌دهندگان تولید تصویر و ویدیو | ۵ |
| انتقال MCP | ۲ (SSE، Streamable HTTP) + OAuth |
| پشتیبان‌گیری و همگام‌سازی | ۲ (WebDAV، S3) + ۲ واردکننده |
| سایر موارد (SSH، وب‌سرور، پروکسی، models.dev، ترجمه، OCR، اسپانسر) | ۷ |

---

## ۱. پروتکل‌های LLM (نوع ارائه‌دهنده)

| نوع | احراز هویت | آدرس پیش‌فرض | فیلدهای تنظیمات | توانایی‌ها | محل کد |
|---|---|---|---|---|---|
| OpenAI (Chat Completions) | API key | `https://api.openai.com/v1` | apiKey، baseUrl، chatCompletionsPath (`/chat/completions`)، includeHistoryReasoning، customHeaders، quotaQuery | چت، استریم، بینایی، ابزار، reasoning، دریافت فهرست مدل‌ها، ویرایش تصویر | `ai/.../providers/openai/ChatCompletionsAPI.kt` |
| OpenAI (Responses API) | API key | همان | useResponseApi=true، responsesPath (`/responses`) | چت، ابزار، reasoning، ابزارهای داخلی | `ai/.../openai/ResponseAPI.kt` |
| Google Gemini | API key (`x-goog-api-key`) | `https://generativelanguage.googleapis.com/v1beta` | apiKey، baseUrl، useInteractionsApi | چت، بینایی، ابزار، thinking، Google Search و URL Context داخلی، embeddings | `ai/.../google/GoogleProvider.kt`، `InteractionsAPI.kt` |
| Google Vertex AI | API key یا Service Account (JWT → `oauth2.googleapis.com/token`) | `https://aiplatform.googleapis.com/v1/projects/{p}/locations/{l}` | vertexAI، useServiceAccount، serviceAccountEmail، privateKey، projectId، location (`us-central1`) | مانند Gemini | `ai/.../google/vertex/ServiceAccountTokenProvider.kt` |
| Anthropic (Claude) | API key (`x-api-key`) | `https://api.anthropic.com/v1` | apiKey، baseUrl، promptCaching، promptCacheTtl (5 دقیقه/1 ساعت) | چت، بینایی، ابزار، extended thinking، prompt cache | `ai/.../claude/ClaudeProvider.kt` |
| ChatGPT (حساب) | OAuth با PKCE و بازگشت loopback (`auth.openai.com`) | `https://chatgpt.com/backend-api/codex` (+ `/wham/usage` برای سهمیه) | فقط ورود؛ آدرس ثابت است | چت و reasoning با اشتراک ChatGPT (Codex backend) | `ai/.../chatgpt/ChatGPTProvider.kt`، `app/.../account/ChatGPTAuth.kt` |
| Grok (حساب) | OAuth با Device Code (RFC 8628، `auth.x.ai`) | `https://cli-chat-proxy.grok.com/v1` | فقط ورود | چت و reasoning با حساب xAI/Grok | `ai/.../grok/GrokProvider.kt`، `app/.../account/GrokAuth.kt` |

> نکته: انواعی که کاربر می‌تواند دستی بسازد فقط OpenAI، Google و Claude هستند (`ProviderSetting.Types`). ChatGPT و Grok از طریق «ورود با حساب» اضافه می‌شوند. ارائه‌دهندهٔ رسمی Anthropic در فهرست پیش‌فرض نیست و کاربر باید آن را دستی بسازد.

## ۲. ارائه‌دهندگان LLM پیش‌فرض (`app/.../data/datastore/DefaultProviders.kt`)

| # | نام | فرمت | آدرس پایه | احراز | طرح رایگان | صفحهٔ ثبت‌نام / کلید | ایران (بررسی شود) |
|---|---|---|---|---|---|---|---|
| 1 | OpenAI | OpenAI | `https://api.openai.com/v1` | API key | نامشخص | https://platform.openai.com/api-keys | IP ایران مسدود است |
| 2 | Gemini | Google | `https://generativelanguage.googleapis.com/v1beta` | API key | نامشخص (در کد نیامده) | https://aistudio.google.com/apikey | IP ایران مسدود است |
| 3 | AiHubMix | OpenAI | `https://aihubmix.com/v1` | API key | نامشخص | https://aihubmix.com | واسط؛ احتمالاً قابل دسترس |
| 4 | APIMart | OpenAI | `https://api.apimart.ai/v1` | API key | نامشخص | https://apimart.ai | واسط |
| 5 | SiliconFlow (硅基流动) | OpenAI | `https://api.siliconflow.cn/v1` | API key | نامشخص | https://cloud.siliconflow.cn/account/ak | شماره چینی لازم است |
| 6 | DeepSeek | OpenAI | `https://api.deepseek.com/v1` | API key | نامشخص | https://platform.deepseek.com/api_keys | پرداخت دشوار است |
| 7 | Moonshot / Kimi (月之暗面) | OpenAI | `https://api.moonshot.cn/v1` | API key | نامشخص | https://platform.moonshot.cn/console/api-keys | شماره چینی لازم است |
| 8 | OpenRouter | OpenAI | `https://openrouter.ai/api/v1` | API key | نامشخص | https://openrouter.ai/keys | پرداخت با کریپتو ممکن است |
| 9 | Vercel AI Gateway | OpenAI | `https://ai-gateway.vercel.sh/v1` | API key | نامشخص | https://vercel.com/dashboard | بررسی شود |
| 10 | Alibaba Bailian (阿里云百炼) | OpenAI | `https://dashscope.aliyuncs.com/compatible-mode/v1` | API key | نامشخص | https://bailian.console.aliyun.com | احراز هویت چینی لازم است |
| 11 | Volcengine Ark (火山引擎) | OpenAI | `https://ark.cn-beijing.volces.com/api/v3` | API key | نامشخص | https://console.volcengine.com/ark | احراز هویت چینی لازم است |
| 12 | Zhipu (智谱) | OpenAI | `https://open.bigmodel.cn/api/paas/v4` | API key | نامشخص | https://bigmodel.cn/usercenter/proj-mgmt/apikeys | بررسی شود |
| 13 | StepFun (阶跃星辰) | OpenAI | `https://api.stepfun.com/v1` | API key | نامشخص | https://platform.stepfun.com | بررسی شود |
| 14 | 302.AI | OpenAI | `https://api.302.ai/v1` | API key | نامشخص | https://302.ai | واسط |
| 15 | Tencent Hunyuan (腾讯) | OpenAI | `https://tokenhub.tencentmaas.com/v1` | API key | نامشخص | https://console.cloud.tencent.com/hunyuan | بررسی شود |
| 16 | xAI | OpenAI | `https://api.x.ai/v1` | API key | نامشخص | https://console.x.ai/ | IP ایران احتمالاً مسدود است |
| 17 | SuiXiang AI Gateway (随想AI网关) | OpenAI | `https://sui-xiang.com/v1` | API key | نامشخص | https://sui-xiang.com | واسط |
| 18 | MaruCode | OpenAI | `https://api.muteki.site/v1` | API key | نامشخص | https://api.muteki.site | واسط |
| 19 | MiniMax | **Anthropic** | `https://api.minimaxi.com/anthropic/v1` | API key | نامشخص | https://platform.minimaxi.com | بررسی شود |
| 20 | Xiaomi MiMo | OpenAI | `https://api.xiaomimimo.com/v1` | API key | نامشخص | https://platform.xiaomimimo.com | بررسی شود |

ارائه‌دهندگان پیشنهادی (`RecommendedProviders.kt`): APIMart، SuiXiang، MaruCode و **NaAPI (钠API)** با آدرس `https://naapi.cc/v1`. همه از فرمت OpenAI استفاده می‌کنند.

توانایی‌ها: در همهٔ این ارائه‌دهندگان، توانایی هر مدل (بینایی، ابزار، reasoning، تصویر، embeddings) در سطح مدل تعیین می‌شود. این اطلاعات از `models.dev` یا دستی تنظیم می‌شود و ارائه‌دهنده آن را تعیین نمی‌کند.

## ۳. جستجو و خواندن وب (`search/src/main/java/me/rerere/search/`)

| نام | احراز | Endpoint | فیلدها | قابلیت | ثبت‌نام |
|---|---|---|---|---|---|
| Bing (محلی، scraping) | ندارد | `https://www.bing.com/search` | — | جستجو | — |
| Zhipu | API key | `open.bigmodel.cn/api/paas/v4/web_search` | apiKey | جستجو | https://bigmodel.cn/usercenter/proj-mgmt/apikeys |
| Doubao (Volcengine) | API key | `open.feedcoopapi.com/search_api/...` | apiKey | جستجو | https://console.volcengine.com/search-infinity/api-key |
| Tavily | API key | `api.tavily.com/search`، `/extract` | apiKey، depth | جستجو و استخراج | https://app.tavily.com/home |
| Exa | API key | `api.exa.ai/search`، `/contents` | apiKey | جستجو و محتوا | https://dashboard.exa.ai/api-keys |
| SearXNG | اختیاری (Basic) | آدرس سرور خود کاربر | url، engines، language، username/password | جستجو | خودمیزبان |
| LinkUp | API key | `api.linkup.so/v1/search`، `/fetch` | apiKey، depth | جستجو و fetch | https://www.linkup.so/ |
| Brave | API key | `api.search.brave.com/res/v1/web/search` | apiKey | جستجو | https://api.search.brave.com/ |
| Metaso (秘塔) | API key | `metaso.cn/api/v1/search` | apiKey | جستجو | https://metaso.cn/ |
| Ollama Web Search | API key | `ollama.com/api/web_search`، `/web_fetch` | apiKey | جستجو و fetch | https://ollama.com/settings/keys |
| Perplexity | API key | `api.perplexity.ai/search` | apiKey | جستجو | https://www.perplexity.ai/settings/api |
| Firecrawl | API key | `api.firecrawl.dev/v2/search`، `/scrape` | apiKey | جستجو و scrape | https://firecrawl.dev |
| Jina | API key | `s.jina.ai`، `r.jina.ai` | apiKey، searchUrl، scrapeUrl | جستجو و خواندن صفحه | https://jina.ai/ |
| Bocha (博查) | API key | `api.bochaai.com/v1/web-search` | apiKey | جستجو | https://open.bochaai.com/ |
| Grok Search | API key | `api.x.ai/v1/responses` (قابل تغییر) | apiKey، model، customUrl، systemPrompt | جستجوی مبتنی بر LLM | https://console.x.ai/ |
| Tinyfish | API key | `api.search.tinyfish.ai`، `api.fetch.tinyfish.ai` | apiKey | جستجو و fetch | https://agent.tinyfish.ai/api-keys |
| Serper (Google) | API key | `google.serper.dev/search` | apiKey | جستجو | https://serper.dev/api-keys |
| DeepSeek Search | API key | `api.deepseek.com/v1/tools/search`، `/fetch` | apiKey | جستجو و fetch | https://platform.deepseek.com/api_keys |
| Custom JS | — | اسکریپت کاربر | name، searchScript، scrapeScript | جستجو و scrape سفارشی | — |

همچنین در Gemini، ابزارهای داخلی Google Search و URL Context از طریق خود مدل در دسترس‌اند.

## ۴. TTS – تبدیل متن به گفتار (`speech/.../tts/provider/`)

| نام | احراز | آدرس پایه | فیلدها | ثبت‌نام |
|---|---|---|---|---|
| OpenAI | API key | `https://api.openai.com/v1` | apiKey، baseUrl، model، voice | platform.openai.com/api-keys |
| Gemini | API key | `generativelanguage.googleapis.com/v1beta` | apiKey، model، voiceName | aistudio.google.com/apikey |
| System TTS (اندروید) | ندارد | — (موتور TTS دستگاه) | — | — |
| MiniMax | API key | `https://api.minimaxi.com/v1` | apiKey، model، voiceId | platform.minimaxi.com |
| Qwen (DashScope/Bailian) | API key | `https://{WorkspaceId}.cn-beijing.maas.aliyuncs.com/api/v1` | apiKey، baseUrl، model، voice، format | bailian.console.aliyun.com |
| Groq | API key | `https://api.groq.com/openai/v1` | apiKey، model، voice | https://console.groq.com/keys |
| xAI | API key | `https://api.x.ai/v1` | apiKey، voiceId، language | console.x.ai |
| Xiaomi MiMo | API key | `https://api.xiaomimimo.com/v1` | apiKey، model، voice | platform.xiaomimimo.com |
| ElevenLabs | API key | `https://api.elevenlabs.io` | apiKey، model، voiceId | https://elevenlabs.io/app/settings/api-keys |
| StepFun | API key | `https://api.stepfun.com` | apiKey، model، voice، responseFormat، instruction | platform.stepfun.com |
| Fish Audio | API key | `https://api.fish.audio` | apiKey، model، referenceId، format، latency | https://fish.audio/app/api-keys |
| Volcengine (Doubao TTS) | API key | `https://openspeech.bytedance.com` | apiKey، resourceId، speaker | console.volcengine.com/speech |

## ۵. ASR / STT – تبدیل گفتار به متن (`speech/.../asr/`)

| نام | احراز | Endpoint | فیلدها | حالت |
|---|---|---|---|---|
| OpenAI Realtime | API key | `wss://api.openai.com/v1/realtime?intent=transcription` | apiKey، websocketUrl، model، language، prompt | استریم WebSocket |
| DashScope (Qwen) | API key | `wss://dashscope.aliyuncs.com/api-ws/v1/realtime` | apiKey، websocketUrl، model، language | استریم |
| Volcengine (Doubao ASR) | API key | `wss://openspeech.bytedance.com/api/v3/sauc/bigmodel(_async)` | apiKey، resourceId، language | استریم باینری |
| Xiaomi MiMo | API key | `https://api.xiaomimimo.com/v1` | apiKey، model، language | HTTP |
| StepFun | API key | `https://api.stepfun.com` (SSE) | apiKey، model، language | SSE |

## ۶. تولید تصویر و ویدیو (`mediagen/`)

| نام | احراز | آدرس پایه | فیلدها | قابلیت |
|---|---|---|---|---|
| OpenAI (و سازگارها) | API key | `https://api.openai.com/v1` | apiKey، baseUrl | تولید و ویرایش تصویر |
| Aliyun (Wan/Qwen-Image) | API key | `https://{workspaceId}.cn-beijing.maas.aliyuncs.com/api/v1` | apiKey، workspaceId، baseUrl | تصویر و ویدیو |
| Volcengine (Seedream/Seedance) | API key | `https://ark.cn-beijing.volces.com/api/v3` | apiKey، baseUrl | تصویر و ویدیو |
| MiniMax (Hailuo) | API key | `https://api.minimaxi.com/v2` | apiKey، baseUrl | تصویر و ویدیو |
| OpenRouter | API key | `https://openrouter.ai/api/v1` | apiKey، baseUrl | تصویر و ویدیو |

## ۷. MCP و OAuth

| مورد | توضیح | محل کد |
|---|---|---|
| MCP – SSE | `SseTransportServer(url, headers)` | `app/.../data/ai/mcp/McpConfig.kt` |
| MCP – Streamable HTTP | `StreamableHTTPServer(url, headers)` | همان |
| MCP – stdio | **پشتیبانی نمی‌شود** (اندروید) | — |
| MCP OAuth | کشف metadata (RFC 8414/9728)، ثبت پویا (DCR) یا Client ID Metadata Document، PKCE، بازگشت loopback | `McpOAuthCoordinator.kt`، `McpOAuthDiscoveryClient.kt`، `oauth/` |
| ChatGPT OAuth | `auth.openai.com/oauth/authorize` و `/oauth/token` با PKCE و loopback | `account/ChatGPTAuth.kt` |
| Grok OAuth | `auth.x.ai/oauth2/device/code` با Device Code | `account/GrokAuth.kt` |

## ۸. سایر سرویس‌ها و زیرساخت

| مورد | احراز | فیلدها | محل کد |
|---|---|---|---|
| فضای کاری SSH (ترمینال و فایل‌ها) | رمز یا کلید خصوصی (+passphrase)، تأیید fingerprint میزبان | host، port (22)، username، authType، password/privateKey، remoteDir | `workspace/.../ssh/` |
| وب‌سرور داخلی (Ktor + web-ui) | رمز دسترسی یا JWT اختیاری | webServerEnabled، port (8080)، jwt، accessPassword، localhostOnly؛ کشف در شبکه با NSD | `app/.../web/` |
| پشتیبان WebDAV | Basic | url، username، password، path | `data/sync/webdav/` |
| پشتیبان S3 (AWS، R2، MinIO و غیره) | AWS SigV4 | endpoint، accessKeyId، secretAccessKey، bucket، region (auto)، pathStyle | `data/sync/s3/` |
| واردکننده‌ها | — | فایل پشتیبان Chatbox و ارائه‌دهندگان Cherry Studio | `data/sync/importer/` |
| پروکسی HTTP | اختیاری | proxyUrl، proxyUsername، proxyPassword | `data/remote/ProxyConfig.kt` |
| models.dev (فهرست مدل‌ها و توانایی‌ها) | ندارد | — | `data/remote/ModelsDevUpdater.kt` |
| ترجمه و OCR | از یک مدل چت انتخابی استفاده می‌کند (API جداگانه ندارد) | translateModeId، prompt، targetLanguage؛ ocrModelId | `Settings.kt`، `OcrTransformer.kt` |
| Sponsors API | ندارد | — | `sponsors.json` (Diamond242/rikkahub-ultra) |

---

## ۹. شکاف‌ها: سرویس‌های پرکاربردی که هنوز پیش‌فرض نیستند

همهٔ موارد زیر چون سازگار با OpenAI هستند **همین الان هم به‌صورت دستی** (نوع OpenAI و تغییر baseUrl) قابل استفاده‌اند. با این حال، نبودِ پیش‌فرض یعنی نه آیکون و توضیح دارند، نه لینک دریافت کلید، و نه آموزش.

| اولویت | سرویس | وضعیت فعلی در کد | آدرس پیشنهادی (بررسی شود) | دلیل |
|---|---|---|---|---|
| ★★★ | **AvalAI** (ایرانی) | ندارد | `https://api.avalai.ir/v1` | پرداخت ریالی، بدون نیاز به پروکسی |
| ★★★ | **GapGPT** (ایرانی) | ندارد | `https://api.gapgpt.app/v1` | پرداخت ریالی |
| ★★★ | **Metis** (ایرانی) | ندارد | `https://api.metisai.ir/openai/v1` | پرداخت ریالی |
| ★★☆ | سایر واسط‌های ایرانی (مثلاً Gilas، Torob AI و غیره) | ندارد | بررسی شود | — |
| ★★★ | Groq (LLM) | فقط در TTS و آیکون | `https://api.groq.com/openai/v1` | سرعت بالا، طرح رایگان (طبق مستندات رسمی؛ بررسی شود) |
| ★★★ | Anthropic رسمی | نوع وجود دارد اما پیش‌فرض نیست | `https://api.anthropic.com/v1` | فقط یک پیش‌فرض با لینک کلید لازم است |
| ★★☆ | Mistral | فقط آیکون | `https://api.mistral.ai/v1` | — |
| ★★☆ | Cohere | ندارد | `https://api.cohere.ai/compatibility/v1` | embeddings و rerank |
| ★★☆ | Kilo Code Gateway | ندارد | بررسی شود | — |
| ★★☆ | Together، Fireworks، Cerebras، SambaNova، Nebius | ندارد | — | مدل‌های باز ارزان |
| ★★☆ | Ollama / LM Studio محلی | فقط آیکون | `http://<ip>:11434/v1` | استفادهٔ آفلاین و در شبکهٔ داخلی |
| ★☆☆ | Azure OpenAI، AWS Bedrock | ندارد | — | به احراز هویت و مسیر متفاوت نیاز دارند |
| ★☆☆ | GitHub Models / Copilot (OAuth) | ندارد | — | مشابه ورود ChatGPT/Grok |
| ★★☆ | جستجو: Google CSE، DuckDuckGo، You.com | ندارد | — | — |
| ★★☆ | TTS/ASR فارسی: Azure Speech، Google Cloud TTS، Whisper (Groq/OpenAI) برای ASR | ASR فقط OpenAI Realtime دارد | — | کیفیت گفتار فارسی |
| ★☆☆ | پشتیبان: Google Drive، Dropbox | ندارد | — | — |

### قابلیت‌های ناقص برای هر سرویس

- **ارائه‌دهندگان واسط/ایرانی:** پرس‌وجوی موجودی (quotaQuery) پیش‌فرض ندارند، و لینک صفحهٔ کلید و توضیح فارسی هم ندارند.
- **Anthropic:** ورود با حساب Claude (OAuth) وجود ندارد. ابزارهای داخلی Anthropic (web search/fetch) بررسی شود.
- **Gemini:** embeddings و تولید تصویر Imagen در mediagen جدا نیستند. Gemini/Imagen به‌عنوان ارائه‌دهندهٔ mediagen وجود ندارد.
- **mediagen:** ارائه‌دهندهٔ Google (Imagen/Veo)، Stability، Replicate و fal.ai ندارد.
- **ASR:** OpenAI Whisper (HTTP غیر‌استریم)، Groq Whisper، Gemini و Deepgram وجود ندارند. همچنین ASR آفلاین یا سیستمی اندروید ندارد.
- **MCP:** stdio پشتیبانی نمی‌شود. اجرای MCP روی فضای کاری SSH ایدهٔ ممکنی است.
- **پشتیبان:** رمزنگاری سمت کاربر قبل از آپلود بررسی شود.
- **web-ui:** زبان فارسی (fa) و RTL ندارد.
- **پیش‌فرض‌های چینی:** برای کاربر ایرانی عملاً بلااستفاده‌اند. پیشنهاد می‌شود ترتیب فهرست بر اساس locale تغییر کند (برای fa: واسط‌های ایرانی و OpenRouter در ابتدا).

---

## ۱۰. برنامهٔ آموزش‌ها (راهنمای مصور فارسی دریافت API key)

قالب مشترک هر راهنما:
۱. معرفی کوتاه و هزینه/طرح رایگان
۲. پیش‌نیازها (ایمیل، شماره، روش پرداخت، نیاز به پروکسی)
۳. ثبت‌نام
۴. شارژ حساب
۵. ساخت کلید
۶. کپی کلید
۷. وارد کردن در RikkaHub (تنظیمات ← ارائه‌دهندگان ← انتخاب یا افزودن ← چسباندن کلید ← دریافت مدل‌ها ← تست)
۸. عیب‌یابی خطاهای 401، 403 و 429 و مشکل منطقه

| سرویس | مراحل خاص | اسکرین‌شات‌های لازم |
|---|---|---|
| AvalAI / GapGPT / Metis | ثبت‌نام با موبایل ایرانی، شارژ ریالی، ساخت کلید، افزودن ارائه‌دهندهٔ OpenAI سفارشی با baseUrl | صفحهٔ ثبت‌نام، درگاه پرداخت، صفحهٔ کلیدها، فرم افزودن در برنامه، فهرست مدل‌ها |
| OpenRouter | ورود با Google/GitHub، شارژ کریپتو، Keys، مدل‌های `:free` | داشبورد Credits، صفحهٔ Keys، فیلتر مدل‌های رایگان |
| Gemini (AI Studio) | ورود با حساب گوگل (با پروکسی)، Get API key، پروژه | aistudio → API keys، دکمهٔ Create، تنظیمات Gemini در برنامه |
| Vertex AI | ساخت پروژه GCP، فعال‌سازی API، Service Account، کلید JSON | کنسول IAM، دانلود JSON، فیلدهای Vertex در برنامه |
| OpenAI / xAI / Anthropic | ثبت‌نام، Billing، Create key | Billing، API keys، کپی کلید |
| ChatGPT / Grok (ورود با حساب) | دکمهٔ ورود، مرورگر یا کد دستگاه، تأیید | صفحهٔ ورود در برنامه، صفحهٔ تأیید، کد دستگاه |
| DeepSeek / Moonshot / Zhipu / SiliconFlow / Bailian / Volcengine / StepFun / MiniMax / MiMo / Hunyuan | ثبت‌نام (احتمالاً شماره چینی)، احراز هویت، ساخت کلید | کنسول هر سرویس (ترجمهٔ برچسب‌های چینی) |
| واسط‌های چینی (AiHubMix، APIMart، 302.AI، SuiXiang، MaruCode، NaAPI) | ثبت‌نام با ایمیل، شارژ، توکن | صفحهٔ توکن، شارژ |
| Groq | console.groq.com، Create API Key | صفحهٔ Keys، تنظیم TTS در برنامه |
| جستجو (Tavily، Exa، Brave، Serper، Jina، Firecrawl، LinkUp، Perplexity، Ollama، Tinyfish) | ثبت‌نام و کپی کلید | داشبورد هر سرویس، صفحهٔ تنظیمات جستجو در برنامه |
| SearXNG | راه‌اندازی خودمیزبان (Docker) یا نمونهٔ عمومی، فعال کردن فرمت JSON | فایل settings.yml، فرم برنامه |
| TTS/ASR (ElevenLabs، Fish Audio، MiniMax، Volcengine، Qwen و غیره) | کلید، شناسهٔ صدا، resourceId یا workspaceId | انتخاب صدا، صفحهٔ کلید، دکمهٔ تست صدا |
| mediagen | کلید و workspaceId (Aliyun) | تنظیمات تولید رسانه، نمونهٔ خروجی |
| MCP | افزودن URL سرور، هدرها، ورود OAuth | فرم افزودن سرور، صفحهٔ OAuth، فهرست ابزارها |
| SSH Workspace | ساخت کلید، افزودن میزبان، تأیید fingerprint | فرم SSH، دیالوگ fingerprint، ترمینال |
| WebDAV / S3 | ساخت bucket یا حساب (مثلاً Cloudflare R2، InfiniCloud)، کلیدها | کنسول R2، فرم پشتیبان، نتیجهٔ تست |
| وب‌سرور | فعال‌سازی، پورت، رمز، باز کردن از کامپیوتر | تنظیمات، صفحهٔ وب در مرورگر |
| پروکسی | وارد کردن آدرس HTTP یا SOCKS | فرم پروکسی |

## ارائه‌دهنده‌های رایگان و ایرانی (فاز ۱ — commit `cfcf2810`)

همه به‌صورت پیش‌فرض **غیرفعال** هستند و شناسهٔ ثابت (uuid5) دارند. برچسب‌ها و فیلتر «همه / رایگان / بدون فیلترشکن» در صفحهٔ تنظیمات › ارائه‌دهنده‌ها نمایش داده می‌شود. متادیتا در `app/.../data/datastore/ProviderMeta.kt` است.

| نام | نوع | Base URL | سطح | بدون کلید | بدون فیلترشکن در ایران |
|---|---|---|---|---|---|
| Groq | OpenAI | `https://api.groq.com/openai/v1` | رایگان | — | — |
| NVIDIA NIM | OpenAI | `https://integrate.api.nvidia.com/v1` | رایگان | — | — |
| Mistral | OpenAI | `https://api.mistral.ai/v1` | رایگان | — | — |
| Cohere | OpenAI | `https://api.cohere.ai/compatibility/v1` | رایگان | — | — |
| Z.ai | OpenAI | `https://api.z.ai/api/paas/v4` | رایگان | — | — |
| SiliconFlow (Global) | OpenAI | `https://api.siliconflow.com/v1` | رایگان | — | — |
| LLM7 | OpenAI | `https://api.llm7.io/v1` | رایگان (توکن رایگان لازم) | — | — |
| Pollinations | OpenAI | `https://gen.pollinations.ai/v1` | رایگان (کلید لازم) | — | — |
| Cerebras | OpenAI | `https://api.cerebras.ai/v1` | اعتبار آزمایشی | — | — |
| SambaNova | OpenAI | `https://api.sambanova.ai/v1` | اعتبار آزمایشی | — | — |
| Anthropic | Claude | `https://api.anthropic.com/v1` | پولی | — | — |
| Ollama (محلی) | OpenAI | `http://localhost:11434/v1` | رایگان | ✅ | ✅ |
| AvalAI | OpenAI | `https://api.avalai.ir/v1` | پولی | — | ✅ |
| GapGPT | OpenAI | `https://api.gapgpt.app/v1` | پولی | — | ✅ |
| Metis | OpenAI | `https://api.metisai.ir/openai/v1` | پولی | — | ✅ |

برچسب‌گذاری موارد موجود: Gemini، OpenRouter، SiliconFlow (cn)، Zhipu = رایگان؛ OpenAI، DeepSeek، xAI = پولی.
حذف‌شده: GitHub Models (از ۳۰ ژوئیهٔ ۲۰۲۶ بازنشسته شد)؛ OpenRouter از قبل موجود بود.
