package com.mit.learning_english.presentation.feature.profile.vocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileVocabularyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileVocabularyFragment : Fragment() {

    private var _binding: FragmentProfileVocabularyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileVocabularyViewModel by viewModels()

    private lateinit var adapter: ProfileVocabularyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileVocabularyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.vocabulary_tab_all))
        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.vocabulary_tab_favorites))
        binding.tabFilter.addTab(binding.tabFilter.newTab().setText(R.string.vocabulary_tab_difficult))

        adapter = ProfileVocabularyAdapter { w -> viewModel.toggleFavorite(w) }
        binding.rvWords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWords.adapter = adapter

        binding.tabFilter.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                val f = when (tab?.position) {
                    1 -> "FAVORITES"
                    2 -> "DIFFICULT"
                    else -> "ALL"
                }
                viewModel.setFilter(f)
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

        binding.etSearch.doAfterTextChanged { e ->
            viewModel.setQuery(e?.toString())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.words.collectLatest { adapter.submitData(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
