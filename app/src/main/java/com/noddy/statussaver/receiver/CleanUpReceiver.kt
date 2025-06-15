package com.noddy.statussaver.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import com.noddy.statussaver.R
import java.io.File
import java.util.Date

class CleanUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            context.getString(R.string.app_name)
        )

        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            val now = Date().time
            val sevenDays = 7 * 24 * 60 * 60 * 1000L

            files?.forEach { file ->
                if (file.isFile && (now - file.lastModified()) > sevenDays) {
                    file.delete()
                }
            }
        }
    }
}