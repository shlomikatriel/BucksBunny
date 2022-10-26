package com.shlomikatriel.expensesmanager.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppPagerIndicator
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.hideToolbar
import com.shlomikatriel.expensesmanager.logs.logDebug
import com.shlomikatriel.expensesmanager.logs.logInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

@ExperimentalPagerApi
@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        hideToolbar()

        return ComposeView(requireContext()).apply {
            setContent {
                OnboardingScreen()
            }
        }
    }

    @Preview
    @Composable
    fun OnboardingScreen() = AppTheme {
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
            AppPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )

            // Creates a coroutine scope, which is bound to the composable lifecycle.
            val scope = rememberCoroutineScope()

            OutlinedButton(
                modifier = Modifier.animateContentSize(),
                onClick = {
                    logInfo("User clicked Next [currentPage=${pagerState.currentPage}]")
                    if (pagerState.isLast()) {
                        findNavController().popBackStack()
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
                AppText(textRes)
            }
        }
    }

    private fun PagerState.isLast() = currentPage == pageCount - 1
}