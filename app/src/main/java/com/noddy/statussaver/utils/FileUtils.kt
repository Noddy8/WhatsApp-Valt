package com.noddy.statussaver.utils

import android.content.Context
import android.os.Environment
import com.noddy.statussaver.R
import java.io.File

fun Context.getStatusFolder(): File {
    return File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
        getString(R.string.app_name)
    )
}