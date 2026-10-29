package me.rerere.rikkahub

import me.rerere.rikkahub.ui.components.ai.ModelQualityRanking
import me.rerere.rikkahub.ui.components.ai.isFreeModelId
import me.rerere.rikkahub.ui.components.ai.normalizeModelId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelIdNormalizationTest {
    @Test
    fun stripsProviderPrefixAndCase() {
        assertEquals("gpt-4o", normalizeModelId("openai/GPT-4o"))
        assertEquals("claude-3-5-sonnet", normalizeModelId("anthropic/claude-3.5-sonnet"))
    }

    @Test
    fun stripsDatesAndLatest() {
        assertEquals("gpt-4o", normalizeModelId("gpt-4o-2024-08-06"))
        assertEquals("claude-3-5-sonnet", normalizeModelId("claude-3-5-sonnet-20241022"))
        assertEquals("claude-3-5-sonnet", normalizeModelId("claude-3-5-sonnet-latest"))
        assertEquals("mistral-large", normalizeModelId("mistral-large-2411"))
        assertEquals("gpt-4", normalizeModelId("gpt-4-0613"))
        assertEquals("gemini-2-5-pro-preview", normalizeModelId("gemini-2.5-pro-preview-05-06"))
    }

    @Test
    fun stripsFreeTag() {
        assertEquals("deepseek-r1", normalizeModelId("deepseek/deepseek-r1:free"))
        assertTrue(isFreeModelId("deepseek/deepseek-r1:free"))
        assertFalse(isFreeModelId("deepseek/deepseek-r1"))
    }

    @Test
    fun keepsSizeSuffixes() {
        assertEquals("qwen3-235b-a22b", normalizeModelId("qwen/qwen3-235b-a22b"))
        assertEquals("gpt-4o-mini", normalizeModelId("gpt-4o-mini-2024-07-18"))
    }

    @Test
    fun rankingLookupWithAliasesAndPrefixes() {
        val ranking = ModelQualityRanking.parse(
            """{"source":"t","date":"d","scores":{"gpt-4o":1340,"gpt-4o-mini":1275,"deepseek-v3":1390},
               "aliases":{"deepseek-chat":"deepseek-v3"}}"""
        )
        assertEquals(1340.0, ranking.score("openai/gpt-4o-2024-11-20")!!, 0.0)
        assertEquals(1275.0, ranking.score("gpt-4o-mini")!!, 0.0)
        assertEquals(1390.0, ranking.score("deepseek-chat")!!, 0.0)
        assertNotNull(ranking.score("gpt-4o-search-preview"))
        assertNull(ranking.score("some-unknown-model"))
        assertEquals(ranking.canonicalId("deepseek/deepseek-chat:free"), ranking.canonicalId("deepseek-v3"))
    }
}
