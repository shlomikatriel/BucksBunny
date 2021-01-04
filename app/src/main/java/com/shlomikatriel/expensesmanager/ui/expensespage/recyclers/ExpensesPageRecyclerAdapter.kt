package com.shlomikatriel.expensesmanager.ui.expensespage.recyclers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.database.Expense
import com.shlomikatriel.expensesmanager.databinding.ExpenseRecyclerItemBinding
import com.shlomikatriel.expensesmanager.navigation.navigate
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections.Companion.openDeleteExpenseDialog
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections.Companion.openUpdateExpenseDialog
import java.text.NumberFormat
import java.util.*

class ExpensesPageRecyclerAdapter(
    private val context: Context,
    private val fragment: Fragment,
    private val currencyFormat: NumberFormat,
    private val month: Int
) : RecyclerView.Adapter<ExpensesPageRecyclerAdapter.ExpenseRecyclerViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val inflater = LayoutInflater.from(context)
    private var data = arrayListOf<Expense>()

    override fun getItemId(position: Int): Long {
        val expense = data[position]
        return Objects.hash(expense.databaseId, expense.javaClass.name).toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseRecyclerViewHolder {
        val binding = DataBindingUtil.inflate<ExpenseRecyclerItemBinding>(
            inflater,
            R.layout.expense_recycler_item,
            parent,
            false
        )
        return ExpenseRecyclerViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ExpenseRecyclerViewHolder, position: Int) {
        val data = data[position]
        holder.binding.apply {
            name.text = data.name
            cost.text = when (data) {
                is Expense.OneTime -> currencyFormat.format(data.cost)
                is Expense.Monthly -> context.getString(
                    R.string.expenses_page_recycler_item_monthly,
                    currencyFormat.format(data.cost)
                )
                is Expense.Payments -> {
                    context.getString(
                        R.string.expenses_page_recycler_item_payments,
                        currencyFormat.format(data.cost / data.payments),
                        month - data.month + 1,
                        data.payments
                    )
                }
            }
            val popupMenu = createPopupMenu(holder.binding.menu, data)
            holder.binding.menu.setOnClickListener {
                Logger.d("Showing expense #${data.databaseId} context menu")
                popupMenu.show()
            }
        }
    }

    private fun createPopupMenu(menuIcon: View, item: Expense) =
        PopupMenu(context, menuIcon).apply {
            menuInflater.inflate(R.menu.expense_context_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.update -> {
                        Logger.i("Showing item #${item.databaseId} update dialog")
                        fragment.navigate(
                            openUpdateExpenseDialog(item.databaseId!!, item.getExpenseType(), month)
                        )
                    }
                    R.id.delete -> {
                        Logger.i("Showing item #${item.databaseId} delete dialog")
                        fragment.navigate(
                            openDeleteExpenseDialog(item.databaseId!!, item.getExpenseType())
                        )
                    }
                }
                true
            }
        }

    fun updateData(data: ArrayList<Expense>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class ExpenseRecyclerViewHolder(val binding: ExpenseRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}