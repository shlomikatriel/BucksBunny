package com.shlomikatriel.expensesmanager.compose

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.shlomikatriel.expensesmanager.R

private val appLightColors = lightColors(
    primary = Color(0xff0288d1),
    primaryVariant = Color(0xff03a9f4),
    secondary = Color(0xff9c27b0),
    secondaryVariant = Color(0xff9c27b0),
    onSecondary = Color.White,
    onBackground = Color.DarkGray,
    onSurface = Color.DarkGray
)

private val appDarkColors = darkColors(
    primary = Color(0xff005b9f),
    primaryVariant = Color(0xff002f6c),
    secondary = Color(0xff6a0080),
    secondaryVariant = Color(0xff6a0080),
    onBackground = Color.LightGray,
    onSurface = Color.LightGray
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    MaterialTheme(
        colors = if (darkTheme) appDarkColors else appLightColors,
        content = content
    )
}

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    bold: Boolean = false,
    isError: Boolean = false,
    colored: Boolean = false,
    textAlign: TextAlign = TextAlign.Center
) = Text(
    text = text,
    style = style,
    color = when {
        isError -> MaterialTheme.colors.error
        colored -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onBackground
    },
    fontWeight = if (bold) FontWeight.Bold else null,
    modifier = modifier,
    textAlign = textAlign
)

@Composable
fun AppInfoText(
    text: String,
    modifier: Modifier = Modifier,
    bold: Boolean = false,
    colored: Boolean = false
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Dp(4f))
) {
    val color = if (colored) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
    Image(
        painter = painterResource(R.drawable.info),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color)
    )
    AppText(
        text = text,
        style = MaterialTheme.typography.body2,
        colored = colored,
        bold = bold,
        textAlign = TextAlign.Start
    )
}

@Composable
fun AppTextField(
    value: String,
    @StringRes label: Int,
    onValueChange: (String) -> Unit,
    valueValidator: (input: String) -> Int?,
    modifier: Modifier = Modifier,
    trailingIcon: String? = null
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    val errorStringRes = valueValidator(value)
    OutlinedTextField(
        value,
        onValueChange,
        label = { AppText(stringResource(label)) },
        colors = TextFieldDefaults.outlinedTextFieldColors(textColor = MaterialTheme.colors.onBackground),
        isError = errorStringRes != null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = { trailingIcon?.let { AppText(it) } },
        singleLine = true
    )
    val errorMessage = errorStringRes?.let { stringResource(it) } ?: ""
    AppText(
        errorMessage,
        modifier = Modifier.padding(top = Dp(8f))
            .animateContentSize()
            .run {
                if (errorStringRes != null) {
                    wrapContentHeight()
                } else {
                    height(Dp(0f))
                }
            },
        isError = true
    )
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