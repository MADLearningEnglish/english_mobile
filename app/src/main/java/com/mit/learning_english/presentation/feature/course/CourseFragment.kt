package com.mit.learning_english.presentation.feature.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mit.learning_english.databinding.FragmentCourseBinding
import com.mit.learning_english.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseFragment() : BaseFragment<FragmentCourseBinding, CourseViewModel>() {
    override val viewModel: CourseViewModel by viewModels()
    override fun verifyBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentCourseBinding {
        return FragmentCourseBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
    }

    override fun bindView() {
    }
}