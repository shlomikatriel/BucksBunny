package com.shlomikatriel.expensesmanager.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.tooling.ScreenPreviews

@ScreenPreviews
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
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(AbsoluteCutCornerShape(50))
                .alpha(0.4f)
                .scale(1.3f),
            painter = painterResource(R.drawable.launcher_foreground),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorResource(R.color.launcher_background))
        )
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_welcome),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            AnimatedSlogan()
        }
    }
    Text(
        text = stringResource(R.string.onboarding_welcome_instructions),
        style = MaterialTheme.typography.bodyMedium
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
    Text(
        text = stringResource(R.string.onboarding_expenses_slogan),
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 16.dp)
            .scale(scale),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary
    )
}