package com.example.github.repositories.helpers

import okhttp3.*
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = try {
            val builder: Request.Builder = chain.request().newBuilder()
            chain.proceed(builder.build())
        } catch (e:Exception) {
            val msg: String
            when (e) {
                is SocketTimeoutException -> {
                    msg = "Timeout - Please check your internet connection."
                }
                is UnknownHostException -> {
                    msg = "Unable to make a connection. Please check your internet."
                }
                is ConnectionShutdownException -> {
                    msg = "Connection shutdown. Please check your internet."
                }
                is IOException -> {
                    msg = "Server is unreachable, please try again later."
                }
                else -> {
                    msg = "Something went wrong."
                }
            }

            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(12345)
                .message(msg)
                .body(ResponseBody.create(null, "{${e}}"))
                .build()
        }
        return response
    }

}