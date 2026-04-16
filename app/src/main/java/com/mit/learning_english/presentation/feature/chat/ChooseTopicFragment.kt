package com.mit.learning_english.presentation.feature.chat

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mit.learning_english.R
import com.mit.learning_english.data.remote.dto.AiScenarioDto
import com.mit.learning_english.databinding.FragmentChooseTopicBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.presentation.feature.ai.ScenarioAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseTopicFragment : BaseFragment<FragmentChooseTopicBinding, ChooseTopicViewModel>() {

    override val viewModel: ChooseTopicViewModel by viewModels()

    private val scenarioAdapter = ScenarioAdapter { scenario ->
        viewModel.startScenario(scenario)
    }

    override fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentChooseTopicBinding {
        return FragmentChooseTopicBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        binding.recyclerScenarios.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerScenarios.adapter = scenarioAdapter
        binding.toolbar.setNavigationOnClickListener {
            if (!findNavController().navigateUp()) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        if (findNavController().previousBackStackEntry == null) {
            binding.toolbar.navigationIcon = null
        }
    }

    override fun bindView() {
        binding.searchInput.doAfterTextChanged { e ->
            viewModel.onSearchQueryChange(e?.toString().orEmpty())
        }
        binding.filterAll.setOnClickListener { selectFilter(TopicLevelFilter.ALL) }
        binding.filterBeginner.setOnClickListener { selectFilter(TopicLevelFilter.BEGINNER) }
        binding.filterIntermediate.setOnClickListener { selectFilter(TopicLevelFilter.INTERMEDIATE) }
        binding.filterAdvanced.setOnClickListener { selectFilter(TopicLevelFilter.ADVANCED) }
        binding.btnGoal.setOnClickListener { showGoalPicker() }
        binding.btnMode.setOnClickListener { showModePicker() }
        binding.btnFreeTalk.setOnClickListener { viewModel.startFreeTalk() }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectState(viewModel.uiState) { state ->
            applyFilterChips(state.selectedFilter)
            val goalLabel = when (state.goalType.uppercase()) {
                "COMMUNICATION" -> "Goal: Talk"
                "GRAMMAR" -> "Goal: Grammar"
                "FLUENCY" -> "Goal: Fluency"
                else -> "Goal: ${state.goalType.replace('_', ' ')}"
            }
            val modeLabel = when (state.coachingMode.uppercase()) {
                "COACH" -> "Mode: Coach"
                "FLUENCY" -> "Mode: Fluency"
                else -> "Mode: ${state.coachingMode.replace('_', ' ')}"
            }
            binding.btnGoal.text = goalLabel
            binding.btnMode.text = modeLabel
            val displayed = state.displayScenarios()
            scenarioAdapter.submitList(displayed)
            val showEmpty = state.scenariosFetchCompleted && displayed.isEmpty()
            binding.emptyState.isVisible = showEmpty
            binding.emptyState.setText(
                if (state.rawScenarios.isEmpty()) {
                    R.string.ai_scenarios_empty
                } else {
                    R.string.ai_scenarios_empty_filtered
                },
            )
        }
        collectEvent(viewModel.event) { ev ->
            when (ev) {
                is ChooseTopicEvent.OpenChat -> {
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(
                        R.id.action_mainFragment_to_aiChatFragment,
                        bundleOf(
                            "sessionId" to ev.sessionId,
                            "title" to ev.title,
                            "aiRole" to ev.aiRole,
                            "levelName" to ev.levelName,
                            "instruction" to ev.instruction,
                            "goalType" to ev.goalType,
                            "focusSkill" to ev.focusSkill,
                            "coachingMode" to ev.coachingMode,
                        )
                    )
                }
            }
        }
    }

    private fun selectFilter(filter: TopicLevelFilter) {
        viewModel.onFilterSelected(filter)
    }

    private fun applyFilterChips(selected: TopicLevelFilter) {
        styleChip(binding.filterAll, selected == TopicLevelFilter.ALL)
        styleChip(binding.filterBeginner, selected == TopicLevelFilter.BEGINNER)
        styleChip(binding.filterIntermediate, selected == TopicLevelFilter.INTERMEDIATE)
        styleChip(binding.filterAdvanced, selected == TopicLevelFilter.ADVANCED)
    }

    private fun styleChip(view: TextView, selected: Boolean) {
        view.setBackgroundResource(
            if (selected) R.drawable.bg_ai_filter_selected else R.drawable.bg_ai_filter_unselected,
        )
        view.setTextColor(
            ContextCompat.getColor(
                view.context,
                if (selected) R.color.white else R.color.ai_chip_text_muted,
            ),
        )
        view.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun showGoalPicker() {
        val options = arrayOf("COMMUNICATION", "GRAMMAR", "FLUENCY")
        val selected = options.indexOf(viewModel.uiState.value.goalType).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose goal")
            .setSingleChoiceItems(options, selected) { dialog, which ->
                val goal = options[which]
                viewModel.onGoalSelected(goal)
                viewModel.onFocusSelected(if (goal == "COMMUNICATION") "FLUENCY" else goal)
                dialog.dismiss()
            }
            .show()
    }

    private fun showModePicker() {
        val options = arrayOf("COACH", "FLUENCY")
        val selected = options.indexOf(viewModel.uiState.value.coachingMode).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose mode")
            .setSingleChoiceItems(options, selected) { dialog, which ->
                viewModel.onModeSelected(options[which])
                dialog.dismiss()
            }
            .show()
    }
}

private fun ChooseTopicUiState.displayScenarios(): List<AiScenarioDto> {
    val q = searchQuery.trim().lowercase()
    return rawScenarios
        .asSequence()
        .filter { it.id != 0 && it.type != "FREE_CHAT" }
        .filter {
            q.isEmpty() ||
                it.title?.lowercase()?.contains(q) == true ||
                it.description?.lowercase()?.contains(q) == true
        }
        .toList()
}
