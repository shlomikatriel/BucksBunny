package com.shlomikatriel.expensesmanager.settings.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shlomikatriel.expensesmanager.*
import com.shlomikatriel.expensesmanager.databinding.ChooseIncomeDialogBinding
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.sharedpreferences.putFloat
import java.util.*
import javax.inject.Inject

class ChooseIncomeDialog : BaseDialog() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var currency: Currency

    private lateinit var binding: ChooseIncomeDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)
    }

    override fun layout() = R.layout.choose_income_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<ChooseIncomeDialogBinding>(view)!!.apply {
            dialog = this@ChooseIncomeDialog
            incomeInputLayout.initialize(
                currency.symbol,
                sharedPreferences.getFloat(FloatKey.INCOME)
            )
        }
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