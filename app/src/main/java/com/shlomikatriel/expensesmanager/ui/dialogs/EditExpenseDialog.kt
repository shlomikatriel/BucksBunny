package com.shlomikatriel.expensesmanager.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.ExpenseDao
import com.shlomikatriel.expensesmanager.databinding.EditExpenseDialogBinding
import com.shlomikatriel.expensesmanager.extensions.showError
import com.shlomikatriel.expensesmanager.logs.Logger
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

class EditExpenseDialog : BaseDialog() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var expenseDao: ExpenseDao

    @Inject
    lateinit var currency: Currency

    lateinit var binding: EditExpenseDialogBinding

    private val args: EditExpenseDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)
    }

    override fun layout() = R.layout.edit_expense_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<EditExpenseDialogBinding>(view)!!.apply {
            dialog = this@EditExpenseDialog
            inputsLayout.costLayout.prefixText = currency.symbol
            inputsLayout.oneTimeMonthlyButtons.check(if (args.isMonthly) R.id.monthly_expense else R.id.one_time_expense)
            inputsLayout.cost.setText(args.amount.toString())
            inputsLayout.name.setText(args.name)
        }
    }

    fun editClicked() {
        val monthly =
            binding.inputsLayout.oneTimeMonthlyButtons.checkedButtonId == R.id.monthly_expense
        val name = binding.inputsLayout.name.text.toString()
        val cost = binding.inputsLayout.cost.text.toString()
        val costAsFloat = cost.toFloatOrNull()
        Logger.d("Trying to edit expense [name=$name, cost=$cost, costAsFloat=$costAsFloat, monthly=$monthly]")
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
            editExpense(name, costAsFloat, monthly)
        }
    }

    private fun editExpense(name: String, cost: Float, monthly: Boolean) {
        thread(name = "EditExpenseThread") {
            val expense = expenseDao.getExpenseById(args.id).copy(
                name = name,
                amount = cost,
                isMonthly = monthly
            )
            Logger.v("Updating expense to database: $expense")
            expenseDao.update(expense)
        }
        findNavController().popBackStack()
    }

    fun cancelClicked() {
        Logger.i("Canceling edit expense")
        findNavController().popBackStack()
    }
}