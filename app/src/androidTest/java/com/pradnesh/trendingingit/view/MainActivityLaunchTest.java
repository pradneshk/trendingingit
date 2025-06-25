package com.pradnesh.trendingingit.view;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.pradnesh.trendingingit.R;
import com.pradnesh.trendingingit.model.api.ApiClient;
import com.pradnesh.trendingingit.util.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityLaunchTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws IOException {
        // Start MockWebServer to handle any network requests made on launch
        // (e.g., by RepoListFragment if it's the start destination)
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        ApiClient.setBaseUrlForTesting(mockWebServer.url("/").toString());

        // Enqueue a default response for any call to prevent test hangs/failures
        // if the initial fragment tries to load data.
        mockWebServer.enqueue(new MockResponse().setBody("{\"items\": []}")); // Empty success

        // Register Idling Resource
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        ApiClient.resetBaseUrl();
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void mainActivity_launchesSuccessfully_andToolbarIsDisplayed() {
        // MainActivity's layout (main_activity.xml) contains a Toolbar with id 'toolbar'
        // and a NavHostFragment with id 'main_nav_fragment'.
        // We can check if either of these is displayed.
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.main_nav_fragment)).check(matches(isDisplayed()));

        // Since RepoList is the start destination and it fetches data,
        // Espresso will wait due to IdlingResource.
        // We can also check for the "No repo to display" text if the default mock response is empty.
        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed()))); // From default empty response
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.empty_text_view)).check(matches(isDisplayed())); // Check for the empty text view in fragment_repo_list.xml
                                                                           // The ID is actually 'No repo to display' if it's a TextView with that text.
                                                                           // The layout uses a TextView with text "No repo to display"
                                                                           // and visibility tied to viewmodel.empty
                                                                           // Let's use the text itself for verification.
        onView(withText("No repo to display")).check(matches(isDisplayed()));

    }
}
