package com.shlomikatriel.expensesmanager.logs

import android.content.Context
import androidx.annotation.WorkerThread
import com.bosphere.filelogger.FL
import com.bosphere.filelogger.FLConfig
import com.bosphere.filelogger.FLConst
import com.shlomikatriel.expensesmanager.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import javax.inject.Inject


class LogManager
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val emptyLogger: EmptyLogger,
    private val logFileFormatter: LogFileFormatter
) {

    companion object {
        const val LOG_ZIP_FILE_NAME = "ExpensesManagerLogs.zip"
        const val SYSTEM_PROPERTIES_FILE_NAME = "system_properties.log"
        const val LOGCAT_FILE_NAME = "logcat.log"
    }

    fun initialize() = FLConfig.Builder(context).run {
        defaultTag(BuildConfig.LOG_TAG)
        if (BuildConfig.DEBUG) {
            minLevel(FLConst.Level.V)
        } else {
            minLevel(FLConst.Level.I)
            logger(emptyLogger)
        }
        logToFile(true)
        formatter(logFileFormatter)
        dir(context.getLogFolder())
        retentionPolicy(FLConst.RetentionPolicy.FILE_COUNT)
        maxFileCount(7)
        build()
    }.let {
        FL.init(it)
        FL.setEnabled(true)
    }

    @WorkerThread
    fun collectLogs(): File {
        val logFolder = context.getLogFolder()
        val file = File(logFolder, LOG_ZIP_FILE_NAME)
        if (file.exists()) {
            logInfo("Deleting existing logs zip file [deleted=${file.delete()}]")
        }

        createPropertiesFile()

        createLogcatFile()

        val zipParameters = ZipParameters().apply {
            compressionMethod = CompressionMethod.DEFLATE
            compressionLevel = CompressionLevel.FASTEST
            isEncryptFiles = true
            encryptionMethod = EncryptionMethod.ZIP_STANDARD
        }

        @Suppress("SpellCheckingInspection")
        ZipFile(file, "vdtor".toCharArray())
            .addFiles(logFolder.listFiles()!!.toList(), zipParameters)

        if (!file.exists()) {
            throw IllegalStateException("Logs zip file not created")
        }
        return file
    }

    @WorkerThread
    private fun createPropertiesFile() {
        val file = File(context.getLogFolder(), SYSTEM_PROPERTIES_FILE_NAME)
        if (file.exists()) {
            logInfo("Deleting existing system properties file [deleted=${file.delete()}]")
        }
        logInfo("Collecting properties")

        val builder = StringBuilder()
        var process: Process? = null
        try {
            @Suppress("SpellCheckingInspection")
            process = Runtime.getRuntime().exec("getprop")
            builder.appendInputStream(process.inputStream)
        } finally {
            process?.apply {
                inputStream.close()
                destroy()
            }
        }
        file.writeText(builder.toString())
    }

    @WorkerThread
    private fun createLogcatFile() {
        val file = File(context.getLogFolder(), LOGCAT_FILE_NAME)
        if (file.exists()) {
            logInfo("Deleting existing logcat file [deleted=${file.delete()}]")
        }
        logInfo("Collecting logcat")

        val builder = StringBuilder()
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("logcat -d")
            process.inputStream.bufferedReader()
            builder.append("Input Stream:\n")
                .appendInputStream(process.inputStream)
                .append("\nError Stream:\n")
                .appendInputStream(process.errorStream)
        } finally {
            process?.apply {
                inputStream.close()
                errorStream.close()
                destroy()
            }
        }
        file.writeText(builder.toString())
    }

    private fun StringBuilder.appendInputStream(inputStream: InputStream): StringBuilder {
        val reader: BufferedReader = inputStream.bufferedReader()
        var line: String?
        do {
            line = reader.readLine()
            append(line).append('\n')
        } while (line != null)
        return this
    }

    private fun Context.getLogFolder() = File("${filesDir.absolutePath}${File.separator}logs")
}