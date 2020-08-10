package com.shlomikatriel.expensesmanager.ui.expensespage.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.shlomikatriel.expensesmanager.ExpensesManagerApp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.databinding.ExpensesPageFragmentBinding
import com.shlomikatriel.expensesmanager.extensions.safeNavigate
import com.shlomikatriel.expensesmanager.logs.Logger
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragmentDirections
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.ExpensesPageEvent
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.ExpensesPageViewModel
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.ExpensesPageViewModelFactory
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.ExpensesPageViewState
import com.shlomikatriel.expensesmanager.ui.expensespage.recyclers.ExpensesPageRecyclerAdapter
import com.shlomikatriel.expensesmanager.utils.AnimationFactory
import java.text.DecimalFormat
import javax.inject.Inject

class ExpensesPageFragment : Fragment() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var animationFactory: AnimationFactory

    private val args: ExpensesPageFragmentArgs by navArgs()

    private lateinit var binding: ExpensesPageFragmentBinding

    private lateinit var model: ExpensesPageViewModel

    private lateinit var expensesRecyclerAdapter: ExpensesPageRecyclerAdapter

    private lateinit var monthlyExpensesRecyclerAdapter: ExpensesPageRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireContext().applicationContext as ExpensesManagerApp).appComponent.inject(this)

        binding = DataBindingUtil.inflate<ExpensesPageFragmentBinding>(
            inflater,
            R.layout.expenses_page_fragment,
            container,
            false
        ).apply {
            fragment = this@ExpensesPageFragment
        }

        initializeViewModel()

        configureRecyclers()

        initializeViewEvents()

        return binding.root
    }

    private fun initializeViewModel() {
        model = ViewModelProvider(
            this,
            ExpensesPageViewModelFactory(appContext, args.month, args.year)
        ).get(args.pagePosition.toString(), ExpensesPageViewModel::class.java)
            .apply {
                postEvent(ExpensesPageEvent.InitializeEvent)
                getViewState().observe(viewLifecycleOwner, Observer { render(it) })
            }
    }

    private fun configureRecyclers() {
        binding.expensesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            expensesRecyclerAdapter =
                ExpensesPageRecyclerAdapter(requireContext(), model, this@ExpensesPageFragment)
            adapter = expensesRecyclerAdapter
        }
        binding.monthlyExpensesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            monthlyExpensesRecyclerAdapter =
                ExpensesPageRecyclerAdapter(requireContext(), model, this@ExpensesPageFragment)
            adapter = monthlyExpensesRecyclerAdapter
        }
    }

    private fun render(viewState: ExpensesPageViewState) {
        viewState.expenses?.let { expensesRecyclerAdapter.updateData(it) }
        viewState.monthlyExpenses?.let { monthlyExpensesRecyclerAdapter.updateData(it) }
        viewState.balance?.let {
            binding.balance.text = DecimalFormat.getCurrencyInstance().format(viewState.balance)
            @ColorRes val color = if (viewState.balance >= 0) R.color.green else R.color.red
            binding.balance.setTextColor(ContextCompat.getColor(appContext, color))
        }
    }

    fun addExpenseClicked(view: View, isMonthly: Boolean) {
        Logger.i("Add expense button clicked [isMonthly=$isMonthly]")
        view.startAnimation(animationFactory.createPopAnimation())
        findNavController().safeNavigate(
            ExpensesMainFragmentDirections.openAddExpenseDialog(
                isMonthly,
                args.month,
                args.year
            )
        )
    }

    private fun initializeViewEvents() = binding.balance.setOnLongClickListener {
        Logger.i("Balance long clicked [arguments=$args]")
        findNavController().safeNavigate(ExpensesMainFragmentDirections.openChooseIncomeDialog())
        true
    }
}