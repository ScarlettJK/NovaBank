package com.app.novabank.data.repository

import com.app.novabank.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    fun getUsuarioActual(): FirebaseUser? = auth.currentUser
    fun cerrarSesion() = auth.signOut()

    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) { Resource.Error(traducir(e.message ?: "")) }
    }

    suspend fun registrar(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) { Resource.Error(traducir(e.message ?: "")) }
    }

    private fun traducir(msg: String) = when {
        msg.contains("already in use")    -> "Este correo ya está registrado"
        msg.contains("wrong password") || msg.contains("password is invalid") -> "Contraseña incorrecta"
        msg.contains("no user record")    -> "No existe cuenta con este correo"
        msg.contains("badly formatted")   -> "Formato de correo inválido"
        msg.contains("network")           -> "Sin conexión a Internet"
        msg.contains("too many requests") -> "Demasiados intentos. Espera un momento"
        else -> "Error: $msg"
    }
}