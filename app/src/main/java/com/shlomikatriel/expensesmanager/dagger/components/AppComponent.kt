package com.shlomikatriel.expensesmanager.dagger.components

import com.shlomikatriel.expensesmanager.BucksBunnyApp
import com.shlomikatriel.expensesmanager.MainActivity
import com.shlomikatriel.expensesmanager.dagger.modules.AppModule
import com.shlomikatriel.expensesmanager.dagger.modules.ContextModule
import com.shlomikatriel.expensesmanager.dagger.modules.DatabaseModule
import com.shlomikatriel.expensesmanager.expenses.components.ExpensesGraph
import com.shlomikatriel.expensesmanager.expenses.dialogs.AddExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.dialogs.DeleteExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.dialogs.UpdateExpenseDialog
import com.shlomikatriel.expensesmanager.expenses.fragments.ExpensesMainFragment
import com.shlomikatriel.expensesmanager.expenses.fragments.ExpensesPageFragment
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesMainViewModel
import com.shlomikatriel.expensesmanager.expenses.mvi.ExpensesPageViewModel
import com.shlomikatriel.expensesmanager.onboarding.OnboardingAnonymousDataFragment
import com.shlomikatriel.expensesmanager.onboarding.OnboardingFragment
import com.shlomikatriel.expensesmanager.onboarding.OnboardingIncomeFragment
import com.shlomikatriel.expensesmanager.settings.dialogs.ChooseIncomeDialog
import com.shlomikatriel.expensesmanager.settings.fragments.SettingsFragment
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

    fun inject(updateExpenseDialog: UpdateExpenseDialog)

    fun inject(bucksBunnyApp: BucksBunnyApp)

    fun inject(onboardingFragment: OnboardingFragment)

    fun inject(onboardingIncomeFragment: OnboardingIncomeFragment)

    fun inject(onboardingAnonymousDataFragment: OnboardingAnonymousDataFragment)

    fun inject(expensesMainViewModel: ExpensesMainViewModel)

    fun inject(expensesGraph: ExpensesGraph)
}