package com.shlomikatriel.expensesmanager.ui.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.ChooseIncomeDialogBinding
import com.shlomikatriel.expensesmanager.extensions.showError
import com.shlomikatriel.expensesmanager.logs.Logger
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

    private lateinit var binding: ChooseIncomeDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate<ChooseIncomeDialogBinding>(
            inflater,
            R.layout.choose_income_dialog,
            container,
            false
        ).apply {
            dialog = this@ChooseIncomeDialog
            incomeLayout.prefixText = Currency.getInstance(Locale.getDefault()).symbol
            income.setText(sharedPreferences.getFloat(FloatKey.INCOME).toString(), TextView.BufferType.NORMAL)
        }

        return binding.root
    }

    fun chooseClicked() {
        val income = binding.income.text.toString()
        val incomeBlank = income.isBlank()
        val incomeAsFloat = income.toFloatOrNull()
        Logger.d("Trying to add expense [income=$income, incomeAsFloat=$incomeAsFloat]")
        when {
            incomeBlank -> binding.incomeLayout.showError(appContext, R.string.error_empty_value)
            incomeAsFloat == null -> binding.incomeLayout.showError(appContext, R.string.error_number_illegal)
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