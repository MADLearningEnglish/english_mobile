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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.mit.learning_english.R
import com.mit.learning_english.shared.UiErrorKey
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce

/**
 * Base Fragment với ViewBinding và ViewModel integration.
 * Loading lấy từ uiState, error lấy từ errorEvent (one-time SharedFlow).
 *
 * @param VB ViewBinding type
 * @param VM BaseViewModel type
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel<*, *>> : Fragment() {

    private var loadingStartTime = 0L
    private val MIN_LOADING_TIME = 500L

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
//        loadingStartTime = System.currentTimeMillis()
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
//        val elapsed = System.currentTimeMillis() - loadingStartTime
//        val remaining = (MIN_LOADING_TIME - elapsed).coerceAtLeast(0)
//        lifecycleScope.launch {
//            delay(remaining)
//            loadingDialog?.dismiss()
//        }
    }

    protected open fun showError(message: String) {
        Toast.makeText(requireContext(), resolveUiMessage(message), Toast.LENGTH_SHORT).show()
        Log.d("Error Fragment",message)
    }

    protected fun resolveUiMessage(message: String): String {
        val resId = when (message) {
            UiErrorKey.UNKNOWN -> R.string.error_unknown
            UiErrorKey.INVALID_SESSION -> R.string.error_invalid_session
            UiErrorKey.LOGIN_FAILED -> R.string.error_login_failed
            UiErrorKey.SIGNUP_FAILED -> R.string.error_signup_failed
            UiErrorKey.UPLOAD_FAILED -> R.string.error_upload_failed
            UiErrorKey.FAILED_LOAD_PAGES -> R.string.error_failed_load_pages
            UiErrorKey.FAILED_LOAD_BOOKS -> R.string.error_failed_load_books
            UiErrorKey.FAILED_SEARCH_BOOKS -> R.string.error_failed_search_books
            UiErrorKey.FAILED_LOAD_AUTHORS -> R.string.error_failed_load_authors
            UiErrorKey.FAILED_LOAD_BOOK_DETAIL -> R.string.error_failed_load_book_detail
            UiErrorKey.CANNOT_READ_IMAGE -> R.string.error_cannot_read_image
            UiErrorKey.CANNOT_GET_RESULTS -> R.string.error_cannot_get_results
            UiErrorKey.UPDATE_PROGRESS_FAILED -> R.string.error_update_progress_failed
            UiErrorKey.HTTP_ERROR -> R.string.error_http_error
            UiErrorKey.DICTIONARY_CONNECTION -> R.string.error_dictionary_connection
            UiErrorKey.ERROR_UPLOADING_FILE -> R.string.error_uploading_file
            UiErrorKey.OTP_OR_EMAIL_MISSING -> R.string.error_otp_or_email_missing
            UiErrorKey.PASSWORD_MISMATCH_OR_EMPTY -> R.string.error_password_mismatch_or_empty
            UiErrorKey.EMAIL_REQUIRED -> R.string.error_email_required
            UiErrorKey.COULD_NOT_LOAD_PROFILE -> R.string.error_could_not_load_profile
            UiErrorKey.COULD_NOT_LOAD_STATS -> R.string.error_could_not_load_stats
            UiErrorKey.LOAD_DATA_VI -> R.string.error_load_data_vi
            UiErrorKey.CREATE_DECK_VI -> R.string.error_create_deck_vi
            UiErrorKey.DELETE_FAILED -> R.string.error_delete_failed
            UiErrorKey.REVIEW_FAILED -> R.string.error_review_failed
            UiErrorKey.STUDY_COMPLETE_FAILED -> R.string.error_study_complete_failed
            UiErrorKey.AUTH_SIGNUP_VI -> R.string.error_auth_signup_vi
            UiErrorKey.FILL_TERM_DEFINITION -> R.string.error_please_fill_term_definition
            else -> 0
        }
        return if (resId != 0) getString(resId) else message
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        loadingDialog?.dismiss()
//        loadingDialog = null
        _binding = null
    }
}
