package com.noddy.statussaver.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.SharedPrefUtils

class FavoritesRepo(context: Context) {
    private val PREFS_KEY = "FAVORITES_LIST"

    fun getFavorites(): ArrayList<MediaModel> {
        val json = SharedPrefUtils.getPrefString(PREFS_KEY, "")
        return if (json.isNullOrEmpty()) ArrayList()
        else Gson().fromJson(json, object : TypeToken<ArrayList<MediaModel>>() {}.type)
    }

    fun addFavorite(media: MediaModel) {
        val favorites = getFavorites().apply { add(media) }
        saveFavorites(favorites)
    }

    fun removeFavorite(media: MediaModel) {
        val favorites = getFavorites().apply { removeAll { it.pathUri == media.pathUri } }
        saveFavorites(favorites)
    }

    fun isFavorite(media: MediaModel): Boolean {
        return getFavorites().any { it.pathUri == media.pathUri }
    }

    private fun saveFavorites(list: ArrayList<MediaModel>) {
        SharedPrefUtils.putPrefString(PREFS_KEY, Gson().toJson(list))
    }
}