package com.mit.learning_english.presentation.feature.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.mit.learning_english.R
import com.mit.learning_english.databinding.FragmentProfileBinding
import com.mit.learning_english.presentation.base.BaseFragment
import com.mit.learning_english.shared.MediaUrlResolver
import java.util.Locale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()

    override fun verifyBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)

    override fun setupView() {
        binding.toolbarProfile.inflateMenu(R.menu.profile_toolbar_menu)
        binding.toolbarProfile.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    viewModel.openEditProfile()
                    true
                }
                R.id.action_add_friend -> {
                    true
                }
                else -> false
            }
        }
        binding.cardProfileHeader.setOnClickListener {
            viewModel.openEditProfile()
        }
        binding.viewPagerProfile.adapter = ProfilePagerAdapter(this)
        binding.tabLayoutProfile.removeAllTabs()
        binding.tabLayoutProfile.addTab(
            binding.tabLayoutProfile.newTab().setText(getString(R.string.profile_tab_progress))
        )
        binding.tabLayoutProfile.addTab(
            binding.tabLayoutProfile.newTab().setText(getString(R.string.profile_tab_corrections))
        )
        binding.tabLayoutProfile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> binding.viewPagerProfile.setCurrentItem(0, false)
                    1 -> viewModel.openMyCorrections()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        binding.tabLayoutProfile.selectTab(binding.tabLayoutProfile.getTabAt(0))
        binding.viewPagerProfile.setCurrentItem(0, false)
    }

    override fun bindView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        val p = state.profile
                        binding.tvDisplayName.text = p?.fullName ?: "—"
                        binding.tvLocation.text = p?.location ?: "—"
                        val level = p?.levelName ?: p?.learningLevel
                        binding.tvLevelBadge.text = formatLevelLabel(level)
                        binding.tvFriendsCount.text = "0"
                        val avatar = MediaUrlResolver.resolve(p?.avatarUrl)
                        if (!avatar.isNullOrBlank()) {
                            Glide.with(this@ProfileFragment)
                                .load(avatar)
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(binding.imgAvatar)
                        } else {
                            binding.imgAvatar.setImageResource(R.drawable.ic_profile_placeholder)
                        }
                    }
                }
                launch {
                    viewModel.event.collect { ev ->
                        val nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        when (ev) {
                            is ProfileEvent.OpenEditProfile ->
                                nav.navigate(R.id.action_mainFragment_to_editProfileFragment)
                            is ProfileEvent.OpenVocabularyList ->
                                nav.navigate(R.id.action_mainFragment_to_profileVocabularyFragment)
                            is ProfileEvent.OpenMyCorrections ->
                                nav.navigate(R.id.action_mainFragment_to_profileMyCorrectionsFragment)
                            is ProfileEvent.OpenDailyActivity ->
                                nav.navigate(R.id.action_mainFragment_to_profileDailyActivityFragment)
                            is ProfileEvent.OpenActivityDay ->
                                nav.navigate(
                                    R.id.action_mainFragment_to_profileActivityDayFragment,
                                    bundleOf("isoDate" to ev.date.toString())
                                )
                        }
                    }
                }
            }
        }
    }

    private fun formatLevelLabel(raw: String?): String {
        if (raw.isNullOrBlank()) return "—"
        return raw.trim().split(Regex("[\\s_]+")).filter { it.isNotEmpty() }
            .joinToString(" ") { part ->
                part.lowercase(Locale.US).replaceFirstChar { it.titlecase(Locale.US) }
            }
    }
}
