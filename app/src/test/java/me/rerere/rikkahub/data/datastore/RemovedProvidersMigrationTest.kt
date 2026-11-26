package me.rerere.rikkahub.data.datastore

import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ProviderSetting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.uuid.Uuid

class RemovedProvidersMigrationTest {
    private val apimart = Uuid.parse("2a05506f-3a59-450a-a493-33a82bc85a81")
    private val suixiang = Uuid.parse("aecf04fd-cb5c-4582-aed2-e8bf393923fd")
    private val marucode = Uuid.parse("afbc54ad-807e-4455-9594-7d7a546356ad")

    @Test
    fun `removed providers are no longer built in or recommended`() {
        val ids = DEFAULT_PROVIDERS.map { it.id }.toSet()
        REMOVED_BUILT_IN_PROVIDER_IDS.forEach { assertFalse(it in ids) }
        assertTrue(RECOMMENDED_PROVIDERS.isEmpty())
        val urls = DEFAULT_PROVIDERS.filterIsInstance<ProviderSetting.ApiKey>().map { it.baseUrl }
        listOf("apimart", "sui-xiang", "muteki", "naapi").forEach { host ->
            assertTrue(urls.none { it.contains(host) })
        }
    }

    @Test
    fun `unused copies are dropped and configured ones are kept as custom providers`() {
        val unused = ProviderSetting.OpenAI(id = apimart, name = "APIMart", baseUrl = "https://api.apimart.ai/v1")
        val withKey = ProviderSetting.OpenAI(id = suixiang, name = "SuiXiang", apiKey = "sk-test")
        val withModels = ProviderSetting.OpenAI(id = marucode, name = "MaruCode", models = listOf(Model(modelId = "gpt-x")))
        val other = ProviderSetting.OpenAI(name = "Mine")
        val settings = Settings.dummy().copy(providers = listOf(unused, withKey, withModels, other))

        val migrated = settings.withoutRemovedServices().withBuiltInDefaults()
        val ids = migrated.providers.map { it.id }
        assertFalse(apimart in ids)
        assertTrue(suixiang in ids)
        assertTrue(marucode in ids)
        assertTrue(other.id in ids)
        assertFalse(migrated.providers.first { it.id == suixiang }.builtIn)
        assertEquals("sk-test", (migrated.providers.first { it.id == suixiang } as ProviderSetting.OpenAI).apiKey)
    }

    @Test
    fun `settings without removed providers are unchanged`() {
        val settings = Settings.dummy().copy(providers = listOf(ProviderSetting.OpenAI(name = "Mine")))
        assertEquals(settings, settings.withoutRemovedProviders())
    }

    @Test
    fun `normalization of old settings does not crash`() {
        val settings = Settings.dummy().copy(
            providers = listOf(ProviderSetting.OpenAI(id = apimart), ProviderSetting.OpenAI(id = marucode, apiKey = "k"))
        )
        val normalized = settings.normalized()
        assertFalse(normalized.providers.any { it.id == apimart })
        assertTrue(normalized.providers.any { it.id == marucode })
    }
}
