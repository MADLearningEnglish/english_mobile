package com.mit.learning_english.presentation.feature.profile.corrections

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileMyCorrectionsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileMyCorrectionsFragment : Fragment() {

    private var _binding: FragmentProfileMyCorrectionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileMyCorrectionsViewModel by viewModels()
    private var tts: TextToSpeech? = null

    private val adapter by lazy {
        MyCorrectionsRowAdapter(
            onReviewRule = { sessionId ->
                findNavController().navigate(
                    R.id.action_profileMyCorrectionsFragment_to_correctionSessionReviewFragment,
                    bundleOf("sessionId" to sessionId)
                )
            },
            onSpeakCorrected = { text ->
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "correction_tts")
            },
            relativeTime = { iso ->
                CorrectionTimeFormatter.relativeShort(requireContext(), iso)
            }
        )
    }

    private var filterViews: List<TextView> = emptyList()
    private var selectedFilterIndex = 0
    private val filterValues = listOf("ALL", "GRAMMAR", "VOCABULARY", "SPELLING")
    private val filterLabels by lazy {
        listOf(
            getString(R.string.corrections_filter_all),
            getString(R.string.corrections_filter_grammar),
            getString(R.string.corrections_filter_vocabulary),
            getString(R.string.corrections_filter_spelling)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileMyCorrectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.toolbar.inflateMenu(R.menu.menu_my_corrections)
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search_corrections)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.vocabulary_search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setQuery(newText)
                return true
            }
        })

        buildFilterChips()

        binding.btnClearHistory.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.corrections_clear_history)
                .setMessage(R.string.corrections_clear_confirm)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.clearHistory()
                }
                .show()
        }

        binding.rvCorrections.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCorrections.adapter = adapter

        tts = TextToSpeech(requireContext()) { }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.rows.collectLatest { adapter.submitData(it) }
                }
                launch {
                    viewModel.events.collect { ev ->
                        when (ev) {
                            is MyCorrectionsEvent.Cleared -> {
                                Toast.makeText(requireContext(), R.string.corrections_cleared, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is MyCorrectionsEvent.ClearFailed ->
                                Toast.makeText(requireContext(), ev.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun buildFilterChips() {
        binding.rowFilterChips.removeAllViews()
        val padH = resources.getDimensionPixelSize(R.dimen.spacing_md)
        val padV = resources.getDimensionPixelSize(R.dimen.spacing_sm)
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_sm)
        filterViews = filterLabels.mapIndexed { index, label ->
            val tv = TextView(requireContext()).apply {
                text = label
                textSize = 12f
                gravity = Gravity.CENTER
                setPadding(padH, padV, padH, padV)
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = spacing
                }
                setOnClickListener {
                    selectedFilterIndex = index
                    viewModel.setFilter(filterValues[index])
                    filterViews.forEachIndexed { i, v -> applyChipStyle(v, i == selectedFilterIndex) }
                }
            }
            applyChipStyle(tv, index == selectedFilterIndex)
            binding.rowFilterChips.addView(tv)
            tv
        }
    }

    private fun applyChipStyle(tv: TextView, selected: Boolean) {
        tv.setBackgroundResource(
            if (selected) R.drawable.bg_profile_level_badge else R.drawable.bg_profile_rating_chip
        )
        tv.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (selected) R.color.primary else R.color.gray_400
            )
        )
        tv.setTypeface(null, if (selected) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
    }

    override fun onDestroyView() {
        tts?.shutdown()
        tts = null
        super.onDestroyView()
        _binding = null
    }
}
