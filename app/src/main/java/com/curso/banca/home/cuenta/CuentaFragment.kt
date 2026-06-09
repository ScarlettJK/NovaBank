package com.curso.banca.home.cuenta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.curso.banca.databinding.FragmentCuentaBinding
import com.curso.banca.onboarding.signIn.LoginActivity
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class CuentaFragment : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CuentaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cargarDatosUsuario()

        lifecycleScope.launch {
            viewModel.estado.collect { estado ->
                when (estado) {
                    is CuentaState.Cargando          -> mostrarCargando()
                    is CuentaState.Exito             -> mostrarDatos(estado.usuario)
                    is CuentaState.Error             -> mostrarError(estado.mensaje)
                    is CuentaState.UsuarioNoEncontrado -> mostrarSinDatos()
                }
            }
        }

        binding.btnCerrarSesion.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas salir?")
                .setPositiveButton("Sí, salir") { _, _ -> cerrarSesion() }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun mostrarCargando() {
        binding.progressBarCarga.visibility = View.VISIBLE
        binding.cardPersonalInfo.visibility  = View.GONE
        binding.tvUserName.text  = "Cargando..."
        binding.tvUserEmail.text = ""
    }

    private fun mostrarDatos(usuario: com.curso.banca.data.repository.Usuario) {

        binding.progressBarCarga.visibility = View.GONE
        binding.cardPersonalInfo.visibility = View.VISIBLE

        binding.tvUserName.text = usuario.fullName

        binding.tvUserEmail.text =
            FirebaseAuth.getInstance().currentUser?.email ?: ""

        binding.tvNombreCompleto.text =
            usuario.fullName.ifEmpty { "No registrado" }

        binding.tvCelularValue.text =
            usuario.phone.ifEmpty { "No registrado" }

        binding.tvFechaNacValue.text =
            usuario.birthdate.ifEmpty { "No registrada" }
    }

    private fun mostrarSinDatos() {
        binding.progressBarCarga.visibility = View.GONE
        binding.cardPersonalInfo.visibility  = View.GONE
        binding.tvUserName.text  = "Perfil incompleto"
        binding.tvUserEmail.text = "Completa tu registro"
    }

    private fun mostrarError(mensaje: String) {
        binding.progressBarCarga.visibility = View.GONE
        binding.cardPersonalInfo.visibility  = View.GONE
        binding.tvUserName.text  = "Error al cargar"
        binding.tvUserEmail.text = "Intenta más tarde"

        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(mensaje)
            .setPositiveButton("Reintentar") { _, _ -> viewModel.cargarDatosUsuario() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        viewModel.cerrarSesion()
        startActivity(
            Intent(requireContext(), LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}