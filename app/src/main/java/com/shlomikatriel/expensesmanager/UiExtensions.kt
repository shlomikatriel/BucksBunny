package com.shlomikatriel.expensesmanager

import androidx.fragment.app.Fragment
import com.shlomikatriel.expensesmanager.logs.logError

fun Fragment.configureToolbar() {
    if (activity is MainActivity) {
        (activity as MainActivity).showToolbar()
    } else {
        logError("Can't configure toolbar, main activity is null for fragment '${javaClass.simpleName}'")
    }
}

fun Fragment.hideToolbar() {
    if (activity is MainActivity) {
        (activity as MainActivity).hideToolbar()
    } else {
        logError("Can't hide toolbar, main activity is null for fragment '${javaClass.simpleName}'")
    }
}