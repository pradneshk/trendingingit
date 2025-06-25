package com.pradnesh.trendingingit.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pradnesh.trendingingit.model.ReposRepository
import com.pradnesh.trendingingit.model.RepositoryCallBack
import com.pradnesh.trendingingit.model.apiresponse.GitResponse
import com.pradnesh.trendingingit.model.apiresponse.Item
import com.pradnesh.trendingingit.model.apiresponse.License // Required for Item
import com.pradnesh.trendingingit.model.apiresponse.Owner   // Required for Item
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.junit.Assert.*
import org.mockito.Mockito.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(ReposRepository::class) // Prepare ReposRepository for static mocking
class RepoListViewModelTest {

    @get:Rule // Apply Rule to getter
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RepoListViewModel

    @Mock
    private lateinit var mockReposRepository: ReposRepository // Mock instance of ReposRepository

    @Mock
    private lateinit var repoListObserver: Observer<List<Item>>
    @Mock
    private lateinit var dataLoadingObserver: Observer<Boolean>
    @Mock
    private lateinit var emptyObserver: Observer<Boolean>
    @Mock
    private lateinit var toastMessageObserver: Observer<String>

    @Captor
    private lateinit var callBackCaptor: ArgumentCaptor<RepositoryCallBack>

    // Helper to create a dummy Item for tests
    private fun createDummyItem(id: Int): Item {
        return Item(
            id = id, nodeId = "nodeId$id", name = "Repo $id", fullName = "owner/repo$id",
            owner = Owner(login="owner", id=1, nodeId="ownerNodeId", avatarUrl=null, gravatarId=null, url=null, htmlUrl=null, followersUrl=null, followingUrl=null, gistsUrl=null, starredUrl=null, subscriptionsUrl=null, organizationsUrl=null, reposUrl=null, eventsUrl=null, receivedEventsUrl=null, type="User", siteAdmin=false),
            isPrivate = false, htmlUrl = "htmlUrl$id", description = "Description $id", fork = false, url = "url$id",
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


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Use openMocks for JUnit 4 with Mockito 3+

        PowerMockito.mockStatic(ReposRepository::class.java)
        `when`(ReposRepository.getRepos(any(RepositoryCallBack::class.java))).thenAnswer {
            // This setup allows capturing the callback if needed, or just preventing real call.
            // The actual mockReposRepository is not directly used by the static call,
            // but PowerMock intercepts the static `ReposRepository.getRepos`.
            // We need to ensure `mockReposRepository.getRepos` is what PowerMock calls.
            // The previous Java code was: `when(ReposRepository.getInstance()).thenReturn(mockReposRepository);`
            // This is slightly different. ReposRepository is an object, so getInstance() is gone.
            // We need to mock the object's method: `PowerMockito.when(ReposRepository.getRepos(any())).thenAnswer...`
            // Or, more simply, if we just want to capture the callback:
            // PowerMockito.doAnswer { invocation -> ... }.`when`(ReposRepository).getRepos(callBackCaptor.capture())
            // For now, let's ensure getRepos on the object is "mocked" to allow callback capture.
            // A common way with PowerMock for static methods (or object methods in Kotlin)
            // is to use `PowerMockito.doAnswer().when(ClassToMock.staticMethod())`.
            // Here, ReposRepository is an object, so `ReposRepository.getRepos(...)` is like a static call.

            // Correct PowerMock setup for object method:
            // No, this is not needed if we are testing ViewModel. ViewModel calls ReposRepository.getRepos.
            // We need to control what that call does.
            // The `mockReposRepository` field is not directly used by ViewModel.
            // The ViewModel calls the actual `ReposRepository.getRepos`.
            // PowerMock should mock this static-like call directly.
            // So, `PowerMockito.mockStatic(ReposRepository::class.java)` prepares the object.
            // Then, `PowerMockito.doAnswer { ... }.`when`(ReposRepository).getRepos(any())`
            // This seems more robust.

            // Let's stick to the plan: ViewModel calls ReposRepository.getRepos.
            // We need to capture the callback passed to it.
            // The existing test structure assumes that `mockReposRepository` is somehow injected or used.
            // This was the point of `when(ReposRepository.getInstance()).thenReturn(mockReposRepository);`
            // Since ReposRepository is an object, there's no `getInstance()`.
            // The ViewModel calls `ReposRepository.getRepos(...)` directly.
            // We need PowerMock to intercept *this* call.
            // So, the `mockReposRepository` field itself is somewhat misleading here unless we make
            // PowerMockito route calls to ReposRepository.getRepos to this mock instance's getRepos.
            // That's overly complex.

            // Simplest for PowerMock with Kotlin object:
            // PowerMockito.replace(ReposRepository.INSTANCE.getMethod("getRepos", RepositoryCallBack::class.java)).with(...)
            // Or, more typically:
            // PowerMockito.doAnswer { invocation ->
            //    val callback = invocation.arguments[0] as RepositoryCallBack
            //    // Simulate behavior, e.g., callback.onResult(true, dummyResponse)
            //    null // Important for doAnswer on void methods
            // }.`when`(ReposRepository).getRepos(any(RepositoryCallBack::class.java))

            // For now, I'll assume the test structure relies on capturing the callback.
            // The `verify(mockReposRepository).getRepos(callBackCaptor.capture())` line
            // in the original Java test will FAIL because `mockReposRepository` is not the one being called.
            // It should be `verify(ReposRepository).getRepos(callBackCaptor.capture())` after PowerMocking.
            // However, Mockito's verify doesn't work directly on static/object methods without PowerMockito's help.
            // `PowerMockito.verifyStatic(ReposRepository::class.java, times(1))`
            // `ReposRepository.getRepos(callBackCaptor.capture())`

            // Let's simplify: the key is to capture the callback.
            // We'll assume PowerMock allows us to capture it from the actual ReposRepository.getRepos call.
            // The `mockReposRepository` field seems to be a leftover from a different mocking strategy.
        }


        viewModel = RepoListViewModel()

        viewModel.repoList.observeForever(repoListObserver)
        viewModel.dataLoading.observeForever(dataLoadingObserver)
        viewModel.empty.observeForever(emptyObserver)
        viewModel.toastMessage.observeForever(toastMessageObserver)
    }

    @Test
    fun testInitialState() {
        assertFalse("DataLoading should be false initially", viewModel.dataLoading.value!!)
        assertTrue("Empty should be true initially", viewModel.empty.value!!)
        assertNull("RepoList should be null initially", viewModel.repoList.value)

        verify(dataLoadingObserver).onChanged(false)
        verify(emptyObserver).onChanged(true)
        // repoListObserver and toastMessageObserver should not have been called yet
        verify(repoListObserver, never()).onChanged(any())
        verify(toastMessageObserver, never()).onChanged(any())
    }

    @Test
    fun fetchRepoList_successCase() {
        val mockItems = listOf(createDummyItem(1))
        val mockResponse = GitResponse(totalCount = 1, incompleteResults = false, items = mockItems)

        viewModel.fetchRepoList()

        verify(dataLoadingObserver).onChanged(true) // First change: true

        // This is the tricky part with PowerMock and Kotlin objects.
        // We need to capture the argument to the *actual* ReposRepository.getRepos call.
        PowerMockito.verifyStatic(ReposRepository::class.java, times(1))
        ReposRepository.getRepos(callBackCaptor.capture()) // Capture callback

        val capturedCallback = callBackCaptor.value
        capturedCallback.onResult(true, mockResponse)

        verify(dataLoadingObserver, times(2)).onChanged(false) // Second change: false
        verify(emptyObserver).onChanged(false)
        verify(repoListObserver).onChanged(mockItems)
        assertEquals("Repo list should match mock items", mockItems, viewModel.repoList.value)
        assertFalse("ViewModel should not be empty after successful fetch", viewModel.empty.value!!)
    }

    @Test
    fun fetchRepoList_successCase_emptyResponse() {
        val emptyItems = emptyList<Item>()
        val mockResponse = GitResponse(totalCount = 0, incompleteResults = false, items = emptyItems)

        viewModel.fetchRepoList()

        verify(dataLoadingObserver).onChanged(true)
        PowerMockito.verifyStatic(ReposRepository::class.java, times(1))
        ReposRepository.getRepos(callBackCaptor.capture())
        val capturedCallback = callBackCaptor.value
        capturedCallback.onResult(true, mockResponse)

        verify(dataLoadingObserver, times(2)).onChanged(false)
        verify(emptyObserver).onChanged(true) // Corrected: empty should be true if list is empty
        verify(repoListObserver).onChanged(emptyItems)
        assertEquals(emptyItems, viewModel.repoList.value)
        assertTrue(viewModel.empty.value!!)
    }

    @Test
    fun fetchRepoList_failureCase() {
        viewModel.fetchRepoList()

        verify(dataLoadingObserver).onChanged(true)
        PowerMockito.verifyStatic(ReposRepository::class.java, times(1))
        ReposRepository.getRepos(callBackCaptor.capture())
        val capturedCallback = callBackCaptor.value
        capturedCallback.onResult(false, null)

        verify(dataLoadingObserver, times(2)).onChanged(false)
        verify(emptyObserver, times(2)).onChanged(true) // Initial true, then set to true again on failure
        verify(toastMessageObserver).onChanged("Oops something went wrong!")
        assertNull("Repo list should be null or unchanged on failure", viewModel.repoList.value) // Or check it's still initial value
        assertTrue(viewModel.empty.value!!)
    }
}
