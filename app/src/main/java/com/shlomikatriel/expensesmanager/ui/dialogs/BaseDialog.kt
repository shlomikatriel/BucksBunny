package com.shlomikatriel.expensesmanager.ui.dialogs

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment

abstract class BaseDialog: DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
    }
}