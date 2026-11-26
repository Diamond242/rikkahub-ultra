package me.rerere.rikkahub.data.datastore

import me.rerere.ai.provider.ProviderSetting

/**
 * 推荐的提供商列表，在提供商设置页右上角的推荐 Sheet 中展示。
 */
// RikkaHub Ultra: upstream's recommended providers (APIMart, SuiXiang, MaruCode, NaAPI) carried upstream's
// affiliate links and were removed. TODO (docs/TODO.md): re-add affiliate providers with our own accounts.
// The recommend button in the provider page is hidden while this list is empty.
val RECOMMENDED_PROVIDERS: List<ProviderSetting> = emptyList()
