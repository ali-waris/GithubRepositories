package com.example.github.repositories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.repositories.data.*
import com.example.github.repositories.data.GitHubEndpoints.Companion.service
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {

    val repositories = MutableLiveData<List<RepositoryDTO>>()

    init {
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch(Dispatchers.Main) {
            delay(1_000) // This is to simulate network latency, please don't remove!
            var response: Response?
            withContext(Dispatchers.IO) {
                response = service.searchRepositories(QUERY, SORT, ORDER)
            }
            repositories.value = response?.items
        }
    }

    fun refresh() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(1_000) // This is to simulate network latency, please don't remove!
            var response: Response?
            withContext(Dispatchers.IO) {
                response = service.searchRepositories(QUERY, SORT, ORDER)
            }
            repositories.value = response?.items
        }
    }
}