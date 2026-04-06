package com.mit.learning_english.presentation.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.mit.learning_english.R
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.doOnPreDraw
import kotlinx.coroutines.flow.debounce

/**
 * Base Fragment với ViewBinding và ViewModel integration.
 * Loading lấy từ uiState, error lấy từ errorEvent (one-time SharedFlow).
 *
 * @param VB ViewBinding type
 * @param VM BaseViewModel type
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel<*, *>> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

//    private var loadingDialog: Dialog? = null

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        setupView()
        bindView()
        observeViewModel()
    }

    /**
     * Observe ViewModel. Mặc định observe loading từ uiState và error từ errorEvent.
     * Override để thêm observe uiState chi tiết, event...
     */
    protected open fun observeViewModel() {
        collectState(viewModel.isLoading) { loading ->
            if (loading) showLoading() else hideLoading()
        }
        collectEvent(viewModel.errorEvent) { message ->
            showError(message)
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

    /**
     * Collect một thuộc tính cụ thể của StateFlow, chỉ trigger khi giá trị thực sự thay đổi.
     * Tránh unnecessary UI update khi các thuộc tính khác trong state thay đổi.
     */
    protected fun <T, R> collectStateProperty(
        flow: StateFlow<T>,
        selector: (T) -> R,
        action: suspend (R) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.map(selector).distinctUntilChanged().collectLatest(action)
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
//        val ctx = context ?: return
//        if (loadingDialog == null) {
//            loadingDialog = Dialog(ctx).apply {
//                setContentView(R.layout.dialog_loading)
//                window?.setBackgroundDrawable(ContextCompat.getColor(requireContext(), R.color.body_on_primary).toDrawable())
//                window?.setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
//                )
//                setCancelable(false)
//            }
//        }
//        if (loadingDialog?.isShowing == false) {
//            loadingDialog?.show()
//        }
    }

    protected open fun hideLoading() {
//        loadingDialog?.dismiss()
    }

    protected open fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.d("Error Fragment ",message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        loadingDialog?.dismiss()
//        loadingDialog = null
        _binding = null
    }
}
