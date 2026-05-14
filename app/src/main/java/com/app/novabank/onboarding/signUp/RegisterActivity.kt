package com.app.novabank.onboarding.signUp

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.novabank.R
import com.app.novabank.databinding.ActivityRegisterBinding
import com.app.novabank.onboarding.personal.DatosPersonalesActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spannable = SpannableString("¿Ya tienes cuenta? Iniciar sesión")
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
            19, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvGoToLogin.text = spannable

        binding.btnBack.setOnClickListener { finish() }
        binding.tvGoToLogin.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            val email   = binding.etEmail.editText?.text.toString().trim()
            val pass    = binding.etPassword.editText?.text.toString()
            val confirm = binding.etConfirmPassword.editText?.text.toString()

            binding.etEmail.error           = null
            binding.etPassword.error        = null
            binding.etConfirmPassword.error = null

            var valid = true
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email con formato inválido"; valid = false
            }
            if (pass.length < 8) {
                binding.etPassword.error = "Mínimo 8 caracteres"; valid = false
            }
            if (pass != confirm) {
                binding.etConfirmPassword.error = "Las contraseñas no coinciden"; valid = false
            }
            if (valid) {
                startActivity(Intent(this, DatosPersonalesActivity::class.java))
            }
        }
    }
}