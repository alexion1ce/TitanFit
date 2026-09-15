package com.example.fitapp.ui.session

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** All enqueue calls come from the ViewModel main thread; flushes may overlap at suspension. */
internal class SessionWriteQueue {
    private val pending = ArrayDeque<suspend () -> Unit>()
    private val mutex = Mutex()
    fun add(write: suspend () -> Unit) { pending.addLast(write) }
    suspend fun flush() = mutex.withLock {
        while (pending.isNotEmpty()) {
            pending.first().invoke()
            pending.removeFirst()
        }
    }
    suspend fun discardAnd(action: suspend () -> Unit) = mutex.withLock {
        action()
        pending.clear()
    }
}
