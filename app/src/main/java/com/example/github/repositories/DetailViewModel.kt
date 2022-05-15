package com.example.github.repositories

import android.content.Intent
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.github.repositories.data.BOOKMARK_EVENT
import com.example.github.repositories.data.LocalDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailViewModel(
    private val localDataStore: LocalDataStore,
    private val localBroadcastManager: LocalBroadcastManager
) : ViewModel() {
    private val _bookmarks = MutableLiveData<List<Int?>>()
    val bookmarks: LiveData<List<Int?>> = _bookmarks

    init {
        getBookmarks()
    }

    private fun getBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            _bookmarks.postValue(localDataStore.getBookmarks())
        }
    }

    fun bookmarkRepo(repoId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            val isBookmarked = _bookmarks.value?.contains(repoId) ?: false
            localDataStore.bookmarkRepo(repoId, isBookmarked)
            getBookmarks()
            sendBookmarkBroadcast()
        }
    }

    private fun sendBookmarkBroadcast() {
        val intent = Intent(BOOKMARK_EVENT)
        localBroadcastManager.sendBroadcast(intent)
    }

    fun formattedCreatedAt(createdAt: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
            val d: Date? = sdf.parse(createdAt)
            if (d != null) ", at ${output.format(d)}" else ""
        } catch (e: Exception) {
            ""
        }
    }
}

class DetailViewModelFactory(
    private val localDataStore: LocalDataStore,
    private val localBroadcastManager: LocalBroadcastManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailViewModel(localDataStore, localBroadcastManager) as T
    }
}