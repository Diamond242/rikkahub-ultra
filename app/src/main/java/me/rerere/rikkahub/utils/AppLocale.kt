package me.rerere.rikkahub.utils

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

/**
 * In-app language selection. Empty tag = follow system.
 * Android 13+: platform per-app language API (LocaleManager), synced with system App languages.
 * Older versions: stored in SharedPreferences and applied via attachBaseContext.
 */
object AppLocale {
    private const val PREFS = "app_locale"
    private const val KEY = "language_tag"

    /** language tag to native display name */
    val supported: List<Pair<String, String>> = listOf(
        "en" to "English",
        "fa" to "\u0641\u0627\u0631\u0633\u06cc",
        "ar" to "\u0627\u0644\u0639\u0631\u0628\u064a\u0629",
        "de" to "Deutsch",
        "ja" to "\u65e5\u672c\u8a9e",
        "ko-KR" to "\ud55c\uad6d\uc5b4",
        "ru" to "\u0420\u0443\u0441\u0441\u043a\u0438\u0439",
        "zh-CN" to "\u7b80\u4f53\u4e2d\u6587",
        "zh-TW" to "\u7e41\u9ad4\u4e2d\u6587",
    )

    fun current(context: Context): String {
        return if (Build.VERSION.SDK_INT >= 33) {
            val list = context.getSystemService(LocaleManager::class.java).applicationLocales
            if (list.isEmpty) "" else list.get(0).toLanguageTag()
        } else {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "") ?: ""
        }
    }

    /** Find the supported tag matching a stored/system tag ("" if follow system). */
    fun match(tag: String): String {
        if (tag.isEmpty()) return ""
        supported.firstOrNull { it.first.equals(tag, true) }?.let { return it.first }
        val lang = tag.substringBefore('-')
        if (lang == "zh") {
            return if (tag.contains("TW", true) || tag.contains("HK", true) || tag.contains("Hant", true)) "zh-TW" else "zh-CN"
        }
        return supported.firstOrNull { it.first.substringBefore('-') == lang }?.first ?: ""
    }

    fun set(activity: Activity, tag: String) {
        if (Build.VERSION.SDK_INT >= 33) {
            activity.getSystemService(LocaleManager::class.java).applicationLocales =
                if (tag.isEmpty()) LocaleList.getEmptyLocaleList() else LocaleList.forLanguageTags(tag)
        } else {
            activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, tag).commit()
            activity.recreate()
        }
    }

    fun wrap(base: Context): Context {
        if (Build.VERSION.SDK_INT >= 33) return base
        val tag = base.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "") ?: ""
        if (tag.isEmpty()) return base
        val locale = Locale.forLanguageTag(tag)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocales(LocaleList(locale))
        return base.createConfigurationContext(config)
    }
}