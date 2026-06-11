package com.curso.banca.network

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

//Beneficiarios (GET /beneficiaries)
data class BeneficiaryRequest(
    val name: String,
    val lastName: String,
    val accountNumber: String,
    val alias: String
)

//Beneficiarios (CREATE /beneficiaries)
data class BeneficiaryResponse(
    val id: String,
    val ownerId: String,
    val name: String,
    val lastName: String,
    val accountNumber: String,
    val alias: String
)

//Beneficiarios (DELETE /beneficiaries/{id})
data class DeleteResponse(
    val id: String,
    val deleted: Boolean
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

// Crear una transferencia (POST /transaction)
data class TransactionRequest(
    val toBeneficiaryId: String,  // id del beneficiario destino
    val amount: Long,             // En centavos: 5000 = $50.00
    val description: String? = null
)

// Fondear la cuenta propia (PUT /account)
data class FundRequest(
    val amount: Long              // En centavos
)