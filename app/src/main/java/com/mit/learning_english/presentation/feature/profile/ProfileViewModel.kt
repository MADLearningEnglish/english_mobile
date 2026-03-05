package com.mit.learning_english.presentation.feature.profile

import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() :
    BaseViewModel<ProfileState, ProfileEvent>(ProfileState()) {}
