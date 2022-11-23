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

enum class LongKey(private val key: String, private val default: Long) : BaseKey<Long> {

    LAST_IN_APP_REVIEW_TIME("last_in_app_review_time", -1);

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
    ANONYMOUS_REPORTS_ENABLED("anonymous_reports_enabled", false);

    override fun getKey() = key

    override fun getDefault() = default

}

enum class StringKey(private val key: String, private val default: String?) : BaseKey<String> {

    LATEST_VERSION_NAME("latest_version_name", null);

    override fun getKey() = key

    override fun getDefault() = default

}