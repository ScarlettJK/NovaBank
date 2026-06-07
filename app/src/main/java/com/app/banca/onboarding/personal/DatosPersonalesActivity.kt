package com.app.banca.onboarding.personal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.banca.data.repository.Usuario
import com.app.banca.data.repository.UsuarioRepository
import com.app.banca.databinding.ActivityDatosPersonalesBinding
import com.app.banca.home.HomeActivity
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DatosPersonalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosPersonalesBinding
    private val usuarioRepo = UsuarioRepository()
    private var uid = ""; private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosPersonalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid   = intent.getStringExtra("uid") ?: ""
        email = intent.getStringExtra("email") ?: ""
        if (uid.isEmpty()) { finish(); return }

        binding.btnBack.setOnClickListener { finish() }
        binding.etFechaInput.setOnClickListener { datePicker() }
        binding.etFechaNacimiento.setEndIconOnClickListener { datePicker() }

        binding.btnContinuar.setOnClickListener {
            val nombre    = binding.etNombre.editText?.text.toString().trim()
            val apellidos = binding.etApellidos.editText?.text.toString().trim()
            val celular   = binding.etCelular.editText?.text.toString().trim()
            val fecha     = binding.etFechaInput.text.toString().trim()
            binding.etNombre.error = null; binding.etApellidos.error = null
            binding.etCelular.error = null; binding.etFechaNacimiento.error = null
            var ok = true
            if (nombre.isEmpty())    { binding.etNombre.error    = "Campo requerido"; ok = false }
            if (apellidos.isEmpty()) { binding.etApellidos.error = "Campo requerido"; ok = false }
            if (celular.isEmpty())   { binding.etCelular.error   = "Campo requerido"; ok = false }
            if (fecha.isEmpty())     { binding.etFechaNacimiento.error = "Selecciona fecha"; ok = false }
            if (ok) guardar(nombre, apellidos, celular, fecha)
        }
    }

    private fun guardar(nombre: String, apellidos: String, celular: String, fecha: String) {
        binding.btnContinuar.isEnabled = false
        lifecycleScope.launch {
            try {
                usuarioRepo.guardar(Usuario(uid = uid, nombre = nombre, apellidos = apellidos,
                    email = email, celular = celular, fechaNacimiento = fecha,
                    fechaCreacion = System.currentTimeMillis()))
                startActivity(Intent(this@DatosPersonalesActivity, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (e: Exception) {
                binding.btnContinuar.isEnabled = true
                Toast.makeText(this@DatosPersonalesActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun datePicker() {
        MaterialDatePicker.Builder.datePicker().setTitleText("Fecha de nacimiento").build().also {
            it.show(supportFragmentManager, "dp")
            it.addOnPositiveButtonClickListener { ms ->
                binding.etFechaInput.setText(SimpleDateFormat("dd / MM / yyyy", Locale.getDefault()).format(Date(ms)))
            }
        }
    }
}