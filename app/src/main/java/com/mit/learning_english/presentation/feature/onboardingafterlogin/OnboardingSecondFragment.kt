package com.mit.learning_english.presentation.feature.onboardingafterlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentOnboardingSecondBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingSecondFragment : BaseFragment<FragmentOnboardingSecondBinding, OnboardingSecondViewModel>() {
    override val viewModel: OnboardingSecondViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentOnboardingSecondBinding {
        return FragmentOnboardingSecondBinding.inflate(layoutInflater, container, false)
    }

    override fun setupView() {
        val adapter = OnboardingSecondAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = adapter.itemCount

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNextButtonLabel(position, adapter.itemCount)
            }
        })
        updateNextButtonLabel(binding.viewPager.currentItem, adapter.itemCount)

        binding.btnNext.setOnClickListener {
            when (binding.viewPager.currentItem) {
                0 -> viewModel.submitLevelAndContinue()
                1 -> viewModel.submitGenresAndFinish()
                else -> Unit
            }
        }

        binding.btnSkip.setOnClickListener {
            viewModel.skipOnboarding()
        }
    }

    private fun updateNextButtonLabel(position: Int, pageCount: Int) {
        if (position == pageCount - 1) {
            binding.btnNext.text = ContextCompat.getString(requireContext(), R.string.get_started)
        } else {
            binding.btnNext.text = ContextCompat.getString(requireContext(), R.string.next)
        }
    }

    override fun bindView() {
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectEvent(viewModel.event) { event ->
            when (event) {
                OnboardingSecondEvent.AdvancePage -> {
                    val next = binding.viewPager.currentItem + 1
                    if (next < (binding.viewPager.adapter?.itemCount ?: 0)) {
                        binding.viewPager.currentItem = next
                    }
                }
                OnboardingSecondEvent.Complete -> {
                    val action= OnboardingSecondFragmentDirections.actionOnboardingSecondFragmentToMainGraph()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private inner class OnboardingSecondAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingChooseLevelFragment()
                else -> OnboardingChooseGenresFragment()
            }
        }
    }
}
