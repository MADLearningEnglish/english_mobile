package com.mit.learning_english.presentation.feature.profile.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileActivityCategoryBinding
import com.mit.learning_english.domain.model.profile.LearningActivityItem
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.feature.profile.ProfileActivityCategoryKey
import com.mit.learning_english.presentation.feature.profile.matchesTimelineCategory
import com.mit.learning_english.presentation.feature.profile.ProfileActivityRowAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivityCategoryFragment : Fragment() {

    private var _binding: FragmentProfileActivityCategoryBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var profileRepository: ProfileRepository

    private val listAdapter = ProfileActivityRowAdapter(
        onItemClick = { item -> openActivityTarget(item) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileActivityCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iso = requireArguments().getString("isoDate")
        val category = requireArguments().getString("category")
        if (iso == null || category == null) {
            findNavController().navigateUp()
            return
        }
        val date = LocalDate.parse(iso)
        val dateLabel = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US))
        val categoryLabel = when (category) {
            ProfileActivityCategoryKey.FLASHCARD -> getString(R.string.profile_activity_category_flashcards)
            ProfileActivityCategoryKey.LESSON_AND_EXERCISE -> getString(R.string.profile_activity_category_lessons)
            ProfileActivityCategoryKey.AI_CHAT -> getString(R.string.profile_activity_category_ai_chats)
            else -> category
        }
        binding.toolbar.title = getString(R.string.profile_activity_category_title_format, categoryLabel, dateLabel)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.adapter = listAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            when (val r = profileRepository.getActivityDay(date)) {
                is Result.Success -> {
                    val filtered = r.data.activities.filter { it.matchesTimelineCategory(category) }
                    listAdapter.submitList(filtered)
                    binding.tvEmpty.isVisible = filtered.isEmpty()
                    binding.rvActivities.isVisible = filtered.isNotEmpty()
                }
                is Result.Error -> {
                    binding.tvEmpty.text = r.message
                    binding.tvEmpty.isVisible = true
                    binding.rvActivities.isVisible = false
                }
                else -> {}
            }
        }
    }

    private fun openActivityTarget(item: LearningActivityItem) {
        val nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val type = item.activityType?.uppercase().orEmpty()
        val refType = item.referenceType?.uppercase().orEmpty()
        val refId = item.referenceId
        when {
            type.contains("FLASHCARD") && refType == "DECK" && refId != null -> {
                nav.navigate(
                    R.id.action_mainFragment_to_studyFragment,
                    bundleOf(
                        "deckId" to refId,
                        "deckTitle" to (item.title ?: "")
                    )
                )
            }
            type.contains("AI") && refType == "AI_CHAT_SESSION" && refId != null -> {
                nav.navigate(
                    R.id.action_mainFragment_to_aiChatFragment,
                    bundleOf(
                        "sessionId" to refId,
                        "title" to (item.title ?: ""),
                        "aiRole" to "",
                        "levelName" to "",
                        "instruction" to ""
                    )
                )
            }
            (type.contains("LESSON") || type.contains("EXERCISE")) && refType == "BOOK" && refId != null -> {
                nav.navigate(
                    R.id.action_mainFragment_to_bookDetailFragment,
                    bundleOf("bookId" to refId)
                )
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    R.string.profile_activity_open_unavailable,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
