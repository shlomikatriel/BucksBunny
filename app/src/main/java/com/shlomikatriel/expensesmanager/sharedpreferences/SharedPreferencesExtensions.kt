package com.shlomikatriel.expensesmanager.sharedpreferences

import android.content.SharedPreferences

fun SharedPreferences.putInt(key: IntKey, value: Int) = edit().putInt(key.getKey(), value).apply()

fun SharedPreferences.getInt(key: IntKey) = getInt(key.getKey(), key.getDefault())

fun SharedPreferences.putFloat(key: FloatKey, value: Float) = edit().putFloat(key.getKey(), value).apply()

fun SharedPreferences.getFloat(key: FloatKey) = getFloat(key.getKey(), key.getDefault())

fun SharedPreferences.putBoolean(key: BooleanKey, value: Boolean) = edit().putBoolean(key.getKey(), value).apply()

fun SharedPreferences.getBoolean(key: BooleanKey) = getBoolean(key.getKey(), key.getDefault())