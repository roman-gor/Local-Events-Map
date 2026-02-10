package com.gorman.data

import android.util.Log
import androidx.room.withTransaction
import com.gorman.cache.data.DataStoreManager
import com.gorman.data.repository.mapevents.MapEventsRepository
import com.gorman.database.data.datasource.LocalEventsDatabase
import com.gorman.database.data.datasource.dao.MapEventsDao
import com.gorman.network.data.datasource.mapevent.MapEventRemoteDataSource
import com.gorman.network.data.models.MapEventRemote
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class MapEventsRepositoryTest {
    private val dao: MapEventsDao = mockk()
    private val dataSource: MapEventRemoteDataSource = mockk()
    private val db: LocalEventsDatabase = mockk()
    private val dataStore: DataStoreManager = mockk()

    private lateinit var repository: MapEventsRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repository = MapEventsRepository(
            dao,
            dataSource,
            db,
            dataStore
        )

        mockkStatic("androidx.room.RoomDatabaseKt")
        mockkStatic(Log::class)

        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `syncWith updates remote events and removes missing ones locally`() = runTest {
        val remoteDtoList = listOf(
            createFakeRemoteEvent(id = "1"),
            createFakeRemoteEvent(id = "2")
        )

        coEvery { dataSource.getAllEventsOnce() } returns remoteDtoList

        val transactionLambda = slot<suspend () -> Unit>()
        coEvery {
            db.withTransaction(capture(transactionLambda))
        } coAnswers {
            transactionLambda.captured.invoke()
        }

        coEvery { dao.deleteEventsNotIn(any()) } just Runs
        coEvery { dao.upsertEvent(any()) } just Runs
        coEvery { dataStore.saveSyncTimestamp(any()) } just Runs

        val result = repository.syncWith()

        assertTrue(result.isSuccess)

        coVerify {
            dao.deleteEventsNotIn(
                match { ids ->
                    ids.contains("1") && ids.contains("2") && ids.size == 2
                }
            )
        }

        coVerify {
            dao.upsertEvent(
                match { entities ->

                    entities.size == 2 && entities.any { it.id == "1" }
                }
            )
        }

        coVerify { dataStore.saveSyncTimestamp(any()) }
    }

    private fun createFakeRemoteEvent(id: String): MapEventRemote {
        val dto = mockk<MapEventRemote>(relaxed = true)
        every { dto.id } returns id
        return dto
    }

    @Test
    fun `isOutdated returns true when time passed TTL`() = runTest {
        val oldTime = System.currentTimeMillis() - (25 * 60 * 60 * 1000L)
        every { dataStore.lastSyncTimestamp } returns flowOf(oldTime)

        val result = repository.isOutdated()

        assertTrue(result)
    }
}
