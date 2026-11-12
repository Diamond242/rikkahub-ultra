package me.rerere.rikkahub.utils

import android.content.Context

/**
 * Release notes written in both English and Persian.
 *
 * A text may contain language sections marked with `<!-- lang:en -->`, `<!-- lang:fa -->` ... and
 * optionally closed with `<!-- /lang -->`. [localize] returns the section for the requested language,
 * falling back to English and then to the whole text. Used for the bundled CHANGELOG.md and for
 * GitHub release bodies.
 */
object Changelog {
    private val marker = Regex("""<!--\s*(/lang|lang:([A-Za-z-]+))\s*-->""")

    fun sections(text: String): Map<String, String> {
        val result = linkedMapOf<String, String>()
        val matches = marker.findAll(text).toList()
        matches.forEachIndexed { index, match ->
            val lang = match.groupValues[2].takeIf { it.isNotEmpty() } ?: return@forEachIndexed
            val end = matches.getOrNull(index + 1)?.range?.first ?: text.length
            val body = text.substring(match.range.last + 1, end).trim()
            if (body.isNotEmpty()) result[lang.lowercase().substringBefore('-')] = body
        }
        return result
    }

    private val htmlDiv = Regex("""</?div\b[^>]*>""", RegexOption.IGNORE_CASE)

    /** Drop GitHub-only wrappers such as <div dir="rtl"> so the in-app dialog stays readable. */
    fun stripGithubMarkup(text: String): String = htmlDiv.replace(text, "").trim()

    fun localize(text: String, language: String): String {
        val sections = sections(text)
        val raw = if (sections.isEmpty()) {
            text.trim()
        } else {
            sections[language.lowercase().substringBefore('-')]
                ?: sections["en"]
                ?: sections.values.first()
        }
        return stripGithubMarkup(raw)
    }

    fun loadBundled(context: Context): String? = runCatching {
        context.assets.open("CHANGELOG.md").bufferedReader().use { it.readText() }
    }.getOrNull()
}