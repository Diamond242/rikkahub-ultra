package me.rerere.rikkahub.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.rerere.common.http.await
import me.rerere.rikkahub.AppScope
import me.rerere.rikkahub.BuildConfig
import me.rerere.rikkahub.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Locale

/**
 * Checks the latest GitHub release of the rikkahub-ultra repository
 * (see [AppLinks.LATEST_RELEASE_API]) instead of upstream's update server.
 */
class UpdateChecker(
    private val client: OkHttpClient,
    appScope: AppScope,
) {
    private val json = Json { ignoreUnknownKeys = true }

    val updateState: StateFlow<UiState<UpdateInfo>> = checkUpdate().stateIn(
        scope = appScope,
        started = SharingStarted.Lazily,
        initialValue = UiState.Loading,
    )

    private fun checkUpdate(): Flow<UiState<UpdateInfo>> = flow {
        emit(UiState.Loading)
        emit(
            UiState.Success(
                data = try {
                    val response = client.newCall(
                        Request.Builder()
                            .url(AppLinks.LATEST_RELEASE_API)
                            .get()
                            .addHeader("Accept", "application/vnd.github+json")
                            .addHeader(
                                "User-Agent",
                                "RikkaHub-Ultra ${BuildConfig.VERSION_NAME} #${BuildConfig.VERSION_CODE}"
                            )
                            .build()
                    ).await()
                    when {
                        response.isSuccessful -> json.decodeFromString<GithubRelease>(response.body.string()).toUpdateInfo()
                        // No release published yet: treat as "up to date" instead of showing an error card.
                        response.code == 404 -> UpdateInfo.upToDate()
                        // GitHub's REST API allows only 60 requests/hour per IP (shared VPN IPs hit this often):
                        // fall back to the public release page of our own repo instead of showing an error.
                        response.code == 403 || response.code == 429 -> {
                            response.close()
                            checkViaReleasePage()
                        }
                        else -> throw Exception("HTTP ${response.code}")
                    }
                } catch (e: Exception) {
                    throw Exception("Failed to fetch update info", e)
                }
            )
        )
    }.catch {
        emit(UiState.Error(it))
    }.flowOn(Dispatchers.IO)

    /** Reads the tag from the redirect of [AppLinks.LATEST_RELEASE_PAGE] (always our repo, never upstream). */
    private suspend fun checkViaReleasePage(): UpdateInfo {
        val response = client.newCall(
            Request.Builder()
                .url(AppLinks.LATEST_RELEASE_PAGE)
                .head()
                .addHeader("User-Agent", "RikkaHub-Ultra ${BuildConfig.VERSION_NAME} #${BuildConfig.VERSION_CODE}")
                .build()
        ).await()
        val finalUrl = response.request.url.toString()
        val code = response.code
        response.close()
        if (code == 404) return UpdateInfo.upToDate()
        if (code !in 200..399) throw Exception("HTTP $code")
        val tag = releaseTagFromUrl(finalUrl) ?: return UpdateInfo.upToDate()
        return UpdateInfo(
            version = normalizeTag(tag),
            publishedAt = "",
            changelog = "",
            downloads = emptyList(),
            pageUrl = finalUrl,
        )
    }

    fun downloadUpdate(context: Context, download: UpdateDownload) {
        runCatching {
            val request = DownloadManager.Request(download.url.toUri()).apply {
                setTitle(download.name)
                setDescription(context.getString(R.string.update_downloading_package))
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, download.name)
                setMimeType("application/vnd.android.package-archive")
            }
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }.onFailure {
            Toast.makeText(context, context.getString(R.string.update_card_download_failed), Toast.LENGTH_SHORT).show()
            context.openUrl(download.url)
        }
    }
}

@Serializable
data class UpdateDownload(
    val name: String,
    val url: String,
    val size: String
)

@Serializable
data class UpdateInfo(
    val version: String,
    val publishedAt: String,
    val changelog: String,
    val downloads: List<UpdateDownload>,
    val pageUrl: String = AppLinks.RELEASES,
) {
    companion object {
        fun upToDate() = UpdateInfo(
            version = BuildConfig.VERSION_NAME,
            publishedAt = "",
            changelog = "",
            downloads = emptyList(),
        )
    }
}

@Serializable
internal data class GithubRelease(
    @SerialName("tag_name") val tagName: String,
    val name: String? = null,
    val body: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("html_url") val htmlUrl: String? = null,
    val assets: List<GithubAsset> = emptyList(),
)

@Serializable
internal data class GithubAsset(
    val name: String,
    @SerialName("browser_download_url") val downloadUrl: String,
    val size: Long = 0,
)

/** "v3.1.1-ultra.2" -> "3.1.1-ultra.2" */
internal fun normalizeTag(tag: String): String = tag.trim().removePrefix("v").removePrefix("V")

/** Extracts the tag from ".../<owner>/<repo>/releases/tag/<tag>" of OUR repo only; null for anything else. */
internal fun releaseTagFromUrl(url: String): String? {
    val prefix = "${AppLinks.GITHUB}/releases/tag/"
    if (!url.startsWith(prefix, ignoreCase = true)) return null
    return url.substring(prefix.length).substringBefore('?').substringBefore('#').trimEnd('/')
        .let { java.net.URLDecoder.decode(it, "UTF-8") }
        .takeIf { it.isNotBlank() && '/' !in it }
}

