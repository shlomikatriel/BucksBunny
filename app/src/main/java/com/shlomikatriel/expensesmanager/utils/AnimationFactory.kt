package com.shlomikatriel.expensesmanager.utils

import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import javax.inject.Inject

class AnimationFactory @Inject constructor() {

    fun createPopAnimation() = ScaleAnimation(
        1f,
        1.4f,
        1f,
        1.4f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    ).apply {
        duration = 150
        repeatCount = 1
        repeatMode = Animation.REVERSE
    }
}