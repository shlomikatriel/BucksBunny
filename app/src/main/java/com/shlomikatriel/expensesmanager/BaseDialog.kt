package com.shlomikatriel.expensesmanager

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(layout(), null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()
        bind(view)
        return dialog
    }
    
    @LayoutRes
    abstract fun layout(): Int

    abstract fun bind(view: View)
}