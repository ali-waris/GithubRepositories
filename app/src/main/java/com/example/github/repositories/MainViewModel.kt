package com.example.github.repositories

import androidx.lifecycle.*
import com.example.github.repositories.data.*
import com.example.github.repositories.data.GitHubEndpoints.Companion.service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val localDataStore: LocalDataStore) : ViewModel() {

    private val _repositories = MutableLiveData<List<RepositoryDTO>>()
    val repositories: LiveData<List<RepositoryDTO>> = _repositories

    private val _showProgress = MutableLiveData(false)
    val showProgress: LiveData<Boolean> = _showProgress

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    private val _bookmarks = MutableLiveData<List<Int?>>()
    val bookmarks: LiveData<List<Int?>> = _bookmarks

    init { fetchItems() }

    fun getBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            _bookmarks.postValue(localDataStore.getBookmarks())
        }
    }

    fun fetchItems(showProgress: Boolean = true) {
        viewModelScope.launch(Dispatchers.Main) {
            if (showProgress)
                _showProgress.value = true
            val repositories = withContext(Dispatchers.IO) {
                getBookmarks()
                delay(1_000) // This is to simulate network latency, please don't remove!
                val response = service.searchRepositories(QUERY, SORT, ORDER)
                if (response.isSuccessful)
                    response.body()?.items
                else {
                    _error.postValue(response.message())
                    listOf()
                }
            }
            repositories?.let { _repositories.value = it }
            _showProgress.value = false
        }
    }

    fun resetError() {
        _error.value = ""
    }
}

class MainViewModelFactory(private val localDataStore: LocalDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(localDataStore) as T
    }
}