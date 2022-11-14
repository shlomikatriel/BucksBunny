package com.shlomikatriel.expensesmanager.compose.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews

@ComponentPreviews
@ExperimentalPagerApi
@Composable
private fun AppPagerIndicatorPreview() = AppTheme {
    AppPagerIndicator(PagerState(6), Modifier.padding(all = 8.dp))
}

@ExperimentalPagerApi
@Composable
fun AppPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = modifier,
        activeColor = MaterialTheme.colors.primary,
        inactiveColor = MaterialTheme.colors.primaryVariant
    )
}

@Composable
fun AppImage(
    @DrawableRes image: Int,
    modifier: Modifier = Modifier,
    @StringRes contentDescription: Int? = null,
    color: Color = MaterialTheme.colors.onBackground,
) {
    Image(
        modifier = modifier,
        painter = painterResource(image),
        contentDescription = contentDescription?.let { stringResource(it) },
        colorFilter = ColorFilter.tint(color)
    )
}

@ComponentPreviews
@Composable
private fun ChipPreview() = AppTheme {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Chip(
            R.string.common_google_play_services_enable_title,
            true,
            {}
        )
        Chip(
            R.string.common_google_play_services_enable_title,
            false,
            {}
        )
    }
}

@ComponentPreviews
@Composable
private fun SingleChipPreview() = AppTheme {
    Chip(
        R.string.common_google_play_services_enable_title,
        false,
        {},
        Modifier.padding(4.dp)
    )
}

@Composable
fun Chip(
    @StringRes title: Int,
    checked: Boolean,
    onCheckedChanged: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = {
                    onCheckedChanged(it)
                }
            ),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        color = if (checked) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.surface
        }
    ) {
        AppText(
            title,
            modifier = Modifier.padding(all = 8.dp),
            color = if (checked) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body2
        )
    }
}