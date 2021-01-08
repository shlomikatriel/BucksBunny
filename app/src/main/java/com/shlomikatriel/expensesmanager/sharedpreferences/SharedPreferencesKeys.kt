package com.shlomikatriel.expensesmanager.sharedpreferences

import androidx.appcompat.app.AppCompatDelegate

interface BaseKey<T> {
    fun getKey(): String

    fun getDefault(): T?
}

enum class IntKey(private val key: String, private val default: Int) : BaseKey<Int> {

    DARK_MODE("dark_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    LATEST_VERSION_CODE("latest_version_code", -1);

    override fun getKey() = key

    override fun getDefault() = default

}

enum class FloatKey(private val key: String, private val default: Float) : BaseKey<Float> {

    INCOME("income", 5000f);

    override fun getKey() = key

    override fun getDefault() = default

}

enum class BooleanKey(private val key: String, private val default: Boolean) : BaseKey<Boolean> {

    SHOULD_SHOW_ONBOARDING("should_show_onboarding", true),
    FIREBASE_ANALYTICS_ENABLED("firebase_analytics_enabled", false),
    FIREBASE_CRASHLYTICS_ENABLED("firebase_crashlytics_enabled", false);

    override fun getKey() = key

    override fun getDefault() = default

}

enum class StringKey(private val key: String, private val default: String?) : BaseKey<String> {

    LATEST_VERSION_NAME("latest_version_name", null);

    override fun getKey() = key

    override fun getDefault() = default

}