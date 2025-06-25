package com.pradnesh.trendingingit.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.pradnesh.trendingingit.R
import com.pradnesh.trendingingit.model.api.ApiClient
import com.pradnesh.trendingingit.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityLaunchTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        // Ensure ApiClient is using the MockWebServer URL
        ApiClient.setBaseUrlForTesting(mockWebServer.url("/").toString())

        // Enqueue a default response for any call to prevent test hangs/failures
        mockWebServer.enqueue(MockResponse().setBody("{\"items\": [], \"total_count\": 0, \"incomplete_results\": false}")) // Empty success

        // Register Idling Resource
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        ApiClient.resetBaseUrl() // Reset for other tests
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun mainActivity_launchesSuccessfully_andInitialUIStateIsCorrect() {
        // Check toolbar and nav fragment from MainActivity's layout
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.main_nav_fragment)).check(matches(isDisplayed()))

        // Since RepoList is the start destination and fetches data (mocked to be empty),
        // Espresso will wait due to IdlingResource.
        // Check for the state of RepoListFragment's UI:
        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed()))) // RecyclerView not displayed
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))   // ProgressBar not displayed
        onView(withText("No repo to display")).check(matches(isDisplayed())) // Empty text is displayed
    }
}
