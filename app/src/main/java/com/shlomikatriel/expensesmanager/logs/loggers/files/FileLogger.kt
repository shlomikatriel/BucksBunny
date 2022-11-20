package com.shlomikatriel.expensesmanager.logs.loggers.files

import android.content.Context
import android.util.Log
import com.shlomikatriel.expensesmanager.logs.LogManager
import com.shlomikatriel.expensesmanager.logs.Tag
import com.shlomikatriel.expensesmanager.logs.logError
import com.shlomikatriel.expensesmanager.logs.logInfo
import com.shlomikatriel.expensesmanager.logs.loggers.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread

@Singleton
class FileLogger @Inject constructor(
    @ApplicationContext private val context: Context
) : Logger() {

    private val queue = LinkedBlockingDeque<LogMessage>()
    private val timeFormat = SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH)
    private var isConsumerThreadRunning = false

    override fun info(tag: Tag, message: String, t: Throwable?) {
        ensureThread()
        produceLogs(tag, message, t, LOG_LEVEL_INFO)
    }

    override fun warning(tag: Tag, message: String, t: Throwable?) {
        ensureThread()
        produceLogs(tag, message, t, LOG_LEVEL_WARNING)
    }

    override fun error(tag: Tag, message: String, t: Throwable?) {
        ensureThread()
        produceLogs(tag, message, t, LOG_LEVEL_ERROR)
    }

    private fun produceLogs(tag: Tag, message: String, t: Throwable?, logLevel: Char) {
        val logMessage = LogMessage(System.currentTimeMillis(), tag, message.appendStackTraceIfNeeded(t), logLevel)
        queue.put(logMessage)
    }

    private fun String.appendStackTraceIfNeeded(t: Throwable?) = if (t != null) "$this\n${Log.getStackTraceString(t)}" else this

    private fun ensureThread() {
        if (!isConsumerThreadRunning) {
            synchronized(this) {
                if (!isConsumerThreadRunning) {
                    isConsumerThreadRunning = true
                    thread(name = "LogsConsumerThread") {
                        consumeLogsForever()
                    }
                }
            }
        }
    }

    private fun consumeLogsForever() {
        logInfo(Tag.LOGS, "Logs consumer thread started")
        try {
            val logFolder = File(context.filesDir, LogManager.LOG_FOLDER_NAME).apply {
                mkdir()
            }
            val logFile = File(logFolder, LogManager.LOG_FILE_NAME)
            val logFileOld = File(logFolder, LogManager.LOG_FILE_NAME_OLD)

            while (true) {
                var logLine = queue.take()
                val writer = logFile.writer()

                do {
                    writer.write(logLine)
                    logLine = queue.poll(2L, TimeUnit.SECONDS)
                } while (logLine != null)

                writer.flush()
                writer.close()

                // Rotate
                if (logFile.length() > LOG_FILE_SIZE_LIMIT) {
                    logFileOld.delete()
                    logFile.renameTo(logFileOld)
                }
            }
        } catch (t: Throwable) {
            logError(Tag.LOGS, "Error executing logs consumer thread", t)
        }
        isConsumerThreadRunning = false
    }

    private fun File.writer() = FileOutputStream(this, true).writer()

    private fun OutputStreamWriter.write(logMessage: LogMessage) {
        val fullMessage = buildString {
            append(timeFormat.format(logMessage.timeStamp))
            append(' ')
            append(logMessage.logLevel)
            append('/')
            append(logMessage.tag.tag)
            append(": ")
            append(logMessage.message)
            append('\n')
        }
        append(fullMessage)
    }

    companion object {
        private const val TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"

        private const val LOG_LEVEL_INFO = 'I'
        private const val LOG_LEVEL_WARNING = 'W'
        private const val LOG_LEVEL_ERROR = 'E'

        private const val LOG_FILE_SIZE_LIMIT = 15 * 1024 * 1024 // 15MB
    }
}