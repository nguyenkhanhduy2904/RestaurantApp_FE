package com.example.restaurantapp2.Utils
import kotlinx.coroutines.delay

import android.util.Log

suspend fun <T> retryOnce(
    times: Int = 3,
    delayMs: Long = 800,
    block: suspend () -> T
): T {
    var lastError: Exception? = null

    repeat(times) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastError = e
            Log.e("Retry", "Attempt ${attempt + 1} failed: ${e.message}")
            delay(delayMs)
        }
    }

    throw lastError ?: Exception("Unknown error")
}