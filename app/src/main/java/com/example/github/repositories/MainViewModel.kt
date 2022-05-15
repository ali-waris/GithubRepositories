package com.example.github.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.repositories.data.GitHubEndpoints.Companion.service
import com.example.github.repositories.data.ORDER
import com.example.github.repositories.data.QUERY
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.SORT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _repositories = MutableLiveData<List<RepositoryDTO>>()
    val repositories: LiveData<List<RepositoryDTO>> = _repositories

    private val _showProgress = MutableLiveData(false)
    val showProgress: LiveData<Boolean> = _showProgress

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    init {
        _showProgress.value = true
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch(Dispatchers.Main) {
            val repositories = withContext(Dispatchers.IO) {
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