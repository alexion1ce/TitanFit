package com.example.fitapp.ui.session

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class SessionWriteQueueTest {
    @Test fun rapidWeightAndDoneWritesFlushInOrderBeforeExit() = runBlocking {
        val queue = SessionWriteQueue()
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val events = mutableListOf<String>()
        queue.add { started.complete(Unit); release.await(); events.add("weight=20") }
        val first = launch { queue.flush() }
        started.await()
        queue.add { events.add("done=true") }
        val exit = launch { queue.flush(); events.add("exit") }
        assertTrue(events.isEmpty())
        release.complete(Unit)
        first.join(); exit.join()
        assertEquals(listOf("weight=20", "done=true", "exit"), events)
    }
    @Test fun failureRetainsHeadAndLaterWritesForOrderedRetry() = runBlocking {
        val queue = SessionWriteQueue()
        var shouldFail = true
        val values = mutableListOf<Int>()
        queue.add { if (shouldFail) error("disk failure"); values.add(20) }
        queue.add { values.add(25) }
        try { queue.flush(); fail("failure expected") } catch (_: IllegalStateException) { }
        assertTrue(values.isEmpty())
        shouldFail = false
        queue.flush()
        assertEquals(listOf(20, 25), values)
    }
    @Test fun failedDiscardRetainsPendingWritesForRetry() = runBlocking {
        val queue = SessionWriteQueue()
        val values = mutableListOf<Int>()
        queue.add { values.add(20) }
        queue.add { values.add(25) }
        try {
            queue.discardAnd { error("delete failed") }
            fail("failure expected")
        } catch (_: IllegalStateException) { }
        assertTrue(values.isEmpty())
        queue.flush()
        assertEquals(listOf(20, 25), values)
    }
}
