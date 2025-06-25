package com.pradnesh.trendingingit.view.fragment

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.pradnesh.trendingingit.R
import com.pradnesh.trendingingit.model.api.ApiClient
import com.pradnesh.trendingingit.model.apiresponse.GitResponse
import com.pradnesh.trendingingit.model.apiresponse.Item
import com.pradnesh.trendingingit.model.apiresponse.License
import com.pradnesh.trendingingit.model.apiresponse.Owner
import com.pradnesh.trendingingit.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RepoListFragmentTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        ApiClient.setBaseUrlForTesting(mockWebServer.url("/").toString())
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        ApiClient.resetBaseUrl()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    // Helper to create a dummy Item for test JSON responses
    private fun createTestItem(id: Int, includeDescription: Boolean): Item {
        return Item(
            id = id,
            nodeId = "nodeId$id",
            name = "Repo $id",
            fullName = "owner/repo$id",
            owner = Owner(login="owner", id=1, nodeId="ownerNodeId", avatarUrl=null, gravatarId=null, url=null, htmlUrl=null, followersUrl=null, followingUrl=null, gistsUrl=null, starredUrl=null, subscriptionsUrl=null, organizationsUrl=null, reposUrl=null, eventsUrl=null, receivedEventsUrl=null, type="User", siteAdmin=false),
            isPrivate = false,
            htmlUrl = "htmlUrl$id",
            description = if (includeDescription) "Description for repo $id" else null,
            fork = false,
            url = "url$id",
            forksUrl = null, keysUrl = null, collaboratorsUrl = null, teamsUrl = null, hooksUrl = null, issueEventsUrl = null,
            eventsUrl = null, assigneesUrl = null, branchesUrl = null, tagsUrl = null, blobsUrl = null, gitTagsUrl = null,
            gitRefsUrl = null, treesUrl = null, statusesUrl = null, languagesUrl = null, stargazersUrl = null,
            contributorsUrl = null, subscribersUrl = null, subscriptionUrl = null, commitsUrl = null, gitCommitsUrl = null,
            commentsUrl = null, issueCommentUrl = null, contentsUrl = null, compareUrl = null, mergesUrl = null,
            archiveUrl = null, downloadsUrl = null, issuesUrl = null, pullsUrl = null, milestonesUrl = null,
            notificationsUrl = null, labelsUrl = null, releasesUrl = null, deploymentsUrl = null, createdAt = null,
            updatedAt = null, pushedAt = null, gitUrl = null, sshUrl = null, cloneUrl = null, svnUrl = null,
            homepage = null, size = 100, stargazersCount = 10, watchersCount = 10, language = "Kotlin",
            hasIssues = true, hasProjects = false, hasDownloads = true, hasWiki = false, hasPages = false,
            forksCount = 1, mirrorUrl = null, archived = false, openIssuesCount = 0,
            license = License(key="mit", name="MIT License", spdxId="MIT", url=null, nodeId=null),
            forks = 1, openIssues = 0, watchers = 10, defaultBranch = "main", score = 1.0
        )
    }

    private fun createSuccessResponseJson(itemCount: Int, includeDescription: Boolean): String {
        val items = List(itemCount) { i -> createTestItem(i, includeDescription) }
        val response = GitResponse(
            totalCount = itemCount,
            incompleteResults = false,
            items = items
        )
        return Gson().toJson(response)
    }

    @Test
    fun testLoadingStateThenDataDisplayed() {
        mockWebServer.enqueue(
            MockResponse()
                .setBodyDelay(1, TimeUnit.SECONDS)
                .setBody(createSuccessResponseJson(3, true))
        )

        val scenario = FragmentScenario.launchInContainer(RepoList::class.java, null, R.style.Theme_TrendingInGit, Lifecycle.State.INITIALIZED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed())))
        onView(withText("No repo to display")).check(matches(not(isDisplayed())))

        // EspressoIdlingResource handles waiting
        onView(withId(R.id.repo_list_rv)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withText("No repo to display")).check(matches(not(isDisplayed())))

        // Optionally, check if an item is displayed. This requires RecyclerView to have items
        // and the item layout to have a view with text "Repo 0".
        // onView(withText("Repo 0")).check(matches(isDisplayed()));
    }

    @Test
    fun testEmptyStateIsShownWhenApiReturnsEmptyList() {
        val jsonResponse = createSuccessResponseJson(0, false)
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val scenario = FragmentScenario.launchInContainer(RepoList::class.java, null, R.style.Theme_TrendingInGit, Lifecycle.State.INITIALIZED)
        scenario.moveToState(Lifecycle.State.RESUMED)


        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withText("No repo to display")).check(matches(isDisplayed()))
    }

    @Test
    fun testErrorStateIsShownOnApiFailure() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Server Error"))

        val scenario = FragmentScenario.launchInContainer(RepoList::class.java, null, R.style.Theme_TrendingInGit, Lifecycle.State.INITIALIZED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.repo_list_rv)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        onView(withText("No repo to display")).check(matches(isDisplayed()))
    }
}
