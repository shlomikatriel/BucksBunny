package com.shlomikatriel.expensesmanager.ui.dialogs

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.shlomikatriel.expensesmanager.R

abstract class BaseDialog : DialogFragment() {

    /**
     * Using nav graph animations doesn't work on dialogs.
     * This is a work around to animate dialog transitions
     *
     * [Stack Overflow](https://stackoverflow.com/questions/57462884/navigation-architecture-component-transition-animations-not-working-for-dialog)
     * */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(getWidthDimension(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun getWidthDimension() = if (resources.getBoolean(R.bool.is_tablet)) {
        resources.getDimensionPixelSize(R.dimen.fragment_width)
    } else {
        ConstraintLayout.LayoutParams.MATCH_PARENT
    }
}