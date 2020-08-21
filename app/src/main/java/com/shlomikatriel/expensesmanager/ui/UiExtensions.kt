package com.shlomikatriel.expensesmanager.ui

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.shlomikatriel.expensesmanager.logs.Logger

fun Fragment.configureToolbar(
    @StringRes title: Int,
    navigateUpEnabled: Boolean = false
) {
    setHasOptionsMenu(true)
    (activity as MainActivity?)?.configureToolbar(
        title,
        navigateUpEnabled
    ) ?: Logger.e("Main activity is null for fragment '${javaClass.simpleName}'")
}

fun View.startPopAnimation() {
    val animation = ScaleAnimation(
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
    startAnimation(animation)
}