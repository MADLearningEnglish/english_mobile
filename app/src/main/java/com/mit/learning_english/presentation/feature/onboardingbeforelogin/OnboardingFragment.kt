package com.mit.learning_english.presentation.feature.onboardingbeforelogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentOnboardingBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : BaseFragment<FragmentOnboardingBinding,OnboardingViewModel>() {
    override val viewModel: OnboardingViewModel by viewModels()
    
    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentOnboardingBinding {
        return FragmentOnboardingBinding.inflate(layoutInflater,container,false)
    }

    override fun setupView() {
        val adapter = OnboardingAdapter(this)
        binding.viewPager.adapter = adapter
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) {
                    binding.btnNext.text = ContextCompat.getString(requireContext(),R.string.get_started)
                } else {
                    binding.btnNext.text = ContextCompat.getString(requireContext(),R.string.next)
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < adapter.itemCount - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                viewModel.setOnboardingCompleted()
            }
        }

    }

    override fun bindView() {
        binding.btnSkip.setOnClickListener {
            viewModel.setOnboardingCompleted()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        collectEvent(viewModel.event){event ->
           if(event is OnboardingEvent.NavigateToLogin ){
               val action = OnboardingFragmentDirections.actionOnboardingFragmentToLoginFragment()
               findNavController().navigate(action)
           }
        }
    }

    private inner class OnboardingAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingPage1Fragment()
                1 -> OnboardingPage2Fragment()
                2 -> OnboardingPage3Fragment()
                else -> OnboardingPage1Fragment()
            }
        }
    }
}