package com.noddy.statussaver.utils

import com.noddy.statussaver.models.MediaModel
import java.io.File
import java.util.Collections

object SortingUtils {
    const val SORT_DATE_ASC = 0
    const val SORT_DATE_DESC = 1
    const val SORT_NAME_ASC = 2
    const val SORT_NAME_DESC = 3
    const val SORT_SIZE_ASC = 4
    const val SORT_SIZE_DESC = 5

    fun sortMediaList(list: ArrayList<MediaModel>, sortType: Int): ArrayList<MediaModel> {
        return when (sortType) {
            SORT_DATE_ASC -> list.sortedBy { File(it.pathUri.toUri()).lastModified() } as ArrayList<MediaModel>
            SORT_DATE_DESC -> list.sortedByDescending { File(it.pathUri.toUri()).lastModified() } as ArrayList<MediaModel>
            SORT_NAME_ASC -> list.sortedBy { it.fileName } as ArrayList<MediaModel>
            SORT_NAME_DESC -> list.sortedByDescending { it.fileName } as ArrayList<MediaModel>
            SORT_SIZE_ASC -> list.sortedBy { File(it.pathUri.toUri()).length() } as ArrayList<MediaModel>
            SORT_SIZE_DESC -> list.sortedByDescending { File(it.pathUri.toUri()).length() } as ArrayList<MediaModel>
            else -> list
        }
    }
}