package com.shlomikatriel.expensesmanager.sharedpreferences

interface BaseKey<T> {
    fun getKey(): String

    fun getDefault(): T?
}

enum class FloatKey(private val key: String, private val default: Float) : BaseKey<Float> {

    INCOME("income", 5000f);

    override fun getKey() = key

    override fun getDefault() = default

}

enum class BooleanKey(private val key: String, private val default: Boolean) : BaseKey<Boolean> {

    CHOOSE_INCOME_DIALOG_SHOWN("choose_income_dialog_shown", false);

    override fun getKey() = key

    override fun getDefault() = default

}