package com.app.novabank

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.app.novabank.databinding.ActivityDatosPersonalesBinding
import java.text.SimpleDateFormat
import java.util.*

class DatosPersonalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosPersonalesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosPersonalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // DatePicker con MaterialDatePicker
        binding.etFechaInput.setOnClickListener { mostrarDatePicker() }
        binding.etFechaNacimiento.setEndIconOnClickListener { mostrarDatePicker() }

        binding.btnContinuar.setOnClickListener {
            val nombre = binding.etNombre.editText?.text.toString().trim()
            val apellidos = binding.etApellidos.editText?.text.toString().trim()
            val celular = binding.etCelular.editText?.text.toString().trim()
            val fecha = binding.etFechaInput.text.toString().trim()

            var valid = true
            if (nombre.isEmpty()) { binding.etNombre.error = "Campo requerido"; valid = false }
            if (apellidos.isEmpty()) { binding.etApellidos.error = "Campo requerido"; valid = false }
            if (celular.isEmpty()) { binding.etCelular.error = "Campo requerido"; valid = false }
            if (fecha.isEmpty()) { binding.etFechaNacimiento.error = "Selecciona una fecha"; valid = false }

            if (valid) {

                // Limpiar back stack: el usuario no puede volver a registro
                val intent = Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private fun mostrarDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Fecha de nacimiento")
            .build()
        picker.show(supportFragmentManager, "datePicker")
        picker.addOnPositiveButtonClickListener { millis ->
            val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
            binding.etFechaInput.setText(sdf.format(Date(millis)))
        }
    }
}

