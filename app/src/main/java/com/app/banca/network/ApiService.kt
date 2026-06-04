package com.app.banca.network

import retrofit2.http.GET

interface ApiService {

    // Obtiene la cuenta del usuario (número de cuenta y saldo)
    @GET("account")
    suspend fun getAccount(): AccountResponse

    // Obtiene la lista de movimientos recientes
    @GET("transaction")
    suspend fun getTransactions(): List<TransactionResponse>
}


