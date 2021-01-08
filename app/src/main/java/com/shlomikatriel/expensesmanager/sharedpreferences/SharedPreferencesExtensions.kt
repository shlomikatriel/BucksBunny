package com.shlomikatriel.expensesmanager.sharedpreferences

import android.content.SharedPreferences
import androidx.core.content.edit

fun SharedPreferences.putInt(key: IntKey, value: Int) = edit { putInt(key.getKey(), value) }

fun SharedPreferences.getInt(key: IntKey) = getInt(key.getKey(), key.getDefault())

fun SharedPreferences.putFloat(key: FloatKey, value: Float) = edit { putFloat(key.getKey(), value) }

fun SharedPreferences.getFloat(key: FloatKey) = getFloat(key.getKey(), key.getDefault())

fun SharedPreferences.putBoolean(key: BooleanKey, value: Boolean) = edit { putBoolean(key.getKey(), value) }

fun SharedPreferences.getBoolean(key: BooleanKey) = getBoolean(key.getKey(), key.getDefault())

fun SharedPreferences.putString(key: StringKey, value: String) = edit { putString(key.getKey(), value) }

fun SharedPreferences.getString(key: StringKey) = getString(key.getKey(), key.getDefault())