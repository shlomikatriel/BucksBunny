package com.shlomikatriel.expensesmanager.expenses.composables

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shlomikatriel.expensesmanager.R
import com.shlomikatriel.expensesmanager.compose.AppTheme
import com.shlomikatriel.expensesmanager.compose.composables.AppText
import com.shlomikatriel.expensesmanager.compose.tooling.ComponentPreviews
import java.lang.Float.max
import java.text.DecimalFormat
import kotlin.math.abs

@Composable
fun ExpensesGraph(income: Float, expenses: Float, modifier: Modifier = Modifier) {
    val animationSpec = tween<Float>(700, easing = LinearOutSlowInEasing)
    val animatedIncome by animateFloatAsState(income, animationSpec)
    val animatedExpenses by animateFloatAsState(expenses, animationSpec)
    ExpensesGraphSnapshot(animatedIncome, animatedExpenses, modifier)
}

@Composable
private fun ExpensesGraphSnapshot(income: Float, expenses: Float, modifier: Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.Bottom
        ) {
            val maxValue = max(income, expenses)
            val balance = income - expenses
            val currencyFormat = DecimalFormat.getCurrencyInstance()

            // Income
            Bar(currencyFormat.format(income), MaterialTheme.colors.secondary, income / maxValue)
            Bar(currencyFormat.format(expenses), MaterialTheme.colors.onBackground, expenses / maxValue)
            val balanceColor = colorResource(if (balance >= 0) R.color.green else R.color.red)
            Bar(currencyFormat.format(balance), balanceColor, abs(balance) / maxValue)
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            AppText(R.string.expenses_main_graph_income, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            AppText(R.string.expenses_main_graph_expenses, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            AppText(R.string.expenses_main_graph_balance, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun RowScope.Bar(numberText: String, color: Color, barHeightFraction: Float) {
    Column(
        modifier = Modifier
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(modifier = Modifier.fractured(this@Column, 1f - barHeightFraction))
        AppText(numberText, color = color)
        Box(
            modifier = Modifier
                .fractured(this@Column, barHeightFraction)
                .width(36.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(color)
        )
    }
}

private fun Modifier.fractured(columnScope: ColumnScope, weight: Float): Modifier = columnScope.run {
    if (weight > 0f) {
        weight(weight)
    } else {
        height(0.dp)
    }
}

@ComponentPreviews
@Composable
private fun ExpensesGraphPreview() = AppTheme {
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var incomeState by remember { mutableStateOf(false) }
        var expensesState by remember { mutableStateOf(false) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { incomeState = !incomeState }) {
                AppText(R.string.expenses_main_graph_income)
            }
            Button(onClick = { expensesState = !expensesState }) {
                AppText(R.string.expenses_main_graph_expenses)
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
        ExpensesGraph(
            income = if (incomeState) 1000f else 2000f,
            expenses = if (expensesState) 721.2f else 1643.42f,
            modifier = Modifier.height(300.dp).fillMaxWidth()
        )
    }
}