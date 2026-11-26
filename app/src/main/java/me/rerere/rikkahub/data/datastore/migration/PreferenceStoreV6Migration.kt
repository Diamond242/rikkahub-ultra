package me.rerere.rikkahub.data.datastore.migration

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import me.rerere.rikkahub.data.datastore.PLAIN_ASSISTANT_ID
import me.rerere.rikkahub.data.datastore.SettingsStore

/**
 * The default assistant changed from the plain one to the smart one.
 * Existing installs that never stored a selection were implicitly on the plain assistant:
 * pin that so only fresh installs get the new default.
 */
class PreferenceStoreV6Migration : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        val version = currentData[SettingsStore.VERSION]
        return version == null || version < 6
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val prefs = currentData.toMutablePreferences()
        if (isExistingInstall(currentData) && prefs[SettingsStore.SELECT_ASSISTANT] == null) {
            prefs[SettingsStore.SELECT_ASSISTANT] = PLAIN_ASSISTANT_ID.toString()
        }
        prefs[SettingsStore.VERSION] = 6
        return prefs.toPreferences()
    }

    override suspend fun cleanUp() {}
}

/**
 * Earlier migrations also write keys on a fresh install, so look for keys only a real run leaves behind:
 * the launch counter (bumped every start) or the providers list (written on the first settings save).
 */
internal fun isExistingInstall(prefs: Preferences): Boolean =
    prefs[SettingsStore.LAUNCH_COUNT] != null || prefs[SettingsStore.PROVIDERS] != null