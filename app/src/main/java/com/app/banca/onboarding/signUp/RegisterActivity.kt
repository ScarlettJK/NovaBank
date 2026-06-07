package com.curso.banca.onboarding.signUp

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.curso.banca.R
import com.curso.banca.data.repository.AuthRepository
import com.curso.banca.databinding.ActivityRegisterBinding
import com.curso.banca.onboarding.personal.DatosPersonalesActivity
import com.curso.banca.utils.Resource
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val auth = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sp = SpannableString("¿Ya tienes cuenta? Iniciar sesión")
        sp.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)), 19, sp.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvGoToLogin.text = sp

        binding.btnBack.setOnClickListener { finish() }
        binding.tvGoToLogin.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            val email   = binding.etEmail.editText?.text.toString().trim()
            val pass    = binding.etPassword.editText?.text.toString()
            val confirm = binding.etConfirmPassword.editText?.text.toString()
            binding.etEmail.error = null; binding.etPassword.error = null; binding.etConfirmPassword.error = null
            var ok = true
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.etEmail.error = "Email inválido"; ok = false }
            if (pass.length < 8) { binding.etPassword.error = "Mínimo 8 caracteres"; ok = false }
            if (pass != confirm) { binding.etConfirmPassword.error = "Las contraseñas no coinciden"; ok = false }
            if (ok) doRegistro(email, pass)
        }
    }

    private fun doRegistro(email: String, pass: String) {
        binding.btnRegister.isEnabled = false
        lifecycleScope.launch {
            when (val r = auth.registrar(email, pass)) {
                is Resource.Success -> {
                    startActivity(Intent(this@RegisterActivity, DatosPersonalesActivity::class.java).apply {
                        putExtra("uid", r.data.uid)
                        putExtra("email", email)
                    })
                    finish()
                }
                is Resource.Error -> { binding.btnRegister.isEnabled = true; Toast.makeText(this@RegisterActivity, r.message, Toast.LENGTH_LONG).show() }
                else -> {}
            }
        }
    }
}