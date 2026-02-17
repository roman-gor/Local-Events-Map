package com.gorman.deeplinks.strategies

import android.net.Uri
import com.google.firebase.FirebaseException
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.deeplinks.DeeplinksStrategy
import com.gorman.deeplinks.states.DeepLinkResult
import java.io.IOException
import javax.inject.Inject

internal class DetailsEventDeeplinksStrategy @Inject constructor(
    private val mapEventsRepository: IMapEventsRepository
) : DeeplinksStrategy {
    override fun matches(uri: Uri): Boolean {
        return uri.scheme == "app" && uri.host == "events"
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> execute(uri: Uri): DeepLinkResult<T> {
        val eventId = uri.lastPathSegment ?: return DeepLinkResult.NotFound as DeepLinkResult<T>

        val result = mapEventsRepository.syncEventById(eventId)

        return if (result.isSuccess) {
            DeepLinkResult.Success(EventId(eventId))
        } else {
            when (val exception = result.exceptionOrNull()) {
                is NoSuchElementException -> DeepLinkResult.NotFound
                is IOException, is FirebaseException -> DeepLinkResult.Network
                else -> DeepLinkResult.Error(exception?.message ?: "Generic error")
            }
        } as DeepLinkResult<T>
    }
}

@JvmInline
value class EventId(val id: String)
