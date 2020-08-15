package com.shlomikatriel.expensesmanager.dagger.components

import com.shlomikatriel.expensesmanager.dagger.modules.AppModule
import com.shlomikatriel.expensesmanager.dagger.modules.ContextModule
import com.shlomikatriel.expensesmanager.dagger.modules.DatabaseModule
import com.shlomikatriel.expensesmanager.ui.MainActivity
import com.shlomikatriel.expensesmanager.ui.dialogs.AddExpenseDialog
import com.shlomikatriel.expensesmanager.ui.dialogs.ChooseIncomeDialog
import com.shlomikatriel.expensesmanager.ui.dialogs.DeleteExpenseDialog
import com.shlomikatriel.expensesmanager.ui.dialogs.EditExpenseDialog
import com.shlomikatriel.expensesmanager.ui.expenses.fragments.ExpensesMainFragment
import com.shlomikatriel.expensesmanager.ui.expensespage.fragments.ExpensesPageFragment
import com.shlomikatriel.expensesmanager.ui.expensespage.mvi.ExpensesPageViewModel
import com.shlomikatriel.expensesmanager.ui.settings.fragments.SettingsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, DatabaseModule::class, AppModule::class])
interface AppComponent {

    fun inject(expensesMainFragment: ExpensesMainFragment)

    fun inject(expensesFragment: ExpensesPageViewModel)

    fun inject(expensesPageFragment: ExpensesPageFragment)

    fun inject(addExpenseDialog: AddExpenseDialog)

    fun inject(deleteExpenseDialog: DeleteExpenseDialog)

    fun inject(chooseIncomeDialog: ChooseIncomeDialog)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(mainActivity: MainActivity)

    fun inject(editExpenseDialog: EditExpenseDialog)
}