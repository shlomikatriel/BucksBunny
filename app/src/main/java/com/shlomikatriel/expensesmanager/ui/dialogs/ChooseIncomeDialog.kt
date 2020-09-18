package com.shlomikatriel.expensesmanager.ui.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.ChooseIncomeDialogBinding
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.sharedpreferences.FloatKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getFloat
import com.shlomikatriel.expensesmanager.sharedpreferences.putFloat
import com.shlomikatriel.expensesmanager.ui.showError
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

    private val args: ChooseIncomeDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)
    }

    override fun layout() = R.layout.choose_income_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<ChooseIncomeDialogBinding>(view)!!.apply {
            fromOnBoarding = args.fromOnBoarding
            dialog = this@ChooseIncomeDialog
            incomeLayout.prefixText = currency.symbol
            income.setText(
                sharedPreferences.getFloat(FloatKey.INCOME).toString(),
                TextView.BufferType.NORMAL
            )
        }
    }

    fun chooseClicked() {
        val income = binding.income.text.toString()
        val incomeBlank = income.isBlank()
        val incomeAsFloat = income.toFloatOrNull()
        Logger.d("Trying to add expense [income=$income, incomeAsFloat=$incomeAsFloat]")
        when {
            incomeBlank -> binding.incomeLayout.showError(appContext, R.string.error_empty_value)
            incomeAsFloat == null -> binding.incomeLayout.showError(
                appContext,
                R.string.error_number_illegal
            )
            else -> {
                sharedPreferences.putFloat(FloatKey.INCOME, incomeAsFloat)
                findNavController().popBackStack()
            }
        }
    }

    fun cancelClicked() {
        Logger.i("Canceling choose income")
        findNavController().popBackStack()
    }
}