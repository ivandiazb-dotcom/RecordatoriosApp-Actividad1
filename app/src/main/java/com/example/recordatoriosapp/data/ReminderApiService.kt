package com.example.recordatoriosapp.data

import com.example.recordatoriosapp.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

interface ReminderApiService {

    @GET("reminders")
    suspend fun getAll(): Response<List<ReminderDto>>

    @GET("reminders/{id}")
    suspend fun getById(@Path("id") id: String): Response<ReminderDto>

    @PUT("reminders/{id}")
    suspend fun upsert(
        @Path("id") id: String,
        @Body reminder: ReminderDto
    ): Response<ReminderDto>

    @DELETE("reminders/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>
}

sealed interface RemoteResult<out T> {
    data class Success<T>(val data: T) : RemoteResult<T>
    data class HttpError(val code: Int, val message: String? = null) : RemoteResult<Nothing>
    data object Timeout : RemoteResult<Nothing>
    data object NoConnection : RemoteResult<Nothing>
    data class InvalidData(val cause: Throwable? = null) : RemoteResult<Nothing>
}

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): RemoteResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                RemoteResult.Success(body)
            } else {
                RemoteResult.InvalidData()
            }
        } else {
            RemoteResult.HttpError(response.code(), response.message())
        }
    } catch (e: SocketTimeoutException) {
        RemoteResult.Timeout
    } catch (e: IOException) {
        RemoteResult.NoConnection
    } catch (e: Exception) {
        RemoteResult.InvalidData(e)
    }
}

object RetrofitClient {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: ReminderApiService = retrofit.create(ReminderApiService::class.java)
}