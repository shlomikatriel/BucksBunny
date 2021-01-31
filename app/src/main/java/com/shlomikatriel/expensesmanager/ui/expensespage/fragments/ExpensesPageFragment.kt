package com.shlomikatriel.expensesmanager.ui.expensespage.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.ExpensesPageFragmentBinding
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.navigation.navigate
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections.Companion.openAddExpenseDialog
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.*
import com.shlomikatriel.expensesmanager.ui.expensespage.recyclers.ExpensesPageRecyclerAdapter
import java.text.NumberFormat
import javax.inject.Inject
import javax.inject.Named

class ExpensesPageFragment : Fragment() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var currencyFormat: NumberFormat

    @Inject
    @Named("integer")
    lateinit var currencyIntegerFormat: NumberFormat

    private val args: ExpensesPageFragmentArgs by navArgs()

    private lateinit var binding: ExpensesPageFragmentBinding

    private val model: ExpensesPageViewModel by viewModels()

    private lateinit var expensesRecyclerAdapter: ExpensesPageRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate<ExpensesPageFragmentBinding>(
            inflater,
            R.layout.expenses_page_fragment,
            container,
            false
        ).apply {
            fragment = this@ExpensesPageFragment
            currencyFormat = currencyIntegerFormat
        }

        model.apply {
            postEvent(ExpensesPageEvent.Initialize(args.month))
            getViewState().observe(viewLifecycleOwner, { render(it) })
        }

        configureRecycler()

        return binding.root
    }

    private fun configureRecycler() = binding.expensesRecycler.apply {
        layoutManager = LinearLayoutManager(requireContext())
        expensesRecyclerAdapter = ExpensesPageRecyclerAdapter(
            requireContext(),
            this@ExpensesPageFragment,
            currencyFormat,
            args.month
        )
        adapter = expensesRecyclerAdapter
    }

    fun onCheckedChanged() {
        val selectedChips = getSelectedChips()
        logInfo("Selected chips changed [selectedChips=$selectedChips]")
        model.postEvent(ExpensesPageEvent.SelectedChipsChange(selectedChips))
    }

    private fun render(viewState: ExpensesPageViewState) {
        expensesRecyclerAdapter.updateData(viewState.expenses)
        binding.total = currencyFormat.format(viewState.total)
    }

    private fun getSelectedChips() = binding.chipGroup.checkedChipIds
        .mapNotNull { binding.chipGroup.findViewById<View>(it).tag as Chip? }
        .toSet()


    fun addExpenseClicked() {
        logInfo("Add expense button clicked")
        navigate(openAddExpenseDialog(args.month))
    }
}