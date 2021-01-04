package com.shlomikatriel.expensesmanager.logs

import android.content.Context
import androidx.annotation.WorkerThread
import com.bosphere.filelogger.FL
import com.bosphere.filelogger.FLConfig
import com.bosphere.filelogger.FLConst
import com.shlomikatriel.expensesmanager.BuildConfig
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject


class LogManager
@Inject constructor(
    private val context: Context,
    private val emptyLogger: EmptyLogger,
    private val logFileFormatter: LogFileFormatter
) {

    companion object {
        const val LOG_ZIP_FILE_NAME = "ExpensesManagerLogs.zip"
        const val SYSTEM_PROPERTIES_FILE_NAME = "system_properties.log"
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
            Logger.i("Deleting existing logs zip file [deleted=${file.delete()}]")
        }

        createPropertiesFile()

        val zipParameters = ZipParameters().apply {
            compressionMethod = CompressionMethod.DEFLATE
            compressionLevel = CompressionLevel.FASTEST
            isEncryptFiles = true
            encryptionMethod = EncryptionMethod.ZIP_STANDARD
        }

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
            Logger.i("Deleting existing system properties file [deleted=${file.delete()}]")
        }

        val builder = StringBuilder()
        var process: Process? = null
        var reader: BufferedReader? = null
        try {
            process = Runtime.getRuntime().exec("getprop")
            reader = BufferedReader(InputStreamReader(process.inputStream, "UTF-8"))
            var line: String?
            do {
                line = reader.readLine()
                builder.append(line).append('\n')
            } while (line != null)
        } finally {
            reader?.close()
            process?.destroy()
        }
        file.writeText(builder.toString())
    }

    private fun Context.getLogFolder() = File("${filesDir.absolutePath}${File.separator}logs")
}