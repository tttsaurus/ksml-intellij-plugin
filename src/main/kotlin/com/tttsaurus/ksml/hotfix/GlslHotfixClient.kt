package com.tttsaurus.ksml.hotfix

import com.google.gson.JsonParser
import com.intellij.util.io.HttpRequests
import com.tttsaurus.ksml.KsmlBundle
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InterruptedIOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

data class GlslHotfixRelease(val tag: String, val prerelease: Boolean, val asset: GlslHotfixAsset?) {
    override fun toString(): String = tag + if (prerelease) " (prerelease)" else ""
}

data class GlslHotfixAsset(val name: String, val url: String, val size: Long, val digest: String?)

internal interface GlslHotfixClient {
    fun repositoryExists(): Boolean
    fun discover(): List<GlslHotfixRelease>
    fun download(release: GlslHotfixRelease): Path
}

internal class GithubGlslHotfixClient(private val cacheDirectory: Path) : GlslHotfixClient {

    override fun repositoryExists(): Boolean {
        return HttpRequests.head("https://github.com/tttsaurus/glsl-plugin-idea")
            .productNameAsUserAgent()
            .connectTimeout(10_000)
            .readTimeout(15_000)
            .throwStatusCodeException(false)
            .tryConnect() == 200
    }

    override fun discover(): List<GlslHotfixRelease> {
        val releases = mutableListOf<GlslHotfixRelease>()
        var page = 1
        do {
            val json = request("https://api.github.com/repos/tttsaurus/glsl-plugin-idea/releases?per_page=100&page=$page")
                .accept("application/vnd.github+json")
                .connect { request -> readLimited(request.inputStream, 10L * 1024 * 1024).toString(Charsets.UTF_8) }
            val items = JsonParser.parseString(json).asJsonArray
            releases += parseReleases(json)
            page++
        } while (items.size() == 100)
        return releases.distinctBy { it.tag }
    }

    override fun download(release: GlslHotfixRelease): Path {
        val asset = requireNotNull(release.asset)
        Files.createDirectories(cacheDirectory)
        val directory = Files.createTempDirectory(cacheDirectory, "download-")
        val file = directory.resolve(asset.name)
        try {
            request(asset.url).connect { request ->
                request.inputStream.use { input ->
                    Files.newOutputStream(file).use { output ->
                        val buffer = ByteArray(8192)
                        var total = 0L
                        while (true) {
                            checkInterrupted()
                            val count = input.read(buffer)
                            if (count < 0) break
                            total += count
                            if (total > MAX_JAR_BYTES) throw IOException(KsmlBundle.message("hotfix.error.size"))
                            output.write(buffer, 0, count)
                        }
                    }
                }
            }
            if (Files.size(file) != asset.size) throw IOException(KsmlBundle.message("hotfix.error.download"))
            verifyDigest(file, asset.digest)
            return file
        } catch (error: Exception) {
            deleteDownload(file)
            throw error
        }
    }

    private fun request(url: String) = HttpRequests.request(url)
        .productNameAsUserAgent()
        .connectTimeout(10_000)
        .readTimeout(15_000)

    companion object {

        private const val MAX_JAR_BYTES = 50L * 1024 * 1024

        internal fun parseReleases(json: String): List<GlslHotfixRelease> = JsonParser.parseString(json).asJsonArray
            .filterNot { it.asJsonObject.get("draft")?.asBoolean == true }
            .map { item ->
                val release = item.asJsonObject
                val tag = release.get("tag_name").asString
                val expectedName = "glsl-plugin-idea-${tag.removePrefix("v")}.jar"
                val asset = release.getAsJsonArray("assets")?.firstOrNull {
                    it.asJsonObject.get("name").asString == expectedName &&
                        expectedName.matches(Regex("glsl-plugin-idea-[A-Za-z0-9][A-Za-z0-9._-]*\\.jar"))
                }?.asJsonObject?.let {
                    val url = it.get("browser_download_url").asString
                    if (url != "https://github.com/tttsaurus/glsl-plugin-idea/releases/download/$tag/$expectedName") {
                        throw IOException(KsmlBundle.message("hotfix.error.asset"))
                    }
                    GlslHotfixAsset(expectedName, url, it.get("size").asLong,
                        it.get("digest")?.takeUnless { digest -> digest.isJsonNull }?.asString)
                }
                GlslHotfixRelease(tag, release.get("prerelease")?.asBoolean == true, asset)
            }

        internal fun verifyDigest(file: Path, digest: String?) {
            if (digest == null) return
            if (!digest.startsWith("sha256:")) throw IOException(KsmlBundle.message("hotfix.error.digest"))
            val hasher = MessageDigest.getInstance("SHA-256")
            Files.newInputStream(file).use { input ->
                val buffer = ByteArray(8192)
                while (true) {
                    checkInterrupted()
                    val count = input.read(buffer)
                    if (count < 0) break
                    hasher.update(buffer, 0, count)
                }
            }
            val actual = hasher.digest().joinToString("") { "%02x".format(it) }
            if (!actual.equals(digest.removePrefix("sha256:"), ignoreCase = true)) {
                throw IOException(KsmlBundle.message("hotfix.error.digest"))
            }
        }

        private fun readLimited(input: InputStream, limit: Long): ByteArray = input.use {
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            var total = 0L
            while (true) {
                checkInterrupted()
                val count = input.read(buffer)
                if (count < 0) break
                total += count
                if (total > limit) throw IOException(KsmlBundle.message("hotfix.error.size"))
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
    }
}

internal fun checkInterrupted() {
    if (Thread.currentThread().isInterrupted) throw InterruptedIOException()
}

internal fun deleteDownload(file: Path) {
    runCatching {
        Files.deleteIfExists(file)
        Files.deleteIfExists(file.parent)
    }
}
