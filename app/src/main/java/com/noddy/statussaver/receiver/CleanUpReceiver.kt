package com.noddy.statussaver.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noddy.statussaver.utils.FileUtils
import java.io.File
import java.util.Date

class CleanUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            context.getString(R.string.app_name)
        )

        if (folder.exists()) {
            val files = folder.listFiles()
            val now = Date().time
            val sevenDays = 7 * 24 * 60 * 60 * 1000L

            files?.forEach { file ->
                if (now - file.lastModified() > sevenDays) {
                    file.delete()
                }
            }
        }
    }
}