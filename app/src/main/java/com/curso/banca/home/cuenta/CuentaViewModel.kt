package com.curso.banca.home.cuenta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.banca.data.repository.Usuario
import com.curso.banca.data.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CuentaState {
    data object Cargando : CuentaState()
    data class Exito(val usuario: Usuario) : CuentaState()
    data class Error(val mensaje: String) : CuentaState()
    data object UsuarioNoEncontrado : CuentaState()
}

class CuentaViewModel(
    private val usuarioRepository: UsuarioRepository = UsuarioRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _estado = MutableStateFlow<CuentaState>(CuentaState.Cargando)
    val estado: StateFlow<CuentaState> = _estado.asStateFlow()

    fun cargarDatosUsuario() {
        viewModelScope.launch {
            _estado.value = CuentaState.Cargando
            try {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    _estado.value = CuentaState.Error("Usuario no autenticado")
                    return@launch
                }

                val usuario = usuarioRepository.obtener(uid)
                if (usuario == null || usuario.uid.isEmpty()) {
                    _estado.value = CuentaState.UsuarioNoEncontrado
                } else {
                    _estado.value = CuentaState.Exito(usuario)
                }
            } catch (e: Exception) {
                _estado.value = CuentaState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun cerrarSesion() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            _estado.value = CuentaState.Error("Error al cerrar sesión: ${e.message}")
        }
    }
}