package com.mit.learning_english.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Base Fragment với ViewBinding và ViewModel integration.
 * Loading và error lấy từ uiState (BaseUiState) - single source of truth.
 *
 * @param VB ViewBinding type
 * @param VM BaseViewModel type (STATE phải extend BaseUiState)
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel<*, *>> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    /**
     * Tạo ViewBinding. Thường dùng: XxxBinding.inflate(inflater, container, false)
     */
    abstract fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Setup UI: toolbar, adapter, visibility ban đầu...
     */
    abstract fun setupView()

    /**
     * Bind data: gọi ViewModel, set listeners, bind dữ liệu...
     */
    abstract fun bindView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = verifyBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        bindView()
        observeViewModel()
    }

    /**
     * Observe ViewModel. Mặc định observe loading và error từ uiState.
     * Override để thêm observe uiState chi tiết, event...
     */
    protected open fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.isLoading) showLoading() else hideLoading()
                    state.errorMessage?.let { showError(it) }
                }
            }
        }
    }

    /**
     * Collect StateFlow an toàn với lifecycle.
     * Dùng trong observeViewModel() khi override.
     */
    protected fun <T> collectState(
        flow: StateFlow<T>,
        action: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(action)
            }
        }
    }

    protected fun <T> collectEvent(
        flow: SharedFlow<T>,
        action: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(action)
            }
        }
    }

    protected open fun showLoading() {
        // Override để hiển thị loading dialog/progress
    }

    protected open fun hideLoading() {
        // Override để ẩn loading
    }

    protected open fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
