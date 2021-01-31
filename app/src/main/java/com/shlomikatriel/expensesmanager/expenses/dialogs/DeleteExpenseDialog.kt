package com.shlomikatriel.expensesmanager.expenses.dialogs

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shlomikatriel.expensesmanager.BaseDialog
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.DatabaseManager
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.database.model.ExpenseType
import com.shlomikatriel.expensesmanager.databinding.DeleteExpenseDialogBinding
import com.shlomikatriel.expensesmanager.logs.logInfo
import javax.inject.Inject
import kotlin.concurrent.thread

class DeleteExpenseDialog : BaseDialog() {

    @Inject
    lateinit var databaseManager: DatabaseManager

    lateinit var binding: DeleteExpenseDialogBinding

    private val args: DeleteExpenseDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)
    }

    override fun layout() = R.layout.delete_expense_dialog

    override fun bind(view: View) {
        binding = DataBindingUtil.bind<DeleteExpenseDialogBinding>(view)!!.apply {
            dialog = this@DeleteExpenseDialog
        }
    }

    fun cancelClicked() {
        logInfo("Canceling delete expense")
        findNavController().popBackStack()
    }

    fun deleteClicked() {
        thread(name = "DeleteExpenseThread") {
            val expense = when (args.type) {
                ExpenseType.ONE_TIME -> Expense.OneTime(args.id, 0L, "", 0f, 0)
                ExpenseType.MONTHLY -> Expense.Monthly(args.id, 0L, "", 0f)
                ExpenseType.PAYMENTS -> Expense.Payments(args.id, 0L, "", 0f, 0, 0)
            }
            databaseManager.delete(expense)
        }
        findNavController().popBackStack()
    }
}