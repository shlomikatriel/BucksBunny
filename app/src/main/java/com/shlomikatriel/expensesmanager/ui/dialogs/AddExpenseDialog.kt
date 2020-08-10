package com.shlomikatriel.expensesmanager.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.ExpenseDao
import com.shlomikatriel.expensesmanager.databinding.AddExpenseDialogBinding
import com.shlomikatriel.expensesmanager.extensions.showError
import com.shlomikatriel.expensesmanager.logs.Logger
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

class AddExpenseDialog : BaseDialog() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var expenseDao: ExpenseDao

    lateinit var binding: AddExpenseDialogBinding

    private val args: AddExpenseDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate<AddExpenseDialogBinding>(
            inflater,
            R.layout.add_expense_dialog,
            container,
            false
        ).apply {
            dialog = this@AddExpenseDialog
            costLayout.prefixText = Currency.getInstance(Locale.getDefault()).symbol
        }

        return binding.root
    }

    fun addClicked() {
        val name = binding.name.text.toString()
        val cost = binding.cost.text.toString()
        val costAsFloat = cost.toFloatOrNull()
        Logger.d("Trying to add expense [name=$name, cost=$cost, costAsFloat=$costAsFloat]")
        val nameBlank = name.isBlank()
        val costBlank = cost.isBlank()

        if (nameBlank) {
            binding.nameLayout.showError(appContext, R.string.error_empty_value)
        }

        when {
            costBlank -> binding.costLayout.showError(appContext, R.string.error_empty_value)
            costAsFloat == null -> binding.costLayout.showError(appContext, R.string.error_number_illegal)
        }

        if (!nameBlank && !costBlank && costAsFloat != null) {
            addExpense(name, costAsFloat)
        }
    }

    private fun addExpense(name: String, cost: Float) {
        val expense = Expense(
            timeStamp = System.currentTimeMillis(),
            name = name,
            amount = cost,
            isMonthly = args.isMonthly,
            month = args.month,
            year = args.year
        )
        Logger.v("Inserting expense to database: $expense")
        thread(name = "AddExpenseThread") {
            expenseDao.insert(expense)
        }
        findNavController().popBackStack()
    }

    fun cancelClicked() {
        Logger.i("Canceling add expense")
        findNavController().popBackStack()
    }
}