package com.curso.banca.home.beneficiarios

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.curso.banca.R
import com.curso.banca.network.BeneficiaryRequest
import com.curso.banca.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddBeneficiarioActivity : AppCompatActivity() {

    private var beneficiaryId: String? = null
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_beneficiario)

        beneficiaryId =
            intent.getStringExtra("ID")

        editMode =
            beneficiaryId != null

        val nombre   = findViewById<TextInputEditText>(R.id.etNombre)
        val apellido = findViewById<TextInputEditText>(R.id.etApellido)
        val cuenta   = findViewById<TextInputEditText>(R.id.etCuenta)
        val alias    = findViewById<TextInputEditText>(R.id.etAlias)

        val btnEliminar =
            findViewById<com.google.android.material.button.MaterialButton>(
                R.id.btnEliminar
            )

        btnEliminar.visibility =
            if (editMode)
                android.view.View.VISIBLE
            else
                android.view.View.GONE

        btnEliminar.setOnClickListener {

            lifecycleScope.launch {

                try {

                    RetrofitClient.api.deleteBeneficiary(
                        beneficiaryId!!
                    )

                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        "Beneficiario eliminado",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } catch (e: Exception) {

                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        if (editMode) {

            nombre.setText(
                intent.getStringExtra("NOMBRE")
            )

            alias.setText(
                intent.getStringExtra("ALIAS")
            )
        }

        findViewById<android.widget.ImageView>(R.id.btnBack)
            .setOnClickListener {
                finish()
            }

        findViewById<com.google.android.material.button.MaterialButton>(
            R.id.btnGuardar
        ).setOnClickListener {

            if (
                nombre.text.isNullOrBlank() ||
                apellido.text.isNullOrBlank() ||
                cuenta.text.isNullOrBlank() ||
                alias.text.isNullOrBlank()
            ) {
                Toast.makeText(
                    this,
                    "Llena todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {

                    val request = BeneficiaryRequest(
                        name = nombre.text.toString(),
                        lastName = apellido.text.toString(),
                        accountNumber = cuenta.text.toString(),
                        alias = alias.text.toString()
                    )

                    if (editMode) {

                        RetrofitClient.api.updateBeneficiary(
                            beneficiaryId!!,
                            request
                        )

                    } else {

                        RetrofitClient.api.createBeneficiary(
                            request
                        )
                    }

                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        if (editMode)
                            "Beneficiario actualizado"
                        else
                            "Beneficiario agregado",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } catch (e: Exception) {

                    Toast.makeText(
                        this@AddBeneficiarioActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}