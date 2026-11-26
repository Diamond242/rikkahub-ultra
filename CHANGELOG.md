# Changelog · تغییرات

Release notes of **RikkaHub Ultra**. The app shows this file in *Settings › About › Release notes*;
the English part is shown for every language except Persian, which gets the Persian part.
GitHub release tags use the form `v<versionName>` (for example `v3.1.1-ultra.1`).

یادداشت‌های انتشار **RikkaHub Ultra**. این فایل در «تنظیمات › درباره › یادداشت‌های انتشار» نمایش داده می‌شود.

<!-- lang:en -->
## 3.1.1-ultra.3

**Independence from upstream**
- Update checks read only this repository's GitHub releases. Versions like `3.1.1-ultra.3` are compared correctly (newer than `3.1.1`, older than `3.1.2`), and when GitHub's API rate limit is hit the app reads the public release page instead of showing an error.
- Firebase Analytics and Crashlytics are turned off: the app collects and sends no usage or crash data.
- Removed the built-in providers that carried upstream's affiliate links (APIMart, SuiXiang, MaruCode, NaAPI). If you had set one up with a key or models, it stays in your list as a normal custom provider.
- The app's own link scheme is now `rikkahub-ultra://`, so it no longer clashes with the original RikkaHub.
- MCP servers with OAuth sign-in now see the app as "RikkaHub Ultra"; its client information is hosted by this project.
- Cleanup: removed unused Afdian, QQ group and Discord leftovers.

## 3.1.1-ultra.2

- Fixed a garbled line in the Persian release notes.
- Release builds are now signed with the permanent RikkaHub Ultra key.

## 3.1.1-ultra.1

First release of the fork, based on RikkaHub 3.1.1.

**Identity**
- New app id `top.vahid.rikkahubultra`: installs next to the original RikkaHub (settings are not shared).
- New name *RikkaHub Ultra* with a green "Ultra" mark, and a refreshed minimal robot icon.
- New About page: developer, our GitHub, documentation, license, release notes and a link to the original RikkaHub.
- In-app updates now come from the GitHub releases of this repository, with localized release notes.
- New donate page (crypto with QR codes and network guides) and our own sponsor list.
- Removed upstream's free RikkaHub AI service and RikkaHub search (api.rikka-ai.com); old settings and backups drop them automatically.

**Persian & languages**
- Complete Persian translation of the app and the web UI, full right-to-left layout and the Vazirmatn font.
- In-app language picker (follows the system by default).
- Hard-coded Chinese texts moved to translatable resources.

**Providers & models**
- 15 extra providers (free, trial and Iranian ones such as AvalAI, GapGPT and Metis) with Free / Free quota / Trial / Paid / No-VPN badges and filters.
- "Fetch models" button with clear errors, "Add all free models", sort by quality, persistent sort mode, favorites and manual ordering.
- Recent models, quick filter chips (free, no VPN, vision, reasoning, tools) and context-size badges in the model picker.
- Step-by-step guides for getting an API key, and a key-test button.

**Easier to use**
- First-run welcome dialog that sets up a free provider in about a minute.
- Human-readable error messages with a shortcut to provider settings.
- Settings search, a clear Settings entry in the drawer, simpler chat defaults and a calmer message jumper.
- Named built-in assistants (Simple / Smart, Smart is the default) with an info button; visible delete action for assistants.

<!-- lang:fa -->
## ۳.۱.۱-ultra.3

**استقلال از نسخهٔ اصلی**
- بررسی به‌روزرسانی فقط از انتشارهای گیت‌هاب همین مخزن انجام می‌شود. نسخه‌هایی مثل `3.1.1-ultra.3` درست مقایسه می‌شوند (جدیدتر از `3.1.1` و قدیمی‌تر از `3.1.2`). اگر سقف درخواست‌های API گیت‌هاب پر شود، برنامه به‌جای نمایش خطا، صفحهٔ عمومی انتشار را می‌خواند.
- Firebase Analytics و Crashlytics خاموش شدند. برنامه هیچ دادهٔ استفاده یا گزارش خطایی جمع نمی‌کند و نمی‌فرستد.
- ارائه‌دهنده‌های داخلی که لینک کمیسیون نسخهٔ اصلی را داشتند حذف شدند (APIMart، SuiXiang، MaruCode، NaAPI). اگر یکی از آن‌ها را با کلید یا مدل تنظیم کرده بودید، به‌صورت ارائه‌دهندهٔ سفارشی در فهرستتان می‌ماند.
- طرح لینک اختصاصی برنامه اکنون `rikkahub-ultra://` است و دیگر با RikkaHub اصلی تداخل ندارد.
- سرورهای MCP که ورود OAuth دارند، اکنون برنامه را با نام «RikkaHub Ultra» می‌شناسند. اطلاعات کلاینت روی سایت خود پروژه میزبانی می‌شود.
- پاک‌سازی: باقی‌ماندهٔ بی‌استفادهٔ Afdian، گروه QQ و Discord حذف شد.

## ۳.۱.۱-ultra.2

- رفع خطای نمایش یک خط خراب در یادداشت‌های انتشار فارسی.
- نسخه‌های انتشار اکنون با کلید دائمی RikkaHub Ultra امضا می‌شوند.

## ۳.۱.۱-ultra.1

نخستین انتشار این نسخه، بر پایهٔ RikkaHub 3.1.1.

**هویت**
- شناسهٔ تازهٔ برنامه `top.vahid.rikkahubultra`: کنار RikkaHub اصلی نصب می‌شود (تنظیمات مشترک نیست).
- نام تازهٔ *RikkaHub Ultra* با نشان سبز «Ultra» و آیکون ربات مینیمال و تازه.
- صفحهٔ «درباره» تازه: توسعه‌دهنده، گیت‌هاب پروژه، مستندات، مجوز، یادداشت‌های انتشار و لینک RikkaHub اصلی.
- به‌روزرسانی داخل برنامه از انتشارهای گیت‌هاب همین مخزن، با یادداشت‌های انتشار فارسی.
- صفحهٔ تازهٔ حمایت مالی (ارز دیجیتال با کد QR و راهنمای شبکه، و کارت بانکی) و فهرست حامیان مخصوص این پروژه.
- سرویس هوش مصنوعی رایگان و جست‌وجوی RikkaHub اصلی (api.rikka-ai.com) حذف شد؛ تنظیمات و پشتیبان‌های قدیمی به‌طور خودکار آن‌ها را کنار می‌گذارند.

**فارسی و زبان‌ها**
- ترجمهٔ کامل فارسی برنامه و رابط وب، چیدمان کامل راست‌به‌چپ و فونت وزیرمتن.
- انتخاب زبان داخل برنامه (به‌طور پیش‌فرض از زبان سیستم پیروی می‌کند).
- انتقال متن‌های چینیِ نوشته‌شده در کد به فایل‌های قابل ترجمه.

**ارائه‌دهنده‌ها و مدل‌ها**
- ۱۵ ارائه‌دهندهٔ تازه (رایگان، آزمایشی و ایرانی مثل اول‌ای‌آی، گپ‌جی‌پی‌تی و متیس) با نشان‌ها و فیلترهای رایگان، سهمیهٔ رایگان، آزمایشی، پولی و بدون فیلترشکن.
- دکمهٔ «دریافت مدل‌ها» با پیام خطای روشن، «افزودن همهٔ مدل‌های رایگان»، مرتب‌سازی بر اساس کیفیت، ذخیرهٔ حالت مرتب‌سازی، علاقه‌مندی‌ها و ترتیب دستی.
- مدل‌های اخیر، فیلترهای سریع (رایگان، بدون فیلترشکن، تصویر، استدلال، ابزار) و نشان اندازهٔ حافظهٔ گفتگو در انتخابگر مدل.
- راهنمای گام‌به‌گام دریافت کلید API و دکمهٔ آزمودن کلید.

**کار با برنامه ساده‌تر شد**
- پنجرهٔ خوش‌آمد در اولین اجرا که در حدود یک دقیقه یک ارائه‌دهندهٔ رایگان را راه می‌اندازد.
- پیام‌های خطای قابل‌فهم با میان‌بر رفتن به تنظیمات ارائه‌دهنده.
- جست‌وجو در تنظیمات، ردیف روشن «تنظیمات» در منوی کناری، پیش‌فرض‌های ساده‌تر گفتگو و دکمه‌های پرش آرام‌تر.
- دستیارهای داخلی با نام «ساده» و «هوشمند» (هوشمند پیش‌فرض است) و دکمهٔ «i»؛ دکمهٔ حذف دستیار در دسترس.
<!-- /lang -->