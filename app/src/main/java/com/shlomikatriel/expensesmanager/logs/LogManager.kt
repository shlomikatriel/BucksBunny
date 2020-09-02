package com.shlomikatriel.expensesmanager.logs

import android.content.Context
import androidx.annotation.WorkerThread
import com.shlomikatriel.expensesmanager.BuildConfig
import de.mindpipe.android.logging.log4j.LogConfigurator
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.apache.log4j.EnhancedPatternLayout
import org.apache.log4j.Level
import org.apache.log4j.RollingFileAppender
import java.io.*
import javax.inject.Inject

class LogManager
@Inject constructor(private val context: Context) {

    companion object {
        const val LOG_ZIP_FILE_NAME = "ExpensesManagerLogs.zip"
        const val SYSTEM_PROPERTIES_FILE_NAME = "system_properties.log"
    }

    @Synchronized
    fun initializeLogger() {

        val logger = org.apache.log4j.Logger.getLogger(BuildConfig.LOG_TAG)
        Logger.setLogger(logger)

        LogConfigurator().apply {
            isUseLogCatAppender = BuildConfig.DEBUG
            isUseFileAppender = false
            rootLevel = Level.DEBUG
            configure()
        }

        org.apache.log4j.Logger.getRootLogger()?.addAppender(
            createRollingFileAppender()
        )
    }

    private fun createRollingFileAppender() = RollingFileAppender(
        EnhancedPatternLayout("%d{ISO8601} %p %m%n"),
        getLogFilePath()
    ).apply {
        name = "RollingAppender"
        maxBackupIndex = 5
        setMaxFileSize("2MB")
        threshold = Level.INFO
    }

    private fun getLogFilePath() = "${getLogFolderPath()}${File.separator}${BuildConfig.LOG_TAG}.log"

    private fun getLogFolderPath() = "${context.filesDir.absolutePath}${File.separator}${Logger.LOG_FOLDER}"

    @WorkerThread
    fun collectLogs(): File {
        val logFolder = File(getLogFolderPath())
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
        val file = File(getLogFolderPath(), SYSTEM_PROPERTIES_FILE_NAME)
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
}