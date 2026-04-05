package com.mit.learning_english.presentation.feature.profile.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileDailyActivityBinding
import com.mit.learning_english.domain.repository.ProfileRepository
import com.mit.learning_english.domain.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDailyActivityFragment : Fragment() {

    private var _binding: FragmentProfileDailyActivityBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var profileRepository: ProfileRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDailyActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            val me = profileRepository.getMe()
            val st = profileRepository.getStatsOverview()
            val day = profileRepository.getActivityDay(LocalDate.now())
            if (me is Result.Success && st is Result.Success) {
                val goalMin = me.data.dailyGoalMinutes ?: 30
                val todayMin = if (day is Result.Success) day.data.totalMinutes else 0
                val frac = (todayMin.toFloat() / goalMin.coerceAtLeast(1)).coerceIn(0f, 1f)
                binding.donutGoal.progress = frac
                binding.tvGoalFraction.text = "${todayMin.coerceAtMost(goalMin)}/$goalMin"
                binding.tvGoalHint.text = getString(R.string.daily_goal_hint_format, (frac * 100).toInt())
                binding.tvStreak.text = getString(R.string.daily_streak_format, st.data.currentStreakDays)
            }
            binding.tvDailySummary.text = getString(R.string.daily_activity_footer)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
