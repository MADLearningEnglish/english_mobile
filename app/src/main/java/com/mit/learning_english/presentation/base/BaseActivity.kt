package com.mit.learning_english.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: VM

    abstract fun inflateBinding(inflater: LayoutInflater): VB
    /**
     * Setup Toolbar
     * Init RecyclerView
     * Set adapter
     * Set click listener
     * Config animation, visibility ban đầu
     */
    abstract fun setupView()

    /**
     * Gọi function trong ViewModel
     * Set listener gọi ViewModel
     * Bind dữ liệu ban đầu
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

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        viewModel.errorMessage.observe(this) { message ->
            showError(message)
        }
    }

    protected open fun showLoading() {
        // Override to show loading
    }

    protected open fun hideLoading() {
        // Override to hide loading
    }

    protected open fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
