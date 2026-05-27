package com.mit.learning_english.presentation.feature.onboardingafterlogin

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
import com.mit.learning_english.databinding.FragmentOnboardingChooseLevelBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment cho phép người dùng lựa chọn trình độ tiếng Anh của mình (như A1, A2, B1, B2...) trong quá trình onboarding.
 */
@AndroidEntryPoint
class OnboardingChooseLevelFragment : Fragment() {

    private var _binding: FragmentOnboardingChooseLevelBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: OnboardingSecondViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private val adapter = OnboardingLevelAdapter { levelId ->
        sharedViewModel.selectLevel(levelId)
    }

    /**
     * Tạo và khởi tạo giao diện Fragment từ FragmentOnboardingChooseLevelBinding.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingChooseLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Thiết lập danh sách các trình độ học tập bằng LinearLayoutManager và adapter,
     * đồng thời lắng nghe trạng thái của các trình độ từ ViewModel để cập nhật UI tương ứng.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvLevels.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLevels.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.uiState.collectLatest { state ->
                    binding.rvLevels.isEnabled = state.levels.isNotEmpty()
                    adapter.submitLevels(state.levels, state.selectedLevelId)
                }
            }
        }
    }

    /**
     * Hủy bỏ binding khi giao diện Fragment bị hủy nhằm tránh rò rỉ bộ nhớ.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
