package com.shlomikatriel.expensesmanager.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {
    @Insert
    fun insert(value: T)

    @Update
    fun update(value: T)

    @Delete
    fun delete(value: T)
}