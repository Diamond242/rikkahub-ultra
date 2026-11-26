package me.rerere.rikkahub.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionTest {

    @Test
    fun `basic version comparison`() {
        assertTrue(Version("1.0.0") < Version("2.0.0"))
        assertTrue(Version("1.0.0") < Version("1.1.0"))
        assertTrue(Version("1.0.0") < Version("1.0.1"))
        assertEquals(0, Version("1.0.0").compareTo(Version("1.0.0")))
    }

    @Test
    fun `different length versions`() {
        assertEquals(0, Version("1.0").compareTo(Version("1.0.0")))
        assertTrue(Version("1.0") < Version("1.0.1"))
    }

    @Test
    fun `prerelease has lower precedence than release`() {
        assertTrue(Version("1.0.0-alpha") < Version("1.0.0"))
        assertTrue(Version("1.0.0-beta") < Version("1.0.0"))
        assertTrue(Version("1.0.0-rc.1") < Version("1.0.0"))
    }

    @Test
    fun `prerelease ordering`() {
        assertTrue(Version("1.0.0-alpha") < Version("1.0.0-beta"))
        assertTrue(Version("1.0.0-beta") < Version("1.0.0-rc"))
        assertTrue(Version("1.0.0-alpha.1") < Version("1.0.0-alpha.2"))
    }

    @Test
    fun `prerelease with more fields has higher precedence`() {
        assertTrue(Version("1.0.0-alpha") < Version("1.0.0-alpha.1"))
    }

    @Test
    fun `numeric prerelease identifiers compared as numbers`() {
        assertTrue(Version("1.0.0-alpha.2") < Version("1.0.0-alpha.10"))
    }

    @Test
    fun `numeric identifiers have lower precedence than string`() {
        assertTrue(Version("1.0.0-1") < Version("1.0.0-alpha"))
    }

    @Test
    fun `build metadata is ignored`() {
        assertEquals(0, Version("1.0.0+build1").compareTo(Version("1.0.0+build2")))
        assertEquals(0, Version("1.0.0-alpha+build").compareTo(Version("1.0.0-alpha")))
    }

    @Test
    fun `semver full precedence chain`() {
        // From SemVer spec: 1.0.0-alpha < 1.0.0-alpha.1 < 1.0.0-beta < 1.0.0-beta.2 < 1.0.0-rc.1 < 1.0.0
        val versions = listOf(
            Version("1.0.0-alpha"),
            Version("1.0.0-alpha.1"),
            Version("1.0.0-beta"),
            Version("1.0.0-beta.2"),
            Version("1.0.0-rc.1"),
            Version("1.0.0"),
        )
        for (i in 0 until versions.size - 1) {
            assertTrue(
                "${versions[i].value} should be < ${versions[i + 1].value}",
                versions[i] < versions[i + 1]
            )
        }
    }

    @Test
    fun `companion compare function`() {
        assertTrue(Version.compare("1.0.0", "2.0.0") < 0)
        assertTrue(Version.compare("2.0.0", "1.0.0") > 0)
        assertEquals(0, Version.compare("1.0.0", "1.0.0"))
    }

    @Test
    fun `string extension operators`() {
        assertTrue("1.0.0" < Version("2.0.0"))
        assertTrue(Version("2.0.0") > "1.0.0")
    }

    @Test
    fun `fork releases rank above their upstream base and below the next upstream`() {
        assertTrue(Version("3.1.1-ultra.1") < Version("3.1.1-ultra.2"))
        assertTrue(Version("3.1.1-ultra.2") > Version("3.1.1-ultra.1"))
        assertTrue(Version("3.1.1") < Version("3.1.1-ultra.1"))
        assertTrue(Version("3.1.1-rc.1") < Version("3.1.1-ultra.1"))
        assertTrue(Version("3.1.1-ultra.9") < Version("3.1.2"))
        assertTrue(Version("3.1.1-ultra.9") < Version("3.1.2-ultra.1"))
        assertTrue(Version("3.1.1-ultra.2") < Version("3.1.1-ultra.10"))
        assertEquals(0, Version("v3.1.1-ultra.2").compareTo(Version("3.1.1-ultra.2")))
        assertEquals(0, Version("3.1.1-ULTRA.2").compareTo(Version("3.1.1-ultra.2")))
    }

    @Test
    fun `prerelease of a fork release is lower than the fork release`() {
        assertTrue(Version("3.1.1-ultra.2.beta.1") < Version("3.1.1-ultra.2"))
        assertTrue(Version("3.1.1-ultra.1") < Version("3.1.1-ultra.2.beta.1"))
    }

    @Test
    fun `tags from our GitHub releases are normalized`() {
        assertEquals("3.1.1-ultra.2", normalizeTag("v3.1.1-ultra.2"))
        assertEquals("3.1.1-ultra.2", normalizeTag(" V3.1.1-ultra.2 "))
        assertEquals(
            "v3.1.1-ultra.2",
            releaseTagFromUrl("https://github.com/Diamond242/rikkahub-ultra/releases/tag/v3.1.1-ultra.2")
        )
        assertEquals(
            "v3.1.1-ultra.2",
            releaseTagFromUrl("https://github.com/Diamond242/rikkahub-ultra/releases/tag/v3.1.1-ultra.2/?x=1")
        )
        // Upstream or unrelated pages are never accepted
        assertEquals(null, releaseTagFromUrl("https://github.com/rikkahub/rikkahub/releases/tag/3.1.2"))
        assertEquals(null, releaseTagFromUrl("https://github.com/Diamond242/rikkahub-ultra/releases"))
        assertEquals(null, releaseTagFromUrl("https://github.com/login"))
    }

    @Test
    fun `update source is our repository only`() {
        assertEquals(
            "https://api.github.com/repos/Diamond242/rikkahub-ultra/releases/latest",
            AppLinks.LATEST_RELEASE_API
        )
        assertEquals("https://github.com/Diamond242/rikkahub-ultra/releases/latest", AppLinks.LATEST_RELEASE_PAGE)
    }
}
