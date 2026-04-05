package com.mit.learning_english.presentation.feature.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileProgressTabBinding
import com.mit.learning_english.databinding.ItemProfileWeekDayBinding
import com.mit.learning_english.domain.model.profile.HeatmapDay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class ProfileProgressTabFragment : Fragment() {

    private var _binding: FragmentProfileProgressTabBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileProgressTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardWordsLearned.setOnClickListener { profileViewModel.openVocabulary() }
        binding.btnDailyActivity.setOnClickListener { profileViewModel.openDailyActivity() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: ProfileUiState) {
        val p = state.profile
        val s = state.stats
        val pct = state.knowledgePercent()
        binding.donutKnowledge.progress = pct / 100f
        binding.tvKnowledgePercent.text = getString(R.string.profile_percent_format, pct)

        binding.tvWordsLearned.text = getString(
            R.string.profile_words_learned_format,
            (s?.wordsLearnedCount ?: 0L).toInt()
        )
        binding.tvCertificates.text = getString(
            R.string.profile_certificates_format,
            (s?.completedLessonsOrExercises ?: 0L).toInt()
        )

        val totalDays = (s?.totalStudyDays ?: 0L).toInt()
        binding.tvActiveDaysSummary.text = buildActiveDaysSummary(totalDays)

        binding.tvRatingCorrection.text = getString(
            R.string.profile_rating_correction_format,
            (s?.completedLessonsOrExercises ?: 0L).toInt().coerceAtMost(999)
        )
        binding.tvRatingLikes.text = getString(
            R.string.profile_rating_likes_format,
            (s?.wordsLearnedCount ?: 0L).toInt().coerceAtMost(999)
        )
        binding.tvRatingBest.text = getString(
            R.string.profile_rating_best_format,
            (s?.currentStreakDays ?: 0).coerceAtMost(999)
        )

        buildWeekRow(state.heatmap)
    }

    private fun buildActiveDaysSummary(totalDays: Int): CharSequence {
        val numStr = totalDays.toString()
        val suffix = if (totalDays == 1) " Active day" else " Active days"
        val ss = SpannableStringBuilder()
        val primary = ContextCompat.getColor(requireContext(), R.color.primary)
        val start = ss.length
        ss.append(numStr)
        ss.setSpan(
            ForegroundColorSpan(primary),
            start,
            ss.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.append(suffix)
        return ss
    }

    private fun buildWeekRow(heatmap: List<HeatmapDay>) {
        binding.rowWeekDays.removeAllViews()
        val today = java.time.LocalDate.now()
        var d = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val inflater = LayoutInflater.from(requireContext())
        val map = heatmap.associateBy { it.date }
        for (i in 0 until 7) {
            val dayBinding = ItemProfileWeekDayBinding.inflate(inflater, binding.rowWeekDays, false)
            val label = d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US).take(2)
            dayBinding.tvWeekdayLabel.text = label
            val info = map[d]
            val active = info != null && (info.activityCount > 0 || info.totalMinutes > 0)
            dayBinding.dayCircle.setBackgroundResource(
                if (active) R.drawable.bg_profile_week_active else R.drawable.bg_profile_week_inactive
            )
            val checkColor = ContextCompat.getColor(
                requireContext(),
                if (active) R.color.white else R.color.gray_400
            )
            ImageViewCompat.setImageTintList(dayBinding.imgCheck, ColorStateList.valueOf(checkColor))
            dayBinding.imgCheck.alpha = if (active) 1f else 0.5f
            val date = d
            dayBinding.root.setOnClickListener {
                profileViewModel.openActivityDay(date)
            }
            binding.rowWeekDays.addView(dayBinding.root)
            d = d.plusDays(1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
