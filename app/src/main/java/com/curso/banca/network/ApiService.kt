package com.curso.banca.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("account")
    suspend fun createAccount(): AccountResponse

    @GET("account")
    suspend fun getAccount(): AccountResponse

    @GET("beneficiaries")
    suspend fun getBeneficiaries(): List<BeneficiaryResponse>

    @POST("beneficiaries")
    suspend fun createBeneficiary(
        @Body body: BeneficiaryRequest
    ): BeneficiaryResponse


    @PUT("beneficiaries/{id}")
    suspend fun updateBeneficiary(
        @Path("id") id: String,
        @Body body: BeneficiaryRequest
    ): BeneficiaryResponse

    @DELETE("beneficiaries/{id}")
    suspend fun deleteBeneficiary(
        @Path("id") id: String
    ): DeleteResponse

    @GET("transaction")
    suspend fun getTransactions(): List<TransactionResponse>
}