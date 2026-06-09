package com.curso.banca.data.repository

import com.curso.banca.network.AccountResponse
import com.curso.banca.network.RetrofitClient

class BankRepository {

    private val api = RetrofitClient.api

    suspend fun createAccount(): AccountResponse {
        return api.createAccount()
    }

    suspend fun getAccount(): AccountResponse {
        return api.getAccount()
    }
}