package com.shlomikatriel.expensesmanager.ui.expensespage.recyclers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.ExpenseRecyclerItemBinding
import com.shlomikatriel.expensesmanager.extensions.safeNavigate
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.ExpenseRecyclerItem
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.ExpensesPageViewModel
import java.text.DecimalFormat

class ExpensesPageRecyclerAdapter(
    context: Context,
    val model: ExpensesPageViewModel,
    val fragment: Fragment
) : RecyclerView.Adapter<ExpensesPageRecyclerAdapter.ExpenseRecyclerViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val inflater = LayoutInflater.from(context)
    private var data = arrayListOf<ExpenseRecyclerItem>()

    override fun getItemId(position: Int): Long {
        return data[position].id
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
            amount.text = DecimalFormat.getCurrencyInstance().format(data.amount)
            holder.binding.root.setOnLongClickListener {
                Logger.d("handling expense #${data.id} deletion")
                fragment.findNavController()
                    .safeNavigate(ExpensesMainFragmentDirections.openDeleteExpenseDialog(data.id))
                true
            }
        }
    }

    fun updateData(data: ArrayList<ExpenseRecyclerItem>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class ExpenseRecyclerViewHolder(val binding: ExpenseRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}