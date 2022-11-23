package com.shlomikatriel.expensesmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.analytics.FirebaseAnalytics
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.expenses.ExpensesScreen
import com.shlomikatriel.expensesmanager.firebase.logEvent
import com.shlomikatriel.expensesmanager.logs.*
import com.shlomikatriel.expensesmanager.navigation.*
import com.shlomikatriel.expensesmanager.onboarding.OnboardingScreen
import com.shlomikatriel.expensesmanager.play.AppReviewManager
import com.shlomikatriel.expensesmanager.play.UpdateManager
import com.shlomikatriel.expensesmanager.preferences.PreferencesScreen
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.IntKey
import com.shlomikatriel.expensesmanager.sharedpreferences.getBoolean
import com.shlomikatriel.expensesmanager.sharedpreferences.getInt
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appReviewManager: AppReviewManager,
    private val updateManager: UpdateManager
) : ViewModel() {

    fun onInAppReviewCheckup(context: Context) {
        if (BuildConfig.DEBUG) return
        if (context !is Activity) {
            logWarning(Tag.IN_APP_REVIEW, "Context is not activity, canceling in-app review checkup")
            return
        }
        viewModelScope.launch(context = Dispatchers.IO) {
            appReviewManager.showAppReviewDialogIfNeeded(context)
        }
    }

    fun onInAppUpdateCheckup(context: Context, snackbarHostState: SnackbarHostState) {
        if (BuildConfig.DEBUG) return
        if (context !is Activity) {
            logWarning(Tag.IN_APP_UPDATE, "Context is not activity, canceling in-app update checkup")
            return
        }
        viewModelScope.launch {
            updateManager.checkIfUpdateAvailable(context, snackbarHostState)
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(shouldShowOnboarding: Boolean, onDestinationChangedListener: NavController.OnDestinationChangedListener) {
    val startDestination = if (shouldShowOnboarding) Destination.ONBOARDING else Destination.EXPENSES
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val model: MainViewModel = hiltViewModel()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener(onDestinationChangedListener)
        model.onInAppReviewCheckup(context)
        model.onInAppUpdateCheckup(context, snackbarHostState)
    }
    val topBarDetailsState = remember { mutableStateOf(startDestination.topBarDetails) }

    Scaffold(
        topBar = {
            topBarDetailsState.value?.let {
                TopAppBar(it, navController)
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
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
            logDebug(Tag.APPLICATION, "Locking orientation")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun configureDarkMode() {
        val mode = sharedPreferences.getInt(IntKey.DARK_MODE)
        logInfo(Tag.APPLICATION, "Dark mode: $mode")
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        logInfo(Tag.NAVIGATION, "Destination changed: ${destination.route}")
        firebaseAnalytics.logEvent("${destination.route}_opened")
    }
}