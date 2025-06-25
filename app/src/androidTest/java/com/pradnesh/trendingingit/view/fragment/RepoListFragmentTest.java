package com.pradnesh.trendingingit.view.fragment;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.pradnesh.trendingingit.R;
import com.pradnesh.trendingingit.model.api.ApiClient;
import com.pradnesh.trendingingit.model.apiresponse.GitResponse;
import com.pradnesh.trendingingit.model.apiresponse.Item;
import com.pradnesh.trendingingit.util.EspressoIdlingResource; // Assuming this will be created
import com.google.gson.Gson;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class RepoListFragmentTest {

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        ApiClient.setBaseUrlForTesting(mockWebServer.url("/").toString());

        // Register Idling Resource
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        ApiClient.resetBaseUrl();

        // Unregister Idling Resource
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    private String createSuccessResponseJson(int itemCount, boolean includeDescription) {
        GitResponse response = new GitResponse();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            Item item = new Item();
            item.setId(i);
            item.setName("Repo " + i);
            if (includeDescription) {
                item.setDescription("Description for repo " + i);
            }
            // Add other necessary fields if your item layout displays them
            // item.setOwner(new Owner()); item.getOwner().setAvatarUrl("url");
            // item.setStargazersCount(i * 10);
            items.add(item);
        }
        response.setItems(items);
        response.setTotalCount(itemCount);
        response.setIncompleteResults(false);
        return new Gson().toJson(response);
    }

    // Test for initial loading state followed by data
    @Test
    public void testLoadingStateThenDataDisplayed() {
        // Enqueue a response that will be served after a slight delay to catch loading state
        mockWebServer.enqueue(
                new MockResponse()
                        .setBodyDelay(1, TimeUnit.SECONDS) // Delay to make loading state more testable
                        .setBody(createSuccessResponseJson(3, true))
        );

        FragmentScenario<RepoList> scenario = FragmentScenario.launchInContainer(RepoList.class, null, R.style.Theme_TrendingInGit, null);
        scenario.moveToState(Lifecycle.State.RESUMED); // Triggers onViewCreated -> fetchRepoList

        // Check for loading state: ProgressBar should be visible, RecyclerView and EmptyText hidden
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed())));
        onView(withText("No repo to display")).check(matches(not(isDisplayed())));

        // After delay and response, data should be displayed
        // EspressoIdlingResource will wait for dataLoading to become false
        onView(withId(R.id.repo_list_rv)).check(matches(isDisplayed()));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withText("No repo to display")).check(matches(not(isDisplayed())));

        // Optionally, check for an item. Requires an adapter and ViewHolder that sets the text.
        // For this, we need to know the layout of list items. Assume 'item_repo.xml' with 'repo_name'.
        // onView(withText("Repo 0")).check(matches(isDisplayed()));
    }


    @Test
    public void testEmptyStateIsShownWhenApiReturnsEmptyList() {
        // API call is successful but returns an empty list of items
        String jsonResponse = createSuccessResponseJson(0, false);
        mockWebServer.enqueue(new MockResponse().setBody(jsonResponse));

        FragmentScenario<RepoList> scenario = FragmentScenario.launchInContainer(RepoList.class, null, R.style.Theme_TrendingInGit, null);
        scenario.moveToState(Lifecycle.State.RESUMED);

        // RecyclerView should be hidden (or not displayed if visibility is GONE)
        // ProgressBar should be hidden
        // "No repo to display" text should be visible
        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed())));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withText("No repo to display")).check(matches(isDisplayed()));
    }

    @Test
    public void testErrorStateIsShownOnApiFailure() {
        // Simulate a server error
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Server Error"));

        FragmentScenario<RepoList> scenario = FragmentScenario.launchInContainer(RepoList.class, null, R.style.Theme_TrendingInGit, null);
        scenario.moveToState(Lifecycle.State.RESUMED);

        // RecyclerView should be hidden
        // ProgressBar should be hidden
        // "No repo to display" text should be visible due to error
        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed())));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withText("No repo to display")).check(matches(isDisplayed()));
        // Optionally, check for toast message if it's implemented and testable
    }
}
