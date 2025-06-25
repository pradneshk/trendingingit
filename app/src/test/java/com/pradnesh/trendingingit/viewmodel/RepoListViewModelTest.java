package com.pradnesh.trendingingit.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.pradnesh.trendingingit.model.ReposRepository;
import com.pradnesh.trendingingit.model.RepositoryCallBack;
import com.pradnesh.trendingingit.model.apiresponse.GitResponse;
import com.pradnesh.trendingingit.model.apiresponse.Item;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReposRepository.class) // Prepare ReposRepository for static mocking
public class RepoListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private RepoListViewModel viewModel;

    @Mock
    private ReposRepository mockReposRepository; // Mock instance of ReposRepository

    @Mock
    private Observer<List<Item>> repoListObserver;
    @Mock
    private Observer<Boolean> dataLoadingObserver;
    @Mock
    private Observer<Boolean> emptyObserver;
    @Mock
    private Observer<String> toastMessageObserver;

    @Captor
    private ArgumentCaptor<RepositoryCallBack> callBackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks

        // Mock the static ReposRepository.getInstance() method
        PowerMockito.mockStatic(ReposRepository.class);
        when(ReposRepository.getInstance()).thenReturn(mockReposRepository);

        viewModel = new RepoListViewModel();

        // Observe LiveData instances
        viewModel.getRepoList().observeForever(repoListObserver);
        viewModel.getDataLoading().observeForever(dataLoadingObserver);
        viewModel.getEmpty().observeForever(emptyObserver);
        viewModel.getToastMessage().observeForever(toastMessageObserver);
    }

    @Test
    public void testInitialState() {
        // Initial state of LiveData in BaseViewModel
        assertFalse("DataLoading should be false initially", viewModel.getDataLoading().getValue());
        assertTrue("Empty should be true initially", viewModel.getEmpty().getValue());
        // Initial state of repoList in RepoListViewModel
        assertNotNull("RepoList LiveData should not be null", viewModel.getRepoList());
        // MutableLiveData initializes with null if no default value, or with the set value.
        // RepoListViewModel does `MutableLiveData<List<Item>> repoList = new MutableLiveData<>();`
        // which means it will be null until a value is set, or if we set an initial value like new ArrayList<>()
        // Let's assume it's implicitly empty or null. For safety, let's check if it's null or empty.
        // If it's `new MutableLiveData<>()` without a default, `getValue()` is null.
        // If it's `new MutableLiveData<>(new ArrayList<>())`, then it's an empty list.
        // The code is: `MutableLiveData<List<Item>> repoList = new MutableLiveData<>();`
        // So, it's null initially. Let's adjust RepoListViewModel to initialize it.
        // For now, I will assume it's okay to be null and then set.
        // However, it's better practice to initialize LiveData that holds collections.
        // I'll proceed with the current state and if tests fail due to this, I'll adjust.
        // If it's null, `getValue().isEmpty()` would NPE.
        // Let's verify the observers were called with initial values from BaseViewModel
        verify(dataLoadingObserver).onChanged(false);
        verify(emptyObserver).onChanged(true);
        // repoListObserver would not have been called yet as repoList is not set by constructor.
    }

    @Test
    public void fetchRepoList_successCase() {
        GitResponse mockResponse = new GitResponse();
        List<Item> mockItems = new ArrayList<>();
        mockItems.add(new Item()); // Add a dummy item
        mockResponse.setItems(mockItems);

        // Trigger the fetch
        viewModel.fetchRepoList();

        // Verify that dataLoading becomes true
        verify(dataLoadingObserver).onChanged(true);

        // Capture the callback passed to our mock repository's getRepos method
        verify(mockReposRepository).getRepos(callBackCaptor.capture());
        RepositoryCallBack capturedCallback = callBackCaptor.getValue();

        // Simulate a successful response from the repository
        capturedCallback.onResult(true, mockResponse);

        // Verify LiveData updates
        // DataLoading should be false after completion
        verify(dataLoadingObserver, times(2)).onChanged(false); // Initial false, then true, then false
        // Empty should be false as we have items
        verify(emptyObserver).onChanged(false);
        // RepoList should be updated with mockItems
        verify(repoListObserver).onChanged(mockItems);
        assertEquals("Repo list should match mock items", mockItems, viewModel.getRepoList().getValue());
        assertFalse("ViewModel should not be empty after successful fetch with items", viewModel.getEmpty().getValue());
    }

    @Test
    public void fetchRepoList_successCase_emptyResponse() {
        GitResponse mockResponse = new GitResponse();
        List<Item> emptyItems = Collections.emptyList();
        mockResponse.setItems(emptyItems); // API returns success but with an empty list

        viewModel.fetchRepoList();

        verify(dataLoadingObserver).onChanged(true);
        verify(mockReposRepository).getRepos(callBackCaptor.capture());
        RepositoryCallBack capturedCallback = callBackCaptor.getValue();
        capturedCallback.onResult(true, mockResponse); // Success, but items list is empty

        verify(dataLoadingObserver, times(2)).onChanged(false);
        // The ViewModel logic is: if(isSuccess){ empty.setValue(false); repoList.setValue(response.getItems()); }
        // This means `empty` will be set to false even if the list is empty.
        // This might be a bug in ViewModel or intended behavior (e.g. "successfully fetched nothing" vs "failed to fetch").
        // Based on current code:
        verify(emptyObserver).onChanged(false); // ViewModel sets empty to false on any success
        verify(repoListObserver).onChanged(emptyItems);
        assertEquals(emptyItems, viewModel.getRepoList().getValue());
        // If the intention is for `empty` to be true when the list is empty, ViewModel logic needs change.
        // For now, testing current behavior.
    }


    @Test
    public void fetchRepoList_failureCase() {
        viewModel.fetchRepoList();

        verify(dataLoadingObserver).onChanged(true);
        verify(mockReposRepository).getRepos(callBackCaptor.capture());
        RepositoryCallBack capturedCallback = callBackCaptor.getValue();

        // Simulate a failure response from the repository
        capturedCallback.onResult(false, null);

        // Verify LiveData updates for failure
        verify(dataLoadingObserver, times(2)).onChanged(false); // Initial, true, then false
        // Empty should be true on failure
        verify(emptyObserver, times(2)).onChanged(true); // Initial true, then remains true (or set again)
        // ToastMessage should be set
        verify(toastMessageObserver).onChanged("Oops something went wrong!");
        // RepoList should remain as it was (or empty if it was never successfully populated)
        // Depending on initial state, it might be null or an empty list.
        // If `repoList` was `new MutableLiveData<>()`, its value is null.
        // Let's assert it's null or empty.
        if (viewModel.getRepoList().getValue() != null) {
            assertTrue("Repo list should be empty on failure if previously empty or null", viewModel.getRepoList().getValue().isEmpty());
        }
        assertTrue("ViewModel should be empty after failed fetch", viewModel.getEmpty().getValue());
    }
}
