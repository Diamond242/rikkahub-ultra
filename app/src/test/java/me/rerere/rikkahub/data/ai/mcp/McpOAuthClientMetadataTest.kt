package me.rerere.rikkahub.data.ai.mcp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class McpOAuthClientMetadataTest {
    private fun metadataFile(): File =
        listOf(File("../docs/oauth/client.json"), File("docs/oauth/client.json")).first { it.exists() }

    @Test
    fun `client metadata is hosted by our repository`() {
        assertEquals("https://diamond242.github.io/rikkahub-ultra/oauth/client.json", MCP_OAUTH_CLIENT_METADATA_URL)
    }

    @Test
    fun `hosted document matches the app`() {
        val json = metadataFile().readText()
        assertTrue(json.contains("\"client_id\": \"$MCP_OAUTH_CLIENT_METADATA_URL\""))
        assertTrue(json.contains("\"$MCP_OAUTH_REDIRECT_URI\""))
        assertTrue(json.contains("\"client_name\": \"RikkaHub Ultra\""))
    }
}
