package com.curso.banca.home.beneficiarios

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.curso.banca.R
import com.curso.banca.network.BeneficiaryRequest
import com.curso.banca.network.RetrofitClient
import com.curso.banca.network.apiCall
import com.curso.banca.network.apiCallSinCuerpo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddBeneficiarioActivity : AppCompatActivity() {

    private var beneficiaryId: String? = null
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_beneficiario)

        beneficiaryId = intent.getStringExtra("ID")
        editMode = beneficiaryId != null

        val nombre   = findViewById<TextInputEditText>(R.id.etNombre)
        val apellido = findViewById<TextInputEditText>(R.id.etApellido)
        val cuenta   = findViewById<TextInputEditText>(R.id.etCuenta)
        val alias    = findViewById<TextInputEditText>(R.id.etAlias)
        val btnGuardar  = findViewById<MaterialButton>(R.id.btnGuardar)
        val btnEliminar = findViewById<MaterialButton>(R.id.btnEliminar)

        btnEliminar.visibility = if (editMode) View.VISIBLE else View.GONE

        if (editMode) {
            // Precargar TODOS los datos del beneficiario a editar
            nombre.setText(intent.getStringExtra("NOMBRE"))
            apellido.setText(intent.getStringExtra("APELLIDO"))
            cuenta.setText(intent.getStringExtra("CUENTA"))
            alias.setText(intent.getStringExtra("ALIAS"))

            findViewById<TextView>(R.id.tvTitle).text = "Editar beneficiario"
            findViewById<TextView>(R.id.tvText).text =
                "Modifica los datos del beneficiario"
            btnGuardar.text = "Guardar cambios"
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar beneficiario")
                .setMessage("¿Seguro que deseas eliminar este beneficiario?")
                .setPositiveButton("Eliminar") { _, _ -> eliminar(btnEliminar) }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnGuardar.setOnClickListener {
            if (nombre.text.isNullOrBlank() ||
                apellido.text.isNullOrBlank() ||
                cuenta.text.isNullOrBlank() ||
                alias.text.isNullOrBlank()
            ) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            guardar(
                btnGuardar,
                BeneficiaryRequest(
                    name = nombre.text.toString().trim(),
                    lastName = apellido.text.toString().trim(),
                    accountNumber = cuenta.text.toString().trim(),
                    alias = alias.text.toString().trim()
                )
            )
        }
    }

    private fun guardar(btn: MaterialButton, request: BeneficiaryRequest) {
        btn.isEnabled = false
        lifecycleScope.launch {
            apiCall {
                if (editMode)
                    RetrofitClient.api.updateBeneficiary(beneficiaryId!!, request)
                else
                    RetrofitClient.api.createBeneficiary(request)
            }
                .onSuccess {
                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        if (editMode) "Beneficiario actualizado" else "Beneficiario agregado",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .onFailure { e ->
                    btn.isEnabled = true
                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        "Error: ${e.message}", Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun eliminar(btn: MaterialButton) {
        btn.isEnabled = false
        lifecycleScope.launch {
            apiCallSinCuerpo { RetrofitClient.api.deleteBeneficiary(beneficiaryId!!) }
                .onSuccess {
                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        "Beneficiario eliminado", Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .onFailure { e ->
                    btn.isEnabled = true
                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        "No se pudo eliminar: ${e.message}", Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}