package com.shlomikatriel.expensesmanager.expenses.dialogs

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shlomikatriel.expensesmanager.*
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.databinding.AddExpenseDialogBinding
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class AddExpenseDialog : BaseDialog() {

    @ApplicationContext
    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var databaseManager: DatabaseManager

    @Inject
    lateinit var localizationManager: LocalizationManager

    lateinit var binding: AddExpenseDialogBinding

    private val args: AddExpenseDialogArgs by navArgs()

    override fun layout() = R.layout.add_expense_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<AddExpenseDialogBinding>(view)!!.apply {
            inputsLayout.initialize(localizationManager.getCurrencySymbol(), args.type)
            title.setText(getDialogTitle())
            dialog = this@AddExpenseDialog
        }
    }

    @StringRes
    private fun getDialogTitle() = when (args.type) {
        ExpenseType.ONE_TIME -> R.string.add_one_time_expense_dialog_title
        ExpenseType.MONTHLY -> R.string.add_monthly_expense_dialog_title
        ExpenseType.PAYMENTS -> R.string.add_payments_expense_dialog_title
    }

    fun addClicked() {
        val name = binding.inputsLayout.name.text.toString()
        val costAsString = binding.inputsLayout.cost.text.toString()
        val cost = costAsString.toFloatOrNull()
        val paymentsAsString = binding.inputsLayout.payments.text.toString()
        val payments = paymentsAsString.toIntOrNull()
        logDebug("Trying to update expense [name=$name, costAsString=$costAsString, paymentsAsString=$paymentsAsString, type=${args.type}]")

        if (binding.inputsLayout.isInputValid(args.type, appContext)) {
            addExpense(name, cost!!, payments, args.type)
        }
    }

    private fun addExpense(name: String, cost: Float, payments: Int?, type: ExpenseType) {
        thread(name = "AddExpenseThread") {
            val expense = when (type) {
                ExpenseType.ONE_TIME -> Expense.OneTime(
                    databaseId = null,
                    timeStamp = System.currentTimeMillis(),
                    name = name,
                    cost = cost,
                    month = args.month
                )
                ExpenseType.MONTHLY -> Expense.Monthly(
                    databaseId = null,
                    timeStamp = System.currentTimeMillis(),
                    name = name,
                    cost = cost
                )
                ExpenseType.PAYMENTS -> Expense.Payments(
                    databaseId = null,
                    timeStamp = System.currentTimeMillis(),
                    name = name,
                    cost = cost,
                    month = args.month,
                    payments = payments!!
                )
            }
            databaseManager.insert(expense)
        }
        findNavController().popBackStack()
    }

    fun cancelClicked() {
        logInfo("Canceling add expense")
        findNavController().popBackStack()
    }
}