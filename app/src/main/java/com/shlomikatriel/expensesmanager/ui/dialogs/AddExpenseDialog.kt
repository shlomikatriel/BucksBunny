package com.shlomikatriel.expensesmanager.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.databinding.AddExpenseDialogBinding
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.ui.getSelectedExpenseType
import com.shlomikatriel.expensesmanager.ui.initialize
import com.shlomikatriel.expensesmanager.ui.isInputValid
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

class AddExpenseDialog : BaseDialog() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var databaseManager: DatabaseManager

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
            inputsLayout.initialize(currency.symbol)
            dialog = this@AddExpenseDialog
        }
    }

    fun addClicked() {
        val type = binding.inputsLayout.getSelectedExpenseType()
        val name = binding.inputsLayout.name.text.toString()
        val costAsString = binding.inputsLayout.cost.text.toString()
        val cost = costAsString.toFloatOrNull()
        val paymentsAsString = binding.inputsLayout.payments.text.toString()
        val payments = paymentsAsString.toIntOrNull()
        Logger.d("Trying to update expense [name=$name, costAsString=$costAsString, paymentsAsString=$paymentsAsString, type=$type]")

        if (binding.inputsLayout.isInputValid(appContext)) {
            addExpense(name, cost!!, payments, type)
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
        Logger.i("Canceling add expense")
        findNavController().popBackStack()
    }
}