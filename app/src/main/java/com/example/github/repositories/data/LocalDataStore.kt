package com.example.github.repositories.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class LocalDataStore(private val context:Context) {
    private val gson = Gson()
    private val prefKey = "bookmarks"
    private val prefs: SharedPreferences = context.getSharedPreferences("gitBookmarks", MODE_PRIVATE)

    fun bookmarkRepo(repositoryId: Int?, bookmarked: Boolean) {
        val bookmarks = getBookmarks().toMutableList()

        if (!bookmarked)
            bookmarks.add(repositoryId)
        else
            bookmarks.remove(repositoryId)

        val bookmarksString = if (bookmarks.isNotEmpty()) gson.toJson(bookmarks) else ""
        prefs.edit().putString(prefKey, bookmarksString).apply()
    }

    fun getBookmarks(): List<Int?> {
        val bookmarks = prefs.getString(prefKey, "")
        if (bookmarks.isNullOrEmpty()) return emptyList()
        return gson.fromJson(bookmarks, object : TypeToken<List<Int?>>() {}.type)
    }
}