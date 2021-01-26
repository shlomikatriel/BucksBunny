package com.shlomikatriel.expensesmanager.expensesgraph

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.withClip
import androidx.core.graphics.withTranslation
import com.shlomikatriel.expensesmanager.R
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ExpensesGraph : View {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
            context,
            attrs,
            defStyleAttr,
            0
    )

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private var incomeLabel: String = "N/A"
    private var expensesLabel: String = "N/A"
    private var balanceLabel: String = "N/A"

    // Income
    private var fromIncome = 0f
    private var toIncome = 0f
    private var income = 0f
    private var fromIncomeBar = 0f // 0f-1f
    private var toIncomeBar = 0f // 0f-1f
    private var incomeBar = 0f // 0f-1f

    // Expenses
    private var fromExpenses = 0f
    private var toExpenses = 0f
    private var expenses = 0f
    private var fromExpensesBar = 0f // 0f-1f
    private var toExpensesBar = 0f // 0f-1f
    private var expensesBar = 0f // 0f-1f

    // Balance
    private var fromBalance = 0f
    private var toBalance = 0f
    private var balance = 0f
    private var fromBalanceBar = 0f // -1f-1f
    private var toBalanceBar = 0f // -1f-1f
    private var balanceBar = 0f // -1f-1f

    // Progress from 0f to 1f
    private val progressAnimator = ValueAnimator().apply {
        duration = 200L
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            val progress = animatedValue as Float
            // Income
            income = calculateCurrentValue(fromIncome, toIncome, progress)
            incomeBar = calculateCurrentValue(fromIncomeBar, toIncomeBar, progress)

            // Expenses
            expenses = calculateCurrentValue(fromExpenses, toExpenses, progress)
            expensesBar = calculateCurrentValue(fromExpensesBar, toExpensesBar, progress)

            // Balance
            balance = calculateCurrentValue(fromBalance, toBalance, progress)
            balanceBar = calculateCurrentValue(fromBalanceBar, toBalanceBar, progress)
            invalidate()
        }
    }

    private val currencyFormat = DecimalFormat.getCurrencyInstance()

    // Dimensions
    private val textMargin = dpToPx(4f) // 4
    private val labelTextSize = spToPx(14f)
    private val valueTextSize = spToPx(16f)
    private val baseLineRadius = dpToPx(1f)
    private val baseLineThickness = dpToPx(1f)
    private val barWidth = dpToPx(36f)
    private val barRadius = dpToPx(4f)
    private val minBarHeight = dpToPx(100f)

    // Paints
    private val grayPaint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.darker_gray)
    }
    private val grayLabelPaint = Paint(grayPaint).apply {
        textSize = labelTextSize
        typeface = Typeface.SANS_SERIF
    }
    private val bluePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.expenses_graph_base_color)
    }
    private val blueValuePaint = TextPaint(bluePaint).apply {
        textSize = valueTextSize
        typeface = Typeface.SANS_SERIF
    }
    private val greenPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.green)
    }
    private val greenValuePaint = TextPaint(greenPaint).apply {
        textSize = valueTextSize
        typeface = Typeface.SANS_SERIF
    }
    private val redPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.red)
    }
    private val redValuePaint = TextPaint(redPaint).apply {
        textSize = valueTextSize
        typeface = Typeface.SANS_SERIF
    }

    private fun init(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ExpensesGraph,
                0, 0
        ).apply {
            try {
                if (isInEditMode) {
                    incomeLabel = getString(R.styleable.ExpensesGraph_incomeLabel) ?: "Income"
                    expensesLabel = getString(R.styleable.ExpensesGraph_expensesLabel) ?: "Expenses"
                    balanceLabel = getString(R.styleable.ExpensesGraph_balanceLabel) ?: "Balance"

                    income = 6500f
                    expenses = 8754f

                    val maxValue = max(income, expenses)

                    // Income
                    incomeBar = income / maxValue

                    // Expenses
                    expensesBar = expenses / maxValue

                    // Balance
                    balance = income - expenses
                    balanceBar = balance / maxValue
                } else {
                    incomeLabel = getString(R.styleable.ExpensesGraph_incomeLabel) ?: "N/A"
                    expensesLabel = getString(R.styleable.ExpensesGraph_expensesLabel) ?: "N/A"
                    balanceLabel = getString(R.styleable.ExpensesGraph_balanceLabel) ?: "N/A"
                }

            } finally {
                recycle()
            }
        }
    }

    @Suppress("unused")
    fun updateGraph(newIncome: Float, newExpenses: Float) {
        if (newIncome <= 0 || newExpenses <= 0)
            throw IllegalArgumentException("income and expenses must both be positive")

        progressAnimator.pause()

        val maxValue = max(newIncome, newExpenses)

        // Income
        fromIncome = income
        toIncome = newIncome
        fromIncomeBar = incomeBar
        toIncomeBar = newIncome / maxValue


        // Expenses
        fromExpenses = expenses
        toExpenses = newExpenses
        fromExpensesBar = expensesBar
        toExpensesBar = newExpenses / maxValue

        // Balance
        val newBalance = newIncome - newExpenses
        fromBalance = balance
        toBalance = newBalance
        fromBalanceBar = balanceBar
        toBalanceBar = newBalance / maxValue

        progressAnimator.setFloatValues(0f, 1f)
    }

    private fun calculateCurrentValue(from: Float, to: Float, progress: Float): Float {
        return if (to >= from) {
            from + progress * (to - from)
        } else {
            from - progress * (from - to)
        }
    }

    /**
     * For convenience, the drawing is done bottom-up
     * */
    override fun onDraw(canvas: Canvas) {
        canvas.drawLabels()

        canvas.drawBaseLine()

        canvas.drawGraphBars()
    }

    private fun Canvas.drawLabels() = withTranslation(y = (height - paddingBottom).toFloat()) {
        // Draw income, expenses and balance (in this order) towards the layout direction
        val pxBetweenEdgeAndFirstLabel = (width - paddingLeft - paddingRight) / 6f
        val incomeLocation = if (layoutDirection == LAYOUT_DIRECTION_RTL) 5 else 1
        val balanceLocation = if (layoutDirection == LAYOUT_DIRECTION_RTL) 1 else 5
        withTranslation(x = paddingLeft + incomeLocation * pxBetweenEdgeAndFirstLabel) {
            drawLabel(incomeLabel)
        }
        withTranslation(x = paddingLeft + 3 * pxBetweenEdgeAndFirstLabel) {
            drawLabel(expensesLabel)
        }
        withTranslation(x = paddingLeft + balanceLocation * pxBetweenEdgeAndFirstLabel) {
            drawLabel(balanceLabel)
        }
    }

    private fun Canvas.drawLabel(label: String) = drawText(
            label,
            -(grayLabelPaint.measureText(label) / 2),
            0f,
            grayLabelPaint
    )

    private fun Canvas.drawBaseLine() = withTranslation(x = paddingLeft.toFloat(), y = height - paddingBottom - labelTextSize - textMargin) {
        val rectWidth = width - paddingLeft - paddingRight
        drawRoundRect(
                0f,
                -baseLineThickness,
                rectWidth.toFloat(),
                0f,
                baseLineRadius,
                baseLineRadius,
                grayLabelPaint
        )
    }

    private fun Canvas.drawGraphBars() = withTranslation(y = height - paddingBottom - labelTextSize - textMargin - baseLineThickness) {
        val maxBarHeight =
                height - paddingBottom - paddingTop - baseLineThickness - labelTextSize - valueTextSize - 2f * textMargin
        val pxBetweenEdgeAndFirstBar = (width - paddingLeft - paddingRight) / 6f

        // Draw income, expenses and balance (in this order) towards the layout direction
        val incomeLocation = if (layoutDirection == LAYOUT_DIRECTION_RTL) 5 else 1
        val balanceLocation = if (layoutDirection == LAYOUT_DIRECTION_RTL) 1 else 5
        withTranslation(x = paddingLeft + incomeLocation * pxBetweenEdgeAndFirstBar) {
            drawBar(bluePaint, incomeBar, blueValuePaint, income, maxBarHeight)
        }
        withTranslation(x = paddingLeft + 3 * pxBetweenEdgeAndFirstBar) {
            drawBar(bluePaint, expensesBar, blueValuePaint, expenses, maxBarHeight)
        }
        withTranslation(x = paddingLeft + balanceLocation * pxBetweenEdgeAndFirstBar) {
            val barPaint = if (balance >= 0) greenPaint else redPaint
            val valuePaint = if (balance >= 0) greenValuePaint else redValuePaint
            drawBar(barPaint, abs(balanceBar), valuePaint, balance, maxBarHeight)
        }
    }

    private fun Canvas.drawBar(
            barPaint: Paint,
            bar: Float,
            valuePaint: Paint,
            value: Float,
            maxHeight: Float
    ) {
        val barHeight = bar * maxHeight

        val top = -barHeight
        val bottom = barRadius * 2f
        val left = -(barWidth / 2f)
        val right = barWidth / 2f

        // Clipping is needed to allow drawing the same radius if bar is smaller then radius
        withClip(left, top, right, 0f) {
            drawRoundRect(left, top, right, bottom, barRadius, barRadius, barPaint)
        }

        val formattedValue = currencyFormat.format(value)
        drawText(
                formattedValue,
                -(valuePaint.measureText(formattedValue) / 2),
                -(barHeight + textMargin),
                valuePaint
        )
    }

    private fun dpToPx(dp: Float) = dimensionAsPx(dp, TypedValue.COMPLEX_UNIT_DIP)

    private fun spToPx(sp: Float) = dimensionAsPx(sp, TypedValue.COMPLEX_UNIT_SP)

    private fun dimensionAsPx(value: Float, unit: Int): Float {
        return TypedValue.applyDimension(unit, value, context.resources.displayMetrics)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val requestedWidthMode = MeasureSpec.getMode(widthMeasureSpec)

        val requestedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val requestedHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val desiredWidth: Int = getDesiredWidth().toInt()
        val desiredHeight: Int = getDesiredHeight().toInt()

        val width = when (requestedWidthMode) {
            MeasureSpec.EXACTLY -> requestedWidth
            MeasureSpec.UNSPECIFIED -> desiredWidth
            MeasureSpec.AT_MOST -> min(requestedWidth, desiredWidth)
            else -> desiredWidth
        }

        val height = when (requestedHeightMode) {
            MeasureSpec.EXACTLY -> requestedHeight
            MeasureSpec.UNSPECIFIED -> desiredHeight
            MeasureSpec.AT_MOST -> min(requestedHeight, desiredHeight)
            else -> desiredHeight
        }
        setMeasuredDimension(width, height)
    }

    private fun getDesiredWidth(): Float {
        val incomeLabelWidth = grayLabelPaint.measureText(incomeLabel)
        val expensesLabelWidth = grayLabelPaint.measureText(expensesLabel)
        val balanceLabelWidth = grayLabelPaint.measureText(balanceLabel)
        val labelsWidth = incomeLabelWidth + expensesLabelWidth + balanceLabelWidth
        val barsWidth = 3 * barWidth

        return 2 * max(labelsWidth, barsWidth)
    }

    private fun getDesiredHeight(): Float {
        return 2 * textMargin + labelTextSize + baseLineThickness + valueTextSize + minBarHeight
    }
}