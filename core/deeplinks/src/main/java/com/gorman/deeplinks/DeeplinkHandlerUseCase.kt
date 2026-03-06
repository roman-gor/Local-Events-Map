package com.gorman.deeplinks

import androidx.core.net.toUri
import com.gorman.deeplinks.states.DeepLinkResult
import javax.inject.Inject

class DeeplinkHandlerUseCase @Inject constructor(
    private val strategies: Set<@JvmSuppressWildcards DeeplinksStrategy>
) {
    @Suppress("UNCHECKED_CAST")
    suspend operator fun <T> invoke(link: String): DeepLinkResult<T> {
        val uri = try {
            link.toUri()
        } catch (_: Exception) {
            return DeepLinkResult.Error("Invalid Link") as DeepLinkResult<T>
        }

        val strategy = strategies.firstOrNull { it.matches(uri) }

        return strategy?.execute(uri) ?: DeepLinkResult.Ignored as DeepLinkResult<T>
    }
}
