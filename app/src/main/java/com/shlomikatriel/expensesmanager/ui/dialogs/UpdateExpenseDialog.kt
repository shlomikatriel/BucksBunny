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
import com.shlomikatriel.expensesmanager.databinding.UpdateExpenseDialogBinding
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.ui.initialize
import com.shlomikatriel.expensesmanager.ui.isInputValid
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

class UpdateExpenseDialog : BaseDialog() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var databaseManager: DatabaseManager

    @Inject
    lateinit var currency: Currency

    lateinit var binding: UpdateExpenseDialogBinding

    private val args: UpdateExpenseDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)
    }

    override fun layout() = R.layout.update_expense_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<UpdateExpenseDialogBinding>(view)!!.apply {
            inputsLayout.initialize(currency.symbol)
            dialog = this@UpdateExpenseDialog
        }
        populateFields()
    }

    private fun populateFields() = thread(name = "PopulateUpdateExpenseDialogFields") {
        Logger.d("Fetching expense to populate fields [$args]")
        val expense = databaseManager.getExpense(args.id, args.type)
        activity?.runOnUiThread {
            Logger.d("Populating fields: $expense")
            binding.inputsLayout.apply {
                when (expense) {
                    is Expense.OneTime -> {
                        typeButtons.check(R.id.one_time_expense)
                        cost.setText(expense.cost.toString())
                        name.setText(expense.name)
                    }
                    is Expense.Monthly -> {
                        typeButtons.check(R.id.monthly_expense)
                        cost.setText(expense.cost.toString())
                        name.setText(expense.name)
                    }
                    is Expense.Payments -> {
                        typeButtons.check(R.id.payments_expense)
                        cost.setText(expense.cost.toString())
                        name.setText(expense.name)
                        paymentsLayout.visibility = View.VISIBLE
                        payments.setText(expense.payments.toString())
                    }
                }
                oneTimeExpense.isEnabled = false
                monthlyExpense.isEnabled = false
                paymentsExpense.isEnabled = false
            }
        }
    }

    fun updateClicked() {
        val name = binding.inputsLayout.name.text.toString()
        val costAsString = binding.inputsLayout.cost.text.toString()
        val cost = costAsString.toFloatOrNull()
        val paymentsString = binding.inputsLayout.payments.text.toString()
        val payments = paymentsString.toIntOrNull()
        Logger.d("Trying to update expense [name=$name, costAsString=$costAsString, paymentsString=$paymentsString, type=${args.type}]")

        if (binding.inputsLayout.isInputValid(appContext)) {
            updateExpense(name, cost!!, payments)
        }
    }

    private fun updateExpense(name: String, cost: Float, payments: Int?) {
        thread(name = "UpdateExpenseThread") {
            when (val oldExpense = databaseManager.getExpense(args.id, args.type)) {
                is Expense.OneTime -> oldExpense.copy(
                    name = name,
                    cost = cost
                )
                is Expense.Monthly -> oldExpense.copy(
                    name = name,
                    cost = cost
                )
                is Expense.Payments -> oldExpense.copy(
                    name = name,
                    cost = cost,
                    payments = payments!!
                )
            }.let { newExpense ->
                databaseManager.update(newExpense)
            }
        }
        findNavController().popBackStack()
    }

    fun cancelClicked() {
        Logger.i("Canceling update expense")
        findNavController().popBackStack()
    }
}