package com.example.github.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.repositories.data.*
import com.example.github.repositories.data.GitHubEndpoints.Companion.service
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {

    private val _repositories = MutableLiveData<List<RepositoryDTO>>()
    val repositories: LiveData<List<RepositoryDTO>> = _repositories

    private val _showProgress = MutableLiveData(false)
    val showProgress: LiveData<Boolean> = _showProgress

    init {
        _showProgress.value = true
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch(Dispatchers.Main) {
            delay(1_000) // This is to simulate network latency, please don't remove!
            var response: Response?
            withContext(Dispatchers.IO) {
                response = service.searchRepositories(QUERY, SORT, ORDER)
            }
            _repositories.value = response?.items
            _showProgress.value = false
        }
    }

    fun refresh() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(1_000) // This is to simulate network latency, please don't remove!
            var response: Response?
            withContext(Dispatchers.IO) {
                response = service.searchRepositories(QUERY, SORT, ORDER)
            }
            _repositories.value = response?.items
        }
    }
}