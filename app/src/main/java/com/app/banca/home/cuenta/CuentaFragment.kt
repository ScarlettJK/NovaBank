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
import com.curso.banca.R
import com.curso.banca.databinding.FragmentCuentaBinding
import com.curso.banca.onboarding.signIn.LoginActivity
import kotlinx.coroutines.launch

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

        // Cargar datos del usuario al crear la vista
        viewModel.cargarDatosUsuario()

        // Observar cambios en el estado
        lifecycleScope.launch {
            viewModel.estado.collect { estado ->
                when (estado) {
                    is CuentaState.Cargando -> mostrarCargando()
                    is CuentaState.Exito -> mostrarDatos(estado.usuario)
                    is CuentaState.Error -> mostrarError(estado.mensaje)
                    is CuentaState.UsuarioNoEncontrado -> mostrarSinDatos()
                }
            }
        }

        // Botón de cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que deseas salir?")
                .setPositiveButton("Sí, salir") { _, _ ->
                    cerrarSesion()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun mostrarCargando() {
        binding.apply {
            progressBarCarga.visibility = View.VISIBLE
            cardPersonalInfo.visibility = View.GONE
            tvUserName.text = "Cargando..."
            tvUserEmail.text = ""
        }
    }

    private fun mostrarDatos(usuario: com.curso.banca.data.repository.Usuario) {
        binding.apply {
            progressBarCarga.visibility = View.GONE
            cardPersonalInfo.visibility = View.VISIBLE

            // Datos en header
            tvUserName.text = "${usuario.nombre} ${usuario.apellidos}"
            tvUserEmail.text = usuario.email

            // Datos en card de información personal
            binding.apply {
                // Nombre completo
                val tvNombreCompleto = view?.findViewById<android.widget.TextView>(
                    resources.getIdentifier("tvNombreCompleto", "id", requireContext().packageName)
                )
                tvNombreCompleto?.text = "${usuario.nombre} ${usuario.apellidos}"

                // Celular
                val tvCelularValue = view?.findViewById<android.widget.TextView>(
                    resources.getIdentifier("tvCelularValue", "id", requireContext().packageName)
                )
                tvCelularValue?.text = usuario.celular

                // Fecha de nacimiento
                val tvFechaNacValue = view?.findViewById<android.widget.TextView>(
                    resources.getIdentifier("tvFechaNacValue", "id", requireContext().packageName)
                )
                tvFechaNacValue?.text = usuario.fechaNacimiento
            }
        }
    }

    private fun mostrarSinDatos() {
        binding.apply {
            progressBarCarga.visibility = View.GONE
            cardPersonalInfo.visibility = View.GONE
            tvUserName.text = "Datos no encontrados"
            tvUserEmail.text = "Contacta al soporte"

            AlertDialog.Builder(requireContext())
                .setTitle("Información incompleta")
                .setMessage("Tu perfil no tiene datos completos. Contacta al administrador.")
                .setPositiveButton("Entendido") { _, _ -> }
                .show()
        }
    }

    private fun mostrarError(mensaje: String) {
        binding.apply {
            progressBarCarga.visibility = View.GONE
            cardPersonalInfo.visibility = View.GONE
            tvUserName.text = "Error"
            tvUserEmail.text = "Intenta más tarde"

            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("Reintentar") { _, _ ->
                    viewModel.cargarDatosUsuario()
                }
                .show()
        }
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