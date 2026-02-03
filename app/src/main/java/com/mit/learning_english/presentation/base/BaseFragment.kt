package com.mit.learning_english.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    abstract fun verifyBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun setupView()
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

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            showError(message)
        }
    }

    protected open fun showLoading() {
        // Override to show loading dialog/progress
    }

    protected open fun hideLoading() {
        // Override to hide loading dialog/progress
    }

    protected open fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
