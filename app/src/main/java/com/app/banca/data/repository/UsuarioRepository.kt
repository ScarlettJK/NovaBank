package com.app.banca.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val celular: String = "",
    val fechaNacimiento: String = "",
    val rol: String = "cliente",
    val estado: String = "activo",
    val fechaCreacion: Long = 0L
)

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance().collection("usuarios")

    suspend fun guardar(usuario: Usuario) {
        db.document(usuario.uid).set(usuario).await()
    }

    suspend fun obtener(uid: String): Usuario? {
        return try {
            db.document(uid).get().await().toObject(Usuario::class.java)
        } catch (e: Exception) { null }
    }
}