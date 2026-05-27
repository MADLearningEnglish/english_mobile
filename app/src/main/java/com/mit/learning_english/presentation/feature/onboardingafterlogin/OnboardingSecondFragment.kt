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

/**
 * Fragment chứa chính quản lý luồng Onboarding sau khi người dùng đăng nhập thành công.
 * Sử dụng ViewPager2 để hiển thị hai bước: Chọn cấp độ học tập và chọn thể loại yêu thích.
 */
@AndroidEntryPoint
class OnboardingSecondFragment : BaseFragment<FragmentOnboardingSecondBinding, OnboardingSecondViewModel>() {
    override val viewModel: OnboardingSecondViewModel by viewModels()

    /**
     * Khởi tạo đối tượng binding cho giao diện fragment từ FragmentOnboardingSecondBinding.
     */
    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentOnboardingSecondBinding {
        return FragmentOnboardingSecondBinding.inflate(layoutInflater, container, false)
    }

    /**
     * Thiết lập ViewPager2, adapter, sự kiện chuyển trang, và xử lý sự kiện click nút Tiếp tục (Next) hoặc Bỏ qua (Skip).
     */
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

    /**
     * Cập nhật văn bản hiển thị cho nút btnNext tùy thuộc vào trang hiện tại (Trang cuối hiển thị "Bắt đầu", trang khác hiển thị "Tiếp tục").
     */
    private fun updateNextButtonLabel(position: Int, pageCount: Int) {
        if (position == pageCount - 1) {
            binding.btnNext.text = ContextCompat.getString(requireContext(), R.string.get_started)
        } else {
            binding.btnNext.text = ContextCompat.getString(requireContext(), R.string.next)
        }
    }

    override fun bindView() {
    }

    /**
     * Quan sát các sự kiện từ ViewModel gửi về để thực hiện chuyển trang tiếp theo hoặc kết thúc onboarding chuyển sang MainGraph.
     */
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

    /**
     * Adapter quản lý danh sách Fragment con bên trong ViewPager2 của Onboarding.
     */
    private inner class OnboardingSecondAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        /**
         * Trả về Fragment tương ứng với vị trí trang hiển thị (0: Chọn cấp độ, 1: Chọn thể loại).
         */
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingChooseLevelFragment()
                else -> OnboardingChooseGenresFragment()
            }
        }
    }
}
