package com.pradnesh.trendingingit.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
// import androidx.navigation.fragment.navArgs // For Safe Args, if used

class RepoDetail : Fragment() {

    // Example of how to get nav args if Safe Args plugin is configured.
    // Add 'androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version' to project build.gradle
    // Add 'apply plugin: "androidx.navigation.safeargs.kotlin"' to app build.gradle
    // Then, the generated NavArgs class can be used:
    // private val args: RepoDetailArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // If this fragment had its own layout, it would be inflated here.
        // For example:
        // val binding = FragmentRepoDetailBinding.inflate(inflater, container, false)
        // return binding.root
        return super.onCreateView(inflater, container, savedInstanceState) // Current behavior
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access arguments:
        // val repoUrlFromSafeArgs = args.repoUrl // Using SafeArgs
        // val repoUrlDirect = arguments?.getString("repoUrl") // Direct access

        // TODO: Implement UI and logic for displaying repository details using repoUrl
    }
}
