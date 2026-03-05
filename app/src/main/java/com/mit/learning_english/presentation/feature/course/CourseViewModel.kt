package com.mit.learning_english.presentation.feature.course

import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor() :
    BaseViewModel<CourseState, CourseEvent>(CourseState()) {}
