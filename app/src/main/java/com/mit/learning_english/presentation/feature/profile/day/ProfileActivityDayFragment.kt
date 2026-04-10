package com.mit.learning_english.presentation.feature.profile.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileActivityDayBinding
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.feature.profile.ProfileActivityRowAdapter
import com.mit.learning_english.presentation.feature.profile.timelineCategoryKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivityDayFragment : Fragment() {

    private var _binding: FragmentProfileActivityDayBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var profileRepository: ProfileRepository

    private val adapter = ProfileActivityRowAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileActivityDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iso = requireArguments().getString("isoDate")
        if (iso == null) {
            findNavController().navigateUp()
            return
        }
        val date = LocalDate.parse(iso)
        binding.toolbar.title = getString(
            R.string.activity_day_title_format,
            date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US))
        )
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.rvTimeline.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTimeline.adapter = adapter
        adapter.onItemClick = { item ->
            item.timelineCategoryKey()?.let { category ->
                findNavController().navigate(
                    R.id.action_profileActivityDayFragment_to_profileActivityCategoryFragment,
                    bundleOf(
                        "isoDate" to date.toString(),
                        "category" to category
                    )
                )
            }
        }

        binding.tvTimelineDate.text = date.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.US)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            when (val r = profileRepository.getActivityDay(date)) {
                is Result.Success -> {
                    val d = r.data
                    binding.tvTotalTime.text = "${d.totalMinutes}m"
                    binding.tvActivityCount.text = d.activityCount.toString()
                    val avg = d.averageScorePercent
                    binding.tvAvgScore.text =
                        if (avg != null) "${avg.toInt()}%" else "—"
                    binding.tvInsightBody.text = d.dailyInsight
                        ?: getString(R.string.activity_day_no_insight)
                    binding.cardInsight.visibility =
                        if (d.dailyInsight.isNullOrBlank()) View.GONE else View.VISIBLE
                    adapter.submitList(d.activities)
                }
                is Result.Error -> binding.tvInsightBody.text = r.message
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
