package me.rerere.rikkahub.data.datastore

import me.rerere.ai.provider.ProviderSetting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultProvidersTest {
    @Test
    fun `default providers should include vercel ai gateway with expected balance config`() {
        val vercelProviders = DEFAULT_PROVIDERS
            .filterIsInstance<ProviderSetting.OpenAI>()
            .filter { it.name == "Vercel AI Gateway" }

        assertEquals(1, vercelProviders.size)

        val provider = vercelProviders.single()
        assertEquals("https://ai-gateway.vercel.sh/v1", provider.baseUrl)
        assertFalse(provider.enabled)
        assertTrue(provider.builtIn)
        assertTrue(provider.quotaQuery.enabled)
        assertEquals("{{baseUrl}}/credits", provider.quotaQuery.url)
        assertEquals("balance", provider.quotaQuery.resultPath)
    }
    @Test
    fun `default provider ids should be unique`() {
        val ids = DEFAULT_PROVIDERS.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `provider metadata should only reference default providers`() {
        val ids = DEFAULT_PROVIDERS.map { it.id }.toSet()
        PROVIDER_META.keys.forEach { assertTrue("unknown id $it", it in ids) }
    }

    @Test
    fun `new free and iranian providers should be disabled built-ins with expected urls`() {
        val expected = mapOf(
            "Groq" to "https://api.groq.com/openai/v1",
            "NVIDIA NIM" to "https://integrate.api.nvidia.com/v1",
            "Mistral" to "https://api.mistral.ai/v1",
            "Cohere" to "https://api.cohere.ai/compatibility/v1",
            "Ollama" to "http://localhost:11434/v1",
            "AvalAI" to "https://api.avalai.ir/v1",
            "GapGPT" to "https://api.gapgpt.app/v1",
            "Metis" to "https://api.metisai.ir/openai/v1",
        )
        expected.forEach { (name, url) ->
            val p = DEFAULT_PROVIDERS.filterIsInstance<ProviderSetting.OpenAI>().single { it.name == name }
            assertEquals(url, p.baseUrl)
            assertFalse(p.enabled)
            assertTrue(p.builtIn)
            assertTrue(p.meta != null)
        }
        val anthropic = DEFAULT_PROVIDERS.filterIsInstance<ProviderSetting.Claude>().single { it.name == "Anthropic" }
        assertFalse(anthropic.enabled)
    }

    @Test
    fun `filters should select by tier and vpn flag`() {
        val metis = DEFAULT_PROVIDERS.single { it.name == "Metis" }
        val groq = DEFAULT_PROVIDERS.single { it.name == "Groq" }
        val ollama = DEFAULT_PROVIDERS.single { it.name == "Ollama" }
        assertTrue(metis.matches(ProviderFilter.NO_VPN))
        // Metis is paid but gives a free signup quota, so it shows up under the Free filter
        assertEquals(ProviderTier.FREE_QUOTA, metis.meta!!.tier)
        assertTrue(metis.matches(ProviderFilter.FREE))
        val paid = DEFAULT_PROVIDERS.first { it.meta?.tier == ProviderTier.PAID }
        assertFalse(paid.matches(ProviderFilter.FREE))
        assertTrue(groq.matches(ProviderFilter.FREE))
        assertFalse(groq.matches(ProviderFilter.NO_VPN))
        assertTrue(groq.matches(ProviderFilter.ALL))
        assertTrue(ollama.meta!!.noKeyRequired)
        assertTrue(ollama.matches(ProviderFilter.NO_VPN))
    }}
