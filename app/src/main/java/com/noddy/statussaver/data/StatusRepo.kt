package com.noddy.statussaver.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.noddy.statussaver.models.MEDIA_TYPE_IMAGE
import com.noddy.statussaver.models.MEDIA_TYPE_VIDEO
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.utils.SharedPrefKeys
import com.noddy.statussaver.utils.SharedPrefUtils
import com.noddy.statussaver.utils.getFileExtension
import com.noddy.statussaver.utils.isStatusExist

class StatusRepo(val context: Context) {

    val whatsAppStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppBusinessStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()

    private val activity = context as Activity
    private val TAG = "StatusRepo"

    fun getAllStatuses(whatsAppType: String = Constants.TYPE_WHATSAPP_MAIN) {
        val treeUriString = when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN ->
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")
            else ->
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")
        }

        if (treeUriString.isNullOrEmpty()) {
            Log.d(TAG, "No tree URI found for $whatsAppType")
            postEmptyList(whatsAppType)
            return
        }

        val treeUri = treeUriString.toUri()
        Log.d(TAG, "getAllStatuses: $treeUri")

        activity.contentResolver.takePersistableUriPermission(
            treeUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val fileDocument = DocumentFile.fromTreeUri(activity, treeUri)
        fileDocument?.listFiles()?.forEach { file ->
            processFile(file, whatsAppType)
        } ?: run {
            Log.e(TAG, "Failed to access directory for $whatsAppType")
            postEmptyList(whatsAppType)
        }
    }

    private fun processFile(file: DocumentFile, whatsAppType: String) {
        if (file.name != ".nomedia" && file.isFile) {
            val fileName = file.name ?: return
            val isDownloaded = context.isStatusExist(fileName)
            val extension = getFileExtension(fileName)
            val type = if (extension == "mp4") MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE

            val model = MediaModel(
                pathUri = file.uri.toString(),
                fileName = fileName,
                type = type,
                isDownloaded = isDownloaded
            )

            when (whatsAppType) {
                Constants.TYPE_WHATSAPP_MAIN ->
                    (whatsAppStatusesLiveData.value ?: ArrayList()).add(model)
                else ->
                    (whatsAppBusinessStatusesLiveData.value ?: ArrayList()).add(model)
            }
        }
    }

    private fun postEmptyList(whatsAppType: String) {
        when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN ->
                whatsAppStatusesLiveData.postValue(ArrayList())
            else ->
                whatsAppBusinessStatusesLiveData.postValue(ArrayList())
        }
    }
}