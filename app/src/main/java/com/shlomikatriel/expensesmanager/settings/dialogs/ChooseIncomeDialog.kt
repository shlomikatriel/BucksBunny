package com.shlomikatriel.expensesmanager.settings.dialogs

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shlomikatriel.expensesmanager.LocalizationManager
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.ChooseIncomeDialogBinding
import com.shlomikatriel.expensesmanager.initialize
import com.shlomikatriel.expensesmanager.isInputValid
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.sharedpreferences.putFloat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class ChooseIncomeDialog : DialogFragment() {

    @ApplicationContext
    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var localizationManager: LocalizationManager

    private lateinit var binding: ChooseIncomeDialogBinding

    /**
     * Using nav graph animations doesn't work on dialogs.
     * This is a work around to animate dialog transitions
     *
     * [Stack Overflow](https://stackoverflow.com/questions/57462884/navigation-architecture-component-transition-animations-not-working-for-dialog)
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.choose_income_dialog, null)
        logInfo("Bla Bla")
        val materialDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()
        binding = DataBindingUtil.bind<ChooseIncomeDialogBinding>(view)!!.apply {
            dialog = this@ChooseIncomeDialog
            incomeInputLayout.initialize(
                localizationManager.getCurrencySymbol(),
                sharedPreferences.getFloat(FloatKey.INCOME)
            )
        }
        return materialDialog
    }

    fun chooseClicked() {
        val income = binding.incomeInputLayout.income.text.toString()
        val incomeAsFloat = income.toFloatOrNull()
        logDebug("Trying to add expense [income=$income, incomeAsFloat=$incomeAsFloat]")
        if (binding.incomeInputLayout.isInputValid(appContext)) {
            sharedPreferences.putFloat(FloatKey.INCOME, incomeAsFloat!!)
            findNavController().popBackStack()
        }
    }

    fun cancelClicked() {
        logInfo("Canceling choose income")
        findNavController().popBackStack()
    }
}