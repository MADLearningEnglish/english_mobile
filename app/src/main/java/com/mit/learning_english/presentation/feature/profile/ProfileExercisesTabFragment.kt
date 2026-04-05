package com.mit.learning_english.presentation.feature.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mit.learning_english.databinding.FragmentProfileExercisesTabBinding
import kotlinx.coroutines.launch

class ProfileExercisesTabFragment : Fragment() {

    private var _binding: FragmentProfileExercisesTabBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels({ requireParentFragment() })
    private val adapter = ProfileActivityRowAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileExercisesTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    val list = state.todayActivities?.activities.orEmpty()
                    adapter.submitList(list)
                    val empty = list.isEmpty()
                    binding.tvExercisesEmpty.visibility = if (empty) View.VISIBLE else View.GONE
                    binding.rvActivities.visibility = if (empty) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
