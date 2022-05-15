package com.example.github.repositories.data

import com.example.github.repositories.helpers.ErrorInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit


interface GitHubEndpoints {

    companion object {
        private val oktHttpClient = OkHttpClient.Builder()
            .addInterceptor(ErrorInterceptor())
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)

        private val retrofit = Retrofit.Builder()
            .baseUrl(GITHUB_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(oktHttpClient.build())
            .build()

        val service: GitHubEndpoints by lazy { retrofit.create(GitHubEndpoints::class.java) }
    }

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") q: String,
        @Query("sort") sort: String,
        @Query("order") order: String
    ): Response<RepositoryResponse>

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): Response<UserDTO>

    @GET
    suspend fun getUserRepositories(
        @Url userRepo: String
    ): Response<MutableList<RepositoryDTO>>
}