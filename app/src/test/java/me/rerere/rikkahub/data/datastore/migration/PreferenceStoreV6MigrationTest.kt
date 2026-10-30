package me.rerere.rikkahub.data.datastore.migration

import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.runBlocking
import me.rerere.rikkahub.data.datastore.DEFAULT_ASSISTANT_ID
import me.rerere.rikkahub.data.datastore.PLAIN_ASSISTANT_ID
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.datastore.toSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PreferenceStoreV6MigrationTest {
    private val migration = PreferenceStoreV6Migration()

    @Test
    fun `fresh install gets the smart assistant`() = runBlocking {
        val migrated = migration.migrate(mutablePreferencesOf(SettingsStore.VERSION to 5))
        assertNull(migrated[SettingsStore.SELECT_ASSISTANT])
        assertEquals(6, migrated[SettingsStore.VERSION])
        assertEquals(DEFAULT_ASSISTANT_ID, migrated.toSettings().assistantId)
    }

    @Test
    fun `existing install without a selection stays on the plain assistant`() = runBlocking {
        val migrated = migration.migrate(mutablePreferencesOf(SettingsStore.LAUNCH_COUNT to 3))
        assertEquals(PLAIN_ASSISTANT_ID.toString(), migrated[SettingsStore.SELECT_ASSISTANT])
        assertEquals(PLAIN_ASSISTANT_ID, migrated.toSettings().assistantId)
    }

    @Test
    fun `existing selection is kept`() = runBlocking {
        val chosen = "11111111-2222-3333-4444-555555555555"
        val migrated = migration.migrate(
            mutablePreferencesOf(SettingsStore.LAUNCH_COUNT to 3, SettingsStore.SELECT_ASSISTANT to chosen)
        )
        assertEquals(chosen, migrated[SettingsStore.SELECT_ASSISTANT])
    }

    @Test
    fun `empty store is not treated as an existing install`() {
        assertEquals(false, isExistingInstall(emptyPreferences()))
    }
}