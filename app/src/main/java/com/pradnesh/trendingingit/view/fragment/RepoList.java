package com.pradnesh.trendingingit.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pradnesh.trendingingit.databinding.FragmentRepoListBinding;
import com.pradnesh.trendingingit.viewmodel.RepoListViewModel;

public class RepoList extends Fragment {

    private FragmentRepoListBinding binding;
    private RepoListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize DataBinding
        binding = FragmentRepoListBinding.inflate(inflater, container, false);
        // It's good practice to set the lifecycle owner for LiveData observation in data binding
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RepoListViewModel.class);

        // Set the ViewModel for DataBinding
        binding.setViewmodel(viewModel);

        // Fetch the list of repositories
        viewModel.fetchRepoList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release the binding to prevent memory leaks
        binding = null;
    }
}
