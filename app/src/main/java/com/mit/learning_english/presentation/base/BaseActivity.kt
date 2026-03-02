package com.mit.learning_english.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Base Activity với ViewBinding và ViewModel integration.
 * Loading và error lấy từ uiState (BaseUiState) - single source of truth.
 *
 * @param VB ViewBinding type
 * @param VM BaseViewModel type (STATE phải extend BaseUiState)
 */
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel<*, *>> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    /**
     * Tạo ViewBinding.
     */
    abstract fun inflateBinding(inflater: LayoutInflater): VB

    /**
     * Setup Toolbar, RecyclerView, adapter, click listener, animation, visibility...
     */
    abstract fun setupView()

    /**
     * Gọi function ViewModel, set listener, bind dữ liệu ban đầu...
     */
    abstract fun bindView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding(layoutInflater)
        setContentView(binding.root)

        setupView()
        bindView()
        observeViewModel()
    }

    /**
     * Observe ViewModel. Mặc định observe loading và error từ uiState.
     */
    protected open fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state.isLoading) showLoading() else hideLoading()
                    state.errorMessage?.let { showError(it) }
                }
            }
        }
    }

    /**
     * Collect StateFlow an toàn với lifecycle.
     */
    protected fun <T> collectState(
        flow: StateFlow<T>,
        action: suspend (T) -> Unit
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(action)
            }
        }
    }

    protected open fun showLoading() {
        // Override để hiển thị loading
    }

    protected open fun hideLoading() {
        // Override để ẩn loading
    }

    protected open fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
