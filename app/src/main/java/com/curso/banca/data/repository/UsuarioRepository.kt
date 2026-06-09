package com.curso.banca.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Usuario(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val phone: String = "",
    val birthdate: String = ""
)

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance()
        .collection("users")

    suspend fun guardar(usuario: Usuario) {
        db.document(usuario.uid).set(usuario).await()
    }

    suspend fun obtener(uid: String): Usuario? {
        return try {
            db.document(uid).get().await().toObject(Usuario::class.java)
        } catch (e: Exception) { null }
    }
}