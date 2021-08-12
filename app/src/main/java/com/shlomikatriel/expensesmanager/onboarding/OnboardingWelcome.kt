package com.shlomikatriel.expensesmanager.onboarding

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppText
import com.shlomikatriel.expensesmanager.compose.AppTheme

@Preview(
    name = "Normal",
    showBackground = true,
    locale = "en"
)
@Preview(
    name = "Custom",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    locale = "iw"
)
@Composable
fun OnboardingWelcomeScreenPreview() = AppTheme {
    OnboardingWelcomeScreen()
}

@Composable
fun OnboardingWelcomeScreen() = Column(
    modifier = Modifier
        .padding(all = dimensionResource(R.dimen.fragment_padding))
        .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(Dp(20f)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.launcher_foreground),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(R.color.launcher_background)),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dp(400f))
                .clip(AbsoluteCutCornerShape(50))
                .alpha(0.4f)
                .scale(1.3f)
        )
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppText(
                text = stringResource(R.string.onboarding_welcome),
                style = MaterialTheme.typography.h5
            )
            AppText(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.h4,
                bold = true,
                colored = true
            )
            AnimatedSlogan()
        }
    }
    AppText(
        text = stringResource(R.string.onboarding_welcome_instructions),
        style = MaterialTheme.typography.body2
    )
}

@Composable
private fun AnimatedSlogan() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse,

            )
    )
    AppText(
        text = stringResource(R.string.onboarding_expenses_slogan),
        modifier = Modifier
            .wrapContentSize()
            .padding(top = Dp(16f))
            .scale(scale),
        style = MaterialTheme.typography.h5,
        colored = true
    )
}