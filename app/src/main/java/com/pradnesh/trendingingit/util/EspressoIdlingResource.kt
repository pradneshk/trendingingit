package com.pradnesh.trendingingit.util

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField // Expose as a static field for Java compatibility if needed, though not strictly necessary here.
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        // Check before decrementing to avoid "Counter has been corrupted!" error
        // if decrement is called more times than increment.
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }

    // The property countingIdlingResource is already public.
    // A specific getter function like getIdlingResource() isn't strictly needed in Kotlin
    // as properties have getters by default.
    // However, to maintain exact signature for Java callers (if any were outside this conversion scope):
    // fun getIdlingResource(): CountingIdlingResource {
    // return countingIdlingResource
    // }
    // For pure Kotlin usage, direct access `EspressoIdlingResource.countingIdlingResource` is fine.
}
