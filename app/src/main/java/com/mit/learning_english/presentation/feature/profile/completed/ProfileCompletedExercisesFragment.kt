package com.mit.learning_english.presentation.feature.profile.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileCompletedExercisesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileCompletedExercisesFragment : Fragment() {

    private var _binding: FragmentProfileCompletedExercisesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileCompletedExercisesViewModel by viewModels()
    private val adapter = CompletedExerciseRowAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileCompletedExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.inflateMenu(R.menu.menu_completed_exercises)
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search_completed)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.vocabulary_search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setQuery(newText)
                return true
            }
        })

        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.completed_exercises_tab_all))
        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.completed_exercises_tab_exercises))
        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.completed_exercises_tab_books))

        binding.tabFilter.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                val f = when (tab?.position) {
                    1 -> "EXERCISE"
                    2 -> "BOOK"
                    else -> "ALL"
                }
                viewModel.setFilter(f)
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
        binding.tabFilter.getTabAt(1)?.select()

        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collectLatest { adapter.submitData(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
