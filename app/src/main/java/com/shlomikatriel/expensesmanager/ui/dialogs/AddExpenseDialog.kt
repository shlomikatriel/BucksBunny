package com.shlomikatriel.expensesmanager.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
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

    @Inject
    lateinit var currency: Currency

    lateinit var binding: AddExpenseDialogBinding

    private val args: AddExpenseDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)
    }

    override fun layout() = R.layout.add_expense_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<AddExpenseDialogBinding>(view)!!.apply {
            dialog = this@AddExpenseDialog
            inputsLayout.costLayout.prefixText = currency.symbol
        }
    }

    fun addClicked() {
        val monthly =
            binding.inputsLayout.oneTimeMonthlyButtons.checkedButtonId == R.id.monthly_expense
        val name = binding.inputsLayout.name.text.toString()
        val cost = binding.inputsLayout.cost.text.toString()
        val costAsFloat = cost.toFloatOrNull()
        Logger.d("Trying to add expense [name=$name, cost=$cost, costAsFloat=$costAsFloat, monthly=$monthly]")
        val nameBlank = name.isBlank()
        val costBlank = cost.isBlank()

        if (nameBlank) {
            binding.inputsLayout.nameLayout.showError(appContext, R.string.error_empty_value)
        }

        when {
            costBlank -> binding.inputsLayout.costLayout.showError(
                appContext,
                R.string.error_empty_value
            )
            costAsFloat == null -> binding.inputsLayout.costLayout.showError(
                appContext,
                R.string.error_number_illegal
            )
        }

        if (!nameBlank && !costBlank && costAsFloat != null) {
            addExpense(name, costAsFloat, monthly)
        }
    }

    private fun addExpense(name: String, cost: Float, monthly: Boolean) {
        val expense = Expense(
            timeStamp = System.currentTimeMillis(),
            name = name,
            amount = cost,
            isMonthly = monthly,
            month = args.month,
            year = args.year
        )
        Logger.d("Inserting expense to database: $expense")
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