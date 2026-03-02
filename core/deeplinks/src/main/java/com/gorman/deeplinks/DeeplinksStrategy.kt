package com.gorman.deeplinks

import android.net.Uri
import com.gorman.deeplinks.states.DeepLinkResult

interface DeeplinksStrategy {
    fun matches(uri: Uri): Boolean
    suspend fun <T> execute(uri: Uri): DeepLinkResult<T>
}
