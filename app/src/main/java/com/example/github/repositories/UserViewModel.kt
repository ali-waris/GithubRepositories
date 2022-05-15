package com.example.github.repositories

import androidx.lifecycle.*
import com.example.github.repositories.data.GitHubEndpoints.Companion.service
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserViewModel(userLogin: String, private val localDataStore: LocalDataStore) : ViewModel() {
    private val _user = MutableLiveData<UserDTO?>()
    val user: LiveData<UserDTO?> = _user

    private val _repositories = MutableLiveData<List<RepositoryDTO>>()
    val repositories: LiveData<List<RepositoryDTO>> = _repositories

    private val _showProgress = MutableLiveData(false)
    val showProgress: LiveData<Boolean> = _showProgress

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    private val _bookmarks = MutableLiveData<List<Int?>>()
    val bookmarks: LiveData<List<Int?>> = _bookmarks

    init {
        getBookmarks()
        fetchUser(userLogin)
    }

    fun getBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            _bookmarks.postValue(localDataStore.getBookmarks())
        }
    }

    private fun fetchUser(username: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _showProgress.value = true
            val user = withContext(Dispatchers.IO) {
                delay(1_000) // This is to simulate network latency, please don't remove!
                val userResponse = service.getUser(username)
                if (userResponse.isSuccessful)
                    userResponse.body()
                else {
                    _error.postValue(userResponse.message())
                    null
                }
            }
            _user.value = user
            _showProgress.value = false
        }
    }

    fun fetchRepositories(reposUrl: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _showProgress.value = true
            val repositories = withContext(Dispatchers.IO) {
                delay(1_000) // This is to simulate network latency, please don't remove!
                val repositoryResponse = service.getUserRepositories(reposUrl)
                if (repositoryResponse.isSuccessful)
                    repositoryResponse.body()
                else {
                    _error.postValue(repositoryResponse.message())
                    null
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

class UserViewModelFactory(private val user: String, private val localDataStore: LocalDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(user, localDataStore) as T
    }
}