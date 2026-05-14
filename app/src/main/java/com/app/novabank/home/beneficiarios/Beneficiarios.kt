package com.app.novabank.home.beneficiarios

data class Beneficiario(
    val id: String = "",
    val nombre: String = "",
    val banco: String = "",
    val cuentaOculta: String = "",
    val uidPropietario: String = ""
)