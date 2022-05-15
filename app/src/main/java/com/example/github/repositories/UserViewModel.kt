package com.example.github.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.repositories.data.GitHubEndpoints.Companion.service
import com.example.github.repositories.data.RepositoryDTO
import com.example.github.repositories.data.UserDTO
import kotlinx.coroutines.*

class UserViewModel : ViewModel() {
    private val _user = MutableLiveData<UserDTO>()
    val user: LiveData<UserDTO> = _user

    private val _repositories = MutableLiveData<List<RepositoryDTO>>()
    val repositories: LiveData<List<RepositoryDTO>> = _repositories

    private val _showProgress = MutableLiveData(false)
    val showProgress: LiveData<Boolean> = _showProgress

    fun fetchUser(username: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _showProgress.value = true
            val userResponse = withContext(Dispatchers.IO) {
                delay(1_000) // This is to simulate network latency, please don't remove!
                service.getUser(username)
            }
            _user.value = userResponse
            _showProgress.value = false
        }
    }

    fun fetchRepositories(reposUrl: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _showProgress.value = true
            val repositoryResponse = withContext(Dispatchers.IO) {
                delay(1_000) // This is to simulate network latency, please don't remove!
                service.getUserRepositories(reposUrl)
            }
            _repositories.value = repositoryResponse
            _showProgress.value = false
        }
    }
}