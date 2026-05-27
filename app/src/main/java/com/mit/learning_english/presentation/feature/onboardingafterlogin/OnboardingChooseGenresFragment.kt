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
import androidx.recyclerview.widget.GridLayoutManager
import com.mit.learning_english.databinding.FragmentOnboardingChooseGenresBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment cho phép người dùng lựa chọn các thể loại sách yêu thích trong quá trình onboarding sau khi đăng nhập.
 */
@AndroidEntryPoint
class OnboardingChooseGenresFragment : Fragment() {

    private var _binding: FragmentOnboardingChooseGenresBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: OnboardingSecondViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private val adapter = OnboardingGenreGridAdapter { genreId ->
        sharedViewModel.toggleGenre(genreId)
    }

    /**
     * Tạo và khởi tạo giao diện Fragment bằng cách inflate layout FragmentOnboardingChooseGenresBinding.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingChooseGenresBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Thiết lập danh sách RecyclerView với Grid Layout 2 cột, gán adapter
     * và lắng nghe sự thay đổi trạng thái danh sách thể loại từ ViewModel để cập nhật UI.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvGenre.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGenre.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.uiState.collectLatest { state ->
                    adapter.submitGenres(state.genres, state.selectedGenreIds)
                }
            }
        }
    }

    /**
     * Giải phóng liên kết view binding khi Fragment bị hủy view để tránh rò rỉ bộ nhớ.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
