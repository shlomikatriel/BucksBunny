package com.shlomikatriel.expensesmanager.ui

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