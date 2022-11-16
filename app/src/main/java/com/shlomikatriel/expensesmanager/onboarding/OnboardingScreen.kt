package com.shlomikatriel.expensesmanager.onboarding

import android.content.SharedPreferences
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.accompanist.pager.*
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.tooling.ScreenPreviews
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.sharedpreferences.BooleanKey
import com.shlomikatriel.expensesmanager.sharedpreferences.putBoolean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private enum class OnboardingPage {
    WELCOME {
        @Composable
        override fun Page() = OnboardingWelcomeScreen()
    },
    INCOME {
        @Composable
        override fun Page() = OnboardingIncomeScreen()
    },
    ANONYMOUS_DATA {
        @Composable
        override fun Page() = AnonymousDataScreen()
    };

    @Composable
    abstract fun Page()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
): ViewModel() {
    fun onOnboardingComplete() {
        logInfo("Onboarding complete")
        sharedPreferences.putBoolean(BooleanKey.SHOULD_SHOW_ONBOARDING, false)
    }
}

@ExperimentalPagerApi
@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) = AppTheme {
    val model: OnboardingViewModel = hiltViewModel()
    Column(
        modifier = Modifier
            .padding(all = dimensionResource(id = R.dimen.fragment_padding))
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.fragment_vertical_spacing)
        )
    ) {
        val pageArray = OnboardingPage.values()
        val totalPages = pageArray.size

        val pagerState = rememberPagerState()
        HorizontalPager(totalPages, state = pagerState, modifier = Modifier.weight(1f, true)) { page ->
            logDebug("Onboarding page $page selected")
            pageArray[page].Page()
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )

        // Creates a coroutine scope, which is bound to the composable lifecycle.
        val scope = rememberCoroutineScope()

        OutlinedButton(
            modifier = Modifier.animateContentSize(),
            onClick = {
                logInfo("User clicked Next [currentPage=${pagerState.currentPage}]")
                if (pagerState.isLast()) {
                    model.onOnboardingComplete()
                    onOnboardingComplete()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        ) {
            val textRes = if (pagerState.isLast()) {
                R.string.onboarding_lets_get_started
            } else {
                R.string.onboarding_next
            }
            Text(stringResource(textRes))
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
private fun PagerState.isLast() = currentPage == pageCount - 1

@OptIn(ExperimentalPagerApi::class)
@ScreenPreviews
@Composable
private fun OnboardingPreview() = AppTheme {
    OnboardingScreen { }
}