package com.mit.learning_english.domain.repository
import com.mit.learning_english.domain.util.Result
interface OnboardingRepository {
     suspend fun checkSeenOnboardingBeforeLogin(): Boolean

     suspend fun updateBeforeLoginOnboardingStatus(hasSeen:Boolean)

     suspend fun hasCompletedAfterLoginOnboarding(): Result<Boolean>

     suspend fun setAfterLoginOnboardingCompleted(completed: Boolean):Result<Boolean>
}
