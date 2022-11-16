package com.shlomikatriel.expensesmanager

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.analytics.FirebaseAnalytics
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.expenses.ExpensesScreen
import com.shlomikatriel.expensesmanager.firebase.logEvent
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.navigation.*
import com.shlomikatriel.expensesmanager.onboarding.OnboardingScreen
import com.shlomikatriel.expensesmanager.preferences.PreferencesScreen
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.IntKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.getInt
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(shouldShowOnboarding: Boolean, onDestinationChangedListener: NavController.OnDestinationChangedListener) {
    val startDestination = if (shouldShowOnboarding) Destination.ONBOARDING else Destination.EXPENSES
    val navController = rememberNavController().apply {
        removeOnDestinationChangedListener(onDestinationChangedListener)
        addOnDestinationChangedListener(onDestinationChangedListener)
    }
    val topBarDetailsState = remember { mutableStateOf(startDestination.topBarDetails) }

    Scaffold(
        topBar = {
            topBarDetailsState.value?.let {
                TopAppBar(it, navController)
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavHost(navController, startDestination) {
                composable(topBarDetailsState, Destination.ONBOARDING) {
                    OnboardingScreen {
                        navController.navigate(Destination.EXPENSES) {
                            popupTo(Destination.ONBOARDING, true)
                        }
                    }
                }
                composable(topBarDetailsState, Destination.EXPENSES) {
                    ExpensesScreen()
                }
                composable(topBarDetailsState, Destination.PREFERENCES) {
                    PreferencesScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(topBarDetails: TopBarDetails, navController: NavController) {
    TopAppBar(
        title = { Text(text = stringResource(topBarDetails.title)) },
        navigationIcon = {
            if (topBarDetails.hasBackNavigation) {
                IconButton(onClick = { navController.popBackStack() }) {
                    val icon = if (LocalLayoutDirection.current == LayoutDirection.Ltr) Icons.Filled.ArrowBack else Icons.Filled.ArrowForward
                    Icon(icon, tint = LocalContentColor.current, contentDescription = stringResource(R.string.back))
                }
            }
        },
        actions = {
            topBarDetails.actions?.forEach { action ->
                IconButton(onClick = { navController.navigate(action.destination) }) {
                    Icon(action.icon, tint = LocalContentColor.current, contentDescription = stringResource(action.contentDescription))
                }
            }
        }
    )
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        toggleOrientationLock()
        configureDarkMode()

        val shouldShowOnboarding = sharedPreferences.getBoolean(BooleanKey.SHOULD_SHOW_ONBOARDING)
        setContent {
            AppTheme {
                MainScreen(shouldShowOnboarding, this)
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun toggleOrientationLock() {
        if (!resources.getBoolean(R.bool.is_tablet)) {
            logDebug("Locking orientation")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun configureDarkMode() {
        val mode = sharedPreferences.getInt(IntKey.DARK_MODE)
        logInfo("Dark mode: $mode")
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        logInfo("Destination changed: ${destination.route}")
        firebaseAnalytics.logEvent("${destination.route}_opened")
    }
}