package me.rerere.rikkahub.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChangelogTest {
    private val text = """
        # Title
        <!-- lang:en -->
        ## 1.0
        - hello
        <!-- lang:fa -->
        ## ۱.۰
        - سلام
        <!-- /lang -->
    """.trimIndent()

    @Test
    fun picksRequestedLanguage() {
        assertEquals("## ۱.۰\n- سلام", Changelog.localize(text, "fa"))
        assertEquals("## 1.0\n- hello", Changelog.localize(text, "en"))
    }

    @Test
    fun fallsBackToEnglish() {
        assertEquals("## 1.0\n- hello", Changelog.localize(text, "de"))
    }

    @Test
    fun plainTextIsReturnedAsIs() {
        assertEquals("just notes", Changelog.localize("  just notes \n", "fa"))
    }

    @Test
    fun stripsGithubDivWrappers() {
        val wrapped = """
            <!-- lang:fa -->
            <div dir="rtl">

            ## ۱.۰
            - سلام

            </div>
            <!-- /lang -->
        """.trimIndent()
        assertEquals("## ۱.۰\n- سلام", Changelog.localize(wrapped, "fa"))
    }

    @Test
    fun githubReleaseMapping() {
        val info = GithubRelease(
            tagName = "v3.1.1-ultra.2",
            body = "notes",
            publishedAt = "2026-11-12T10:00:00Z",
            htmlUrl = "https://github.com/x/y/releases/tag/v3.1.1-ultra.2",
            assets = listOf(
                GithubAsset("app-universal.apk", "u", 50L * 1024 * 1024),
                GithubAsset("app-arm64-v8a.apk", "a", 30L * 1024 * 1024),
                GithubAsset("checksums.txt", "c", 10),
            ),
        ).toUpdateInfo()
        assertEquals("3.1.1-ultra.2", info.version)
        assertEquals(listOf("app-arm64-v8a.apk", "app-universal.apk"), info.downloads.map { it.name })
        assertEquals("30.0 MB", info.downloads.first().size)
        assertTrue(Version("3.1.1-ultra.2") > Version("3.1.1-ultra.1"))
        assertTrue(Version("3.1.1-ultra.10") > Version("3.1.1-ultra.9"))
    }
}