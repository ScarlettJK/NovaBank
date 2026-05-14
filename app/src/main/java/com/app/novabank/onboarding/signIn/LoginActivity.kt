package com.app.novabank.onboarding.signIn

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.novabank.R
import com.app.novabank.onboarding.signUp.RegisterActivity
import com.app.novabank.databinding.ActivityLoginBinding
import com.app.novabank.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Texto enriquecido en tvGoToRegister
        val spannable = SpannableString("¿No tienes cuenta? Regístrate")
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
            19, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvGoToRegister.text = spannable

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.editText?.text.toString().trim()
            val pass  = binding.etPassword.editText?.text.toString()

            // Limpiar errores previos
            binding.etEmail.error    = null
            binding.etPassword.error = null

            var valid = true
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email inválido"
                valid = false
            }
            if (pass.length < 8) {
                binding.etPassword.error = "Mínimo 8 caracteres"
                valid = false
            }
            if (valid) {
                // Navegar a Home limpiando el back stack
                val intent = Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}