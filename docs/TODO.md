# RikkaHub Ultra – TODO

Planned follow-ups that need an account or a decision first.
(کارهای بعدی که اول به یک حساب یا تصمیم نیاز دارند.)

## Affiliate providers / ارائه‌دهنده‌های کمیسیون‌دار

- [ ] Re-add providers that pay an affiliate commission (e.g. APIMart, MaruCode, NaAPI, SuiXiang, and others)
      using **our own** affiliate accounts and links. They were removed on 2026-11-26 because the built-in
      entries carried upstream RikkaHub's affiliate links (`gh-rikkahub`, `aff=Rikkahub`, `aff=rikkahub`).
      Add them back in `DefaultProviders.kt` / `RecommendedProviders.kt` with **new ids** (the old ids are
      listed in `REMOVED_BUILT_IN_PROVIDER_IDS` in `SettingsNormalization.kt`).
  - ارائه‌دهنده‌هایی که کمیسیون می‌دهند را بعداً با حساب و لینک کمیسیون خودمان دوباره اضافه کنیم.

## Firebase / فایربیس

- [ ] Create our own Firebase project (package `top.vahid.rikkahubultra`, plus `.debug`), put our own
      `app/google-services.json` locally and in the `GOOGLE_SERVICES_JSON` CI secret, then remove the two
      `firebase_*` meta-data entries in `AndroidManifest.xml` to turn Analytics/Crashlytics on.
      Until then Firebase stays in the code but **disabled** (no data is collected or sent).
  - بعداً پروژهٔ Firebase خودمان را می‌سازیم و فایل google-services.json خودمان را می‌گذاریم؛ تا آن موقع Firebase غیرفعال می‌ماند.
