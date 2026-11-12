package me.rerere.rikkahub.data.datastore

import me.rerere.search.SearchServiceOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemovedSearchServiceMigrationTest {
    @Test
    fun `legacy RikkaHub search service is dropped and selection follows the same entry`() {
        val bing = SearchServiceOptions.BingLocalOptions()
        val tavily = SearchServiceOptions.TavilyOptions()
        val settings = Settings.dummy().copy(
            searchServices = listOf(SearchServiceOptions.RikkaHubOptions(), bing, tavily),
            searchServiceSelected = 2,
        )
        val migrated = settings.withoutRemovedServices()
        assertEquals(listOf(bing, tavily), migrated.searchServices)
        assertEquals(1, migrated.searchServiceSelected)
    }

    @Test
    fun `only legacy service falls back to default`() {
        val settings = Settings.dummy().copy(
            searchServices = listOf(SearchServiceOptions.RikkaHubOptions()),
            searchServiceSelected = 0,
        )
        val migrated = settings.withoutRemovedServices()
        assertEquals(1, migrated.searchServices.size)
        assertTrue(migrated.searchServices.none { it is SearchServiceOptions.RikkaHubOptions })
        assertEquals(0, migrated.searchServiceSelected)
    }
}
