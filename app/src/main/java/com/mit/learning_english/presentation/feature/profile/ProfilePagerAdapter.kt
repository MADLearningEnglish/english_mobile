package com.mit.learning_english.presentation.feature.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ProfileProgressTabFragment()
        1 -> ProfileExercisesTabFragment()
        else -> ProfileCorrectionsTabFragment()
    }
}
