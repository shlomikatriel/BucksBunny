package com.shlomikatriel.expensesmanager.sharedpreferences

import androidx.appcompat.app.AppCompatDelegate

interface BaseKey<T> {
    fun getKey(): String

    fun getDefault(): T?
}

enum class IntKey(private val key: String, private val default: Int) : BaseKey<Int> {

    DARK_MODE("dark_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

    override fun getKey() = key

    override fun getDefault() = default

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