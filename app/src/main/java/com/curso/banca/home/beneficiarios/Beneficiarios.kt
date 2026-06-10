package com.curso.banca.home.beneficiarios

data class Beneficiario(
    val id: String = "",
    val nombre: String = "",      // solo nombre(s)
    val apellido: String = "",
    val banco: String = "",       // alias
    val cuenta: String = "",      // número de cuenta completo
    val uidPropietario: String = ""
) {
    /** Nombre completo para mostrar en listas. */
    val nombreCompleto: String
        get() = "$nombre $apellido".trim()

    /** Cuenta enmascarada para mostrar en listas. */
    val cuentaOculta: String
        get() = "****${cuenta.takeLast(4)}"
}