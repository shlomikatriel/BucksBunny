package com.shlomikatriel.expensesmanager.logs

enum class Tag(val tag: String) {
    LOGS("Logs"),
    MIGRATION("Migration"),
    SETTINGS("Settings"),
    EXPENSES("Expenses"),
    DATABASE("Database"),
    NAVIGATION("Navigation"),
    IN_APP_REVIEW("InAppReview"),
    IN_APP_UPDATE("")
}