internal fun GithubRelease.toUpdateInfo(): UpdateInfo = UpdateInfo(
    version = normalizeTag(tagName),
    publishedAt = publishedAt.orEmpty(),
    changelog = body.orEmpty(),
    // APKs only; arm64-v8a first because almost every modern phone uses it
    downloads = assets
        .filter { it.name.endsWith(".apk", ignoreCase = true) }
        .sortedWith(compareByDescending<GithubAsset> { it.name.contains("arm64", true) }.thenBy { it.name })
        .map { UpdateDownload(name = it.name, url = it.downloadUrl, size = formatBytes(it.size)) },
    pageUrl = htmlUrl ?: AppLinks.RELEASES,
)

private fun formatBytes(bytes: Long): String = when {
    bytes <= 0 -> ""
    bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes / 1024.0 / 1024.0)
    else -> String.format(Locale.US, "%.0f KB", bytes / 1024.0)
}
/**
 * 版本号值类，封装版本号字符串并提供比较功能
 *
 * 支持完整的 SemVer 规范：MAJOR.MINOR.PATCH[-prerelease][+build]
 * - 预发布版本优先级低于正式版：1.0.0-alpha < 1.0.0
 * - 预发布标识符按段逐个比较：数字按数值比较，字符串按字典序比较
 * - 预发布标识符优先级：alpha < beta < rc（通过字典序自然满足）
 * - build metadata（+号后面的部分）不影响优先级比较
 */
@JvmInline
value class Version(val value: String) : Comparable<Version> {

    private fun parse(): ParsedVersion {
        // 去掉 build metadata（+号后面的部分）
        val withoutBuild = value.trim().removePrefix("v").removePrefix("V").split("+").first()
        // 分离主版本号和预发布标识符
        val hyphenIndex = withoutBuild.indexOf('-')
        val (coreStr, prereleaseStr) = if (hyphenIndex >= 0) {
            withoutBuild.substring(0, hyphenIndex) to withoutBuild.substring(hyphenIndex + 1)
        } else {
            withoutBuild to null
        }
        val core = coreStr.split(".").map { it.toIntOrNull() ?: 0 }
        var prerelease = prereleaseStr?.split(".")
        // RikkaHub Ultra: "<upstream>-ultra.N" is a fork release built ON TOP of <upstream>, so it is not a
        // SemVer prerelease: 3.1.1-rc.1 < 3.1.1 < 3.1.1-ultra.1 < 3.1.1-ultra.2 < 3.1.2
        var fork = 0
        if (prerelease != null && prerelease.first().equals(FORK_TAG, ignoreCase = true)) {
            val n = prerelease.getOrNull(1)?.toIntOrNull()
            fork = n ?: 1
            prerelease = prerelease.drop(if (n != null) 2 else 1).ifEmpty { null }
        }
        return ParsedVersion(core, prerelease, fork)
    }

    override fun compareTo(other: Version): Int {
        val a = this.parse()
        val b = other.parse()

        // 先比较主版本号
        val maxLen = maxOf(a.core.size, b.core.size)
        for (i in 0 until maxLen) {
            val ap = if (i < a.core.size) a.core[i] else 0
            val bp = if (i < b.core.size) b.core[i] else 0
            if (ap != bp) return ap.compareTo(bp)
        }

        // RikkaHub Ultra: same upstream version -> higher fork release wins (plain upstream = 0)
        if (a.fork != b.fork) return a.fork.compareTo(b.fork)

        // 主版本号相同时比较预发布标识符
        // 有预发布标识符的版本优先级低于没有的：1.0.0-alpha < 1.0.0
        return when {
            a.prerelease == null && b.prerelease == null -> 0
            a.prerelease != null && b.prerelease == null -> -1
            a.prerelease == null && b.prerelease != null -> 1
            else -> comparePrerelease(a.prerelease!!, b.prerelease!!)
        }
    }

    companion object {
        private const val FORK_TAG = "ultra"

        fun compare(version1: String, version2: String): Int {
            return Version(version1).compareTo(Version(version2))
        }

        private fun comparePrerelease(a: List<String>, b: List<String>): Int {
            val maxLen = maxOf(a.size, b.size)
            for (i in 0 until maxLen) {
                // 字段少的优先级更低：1.0.0-alpha < 1.0.0-alpha.1
                if (i >= a.size) return -1
                if (i >= b.size) return 1

                val aNum = a[i].toIntOrNull()
                val bNum = b[i].toIntOrNull()

                val cmp = when {
                    // 都是字：按数值比较
                    aNum != null && bNum != null -> aNum.compareTo(bNum)
                    // 数字优先级低于字符串
                    aNum != null -> -1
                    bNum != null -> 1
                    // 都是字符串：按字典序比较
                    else -> a[i].compareTo(b[i])
                }
                if (cmp != 0) return cmp
            }
            return 0
        }
    }
}

private data class ParsedVersion(
    val core: List<Int>,
    val prerelease: List<String>?,
    val fork: Int = 0,
)

// 扩展操作符函数，使比较更直观
operator fun String.compareTo(other: Version): Int = Version(this).compareTo(other)
operator fun Version.compareTo(other: String): Int = this.compareTo(Version(other))
