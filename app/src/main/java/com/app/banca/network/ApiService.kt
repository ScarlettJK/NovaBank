package com.app.banca.network

import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("account")
    suspend fun createAccount(): AccountResponse

    @GET("account")
    suspend fun getAccount(): AccountResponse

    @GET("transaction")
    suspend fun getTransactions(): List<TransactionResponse>
}