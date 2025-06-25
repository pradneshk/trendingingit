package com.pradnesh.trendingingit.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pradnesh.trendingingit.databinding.FragmentRepoListBinding
import com.pradnesh.trendingingit.viewmodel.RepoListViewModel

class RepoList : Fragment() {

    // Nullable binding variable
    private var _binding: FragmentRepoListBinding? = null
    // Non-null getter for the binding, valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    private lateinit var viewModel: RepoListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner // Use the non-null getter
        return binding.root // Use the non-null getter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[RepoListViewModel::class.java] // Kotlin property access for get

        binding.viewmodel = viewModel // Use the non-null getter

        viewModel.fetchRepoList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Release binding
    }
}
