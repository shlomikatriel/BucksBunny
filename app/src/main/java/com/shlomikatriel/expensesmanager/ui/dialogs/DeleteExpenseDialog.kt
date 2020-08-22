package com.shlomikatriel.expensesmanager.ui.dialogs

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.ExpenseDao
import com.shlomikatriel.expensesmanager.databinding.DeleteExpenseDialogBinding
import com.shlomikatriel.expensesmanager.logs.Logger
import javax.inject.Inject
import kotlin.concurrent.thread

class DeleteExpenseDialog : BaseDialog() {

    @Inject
    lateinit var expenseDao: ExpenseDao

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
        Logger.i("Canceling delete expense")
        findNavController().popBackStack()
    }

    fun deleteClicked() {
        Logger.v("Deleting expense from database: ${args.id}")
        thread(name = "DeleteExpenseThread") {
            expenseDao.deleteById(args.id)
        }
        findNavController().popBackStack()
    }
}