package com.app.banca.network

import com.google.gson.annotations.SerializedName

// Formato de fecha que usa Firebase
data class FirebaseTimestamp(
    @SerializedName("_seconds") val seconds: Long,
    @SerializedName("_nanoseconds") val nanoseconds: Int
)

// Datos de la cuenta bancaria (GET /account)
data class AccountResponse(
    val id: String,
    val ownerId: String,
    val accountNumber: String,
    val balance: Long,       // En centavos: 5000 = $50.00
    val createdAt: FirebaseTimestamp?
)

// Cada movimiento de la cuenta (GET /transaction)
data class TransactionResponse(
    val id: String,
    val fromAccount: String,
    val toAccount: String,
    val amount: Long,        // En centavos
    val description: String?,
    val status: String,
    val date: FirebaseTimestamp?,
    val direction: String    // "in" = recibiste, "out" = enviaste
)