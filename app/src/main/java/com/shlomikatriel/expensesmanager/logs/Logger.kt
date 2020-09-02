package com.shlomikatriel.expensesmanager.logs


class Logger {
    companion object {

        private var logger: org.apache.log4j.Logger? = null

        const val LOG_FOLDER = "logs"

        fun d(message: String) = logger!!.debug(addPrefix(message))

        fun i(message: String) = logger!!.info(addPrefix(message))

        fun w(message: String, throwable: Throwable? = null) =
            logger!!.warn(addPrefix(message), throwable)

        fun e(message: String, throwable: Throwable? = null) =
            logger!!.error(addPrefix(message), throwable)

        private fun addPrefix(message: String): String {
            val thread = Thread.currentThread()
            return thread.stackTrace[4].let {
                val classSimpleName = simplifyFullyQualifiedClassName(it.className)
                "[${thread.id}] $classSimpleName#${it.methodName}: $message"
            }
        }

        private fun simplifyFullyQualifiedClassName(className: String) =
            className.substringAfterLast('.')

        fun setLogger(logger: org.apache.log4j.Logger) {
            if (this.logger != null) {
                throw IllegalStateException("Logger cannot be initialized twice")
            }
            this.logger = logger
        }
    }
}