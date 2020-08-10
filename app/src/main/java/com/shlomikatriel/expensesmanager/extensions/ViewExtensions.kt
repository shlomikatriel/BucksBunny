package com.shlomikatriel.expensesmanager.extensions

import android.content.Context
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.showError(context: Context, @StringRes errorRes: Int) {
    error = context.getString(errorRes)
    postDelayed({
        error = null
    }, 1500L)
}