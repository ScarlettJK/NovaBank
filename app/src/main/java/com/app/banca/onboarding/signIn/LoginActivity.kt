package com.app.banca.onboarding.signIn

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
import com.app.banca.R
import com.app.banca.data.repository.AuthRepository
import com.app.banca.databinding.ActivityLoginBinding
import com.app.banca.home.HomeActivity
import com.app.banca.onboarding.signUp.RegisterActivity
import com.app.banca.utils.Resource
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sesión activa → ir directo a Home
        if (auth.getUsuarioActual() != null) { irAHome(); return }

        val sp = SpannableString("¿No tienes cuenta? Regístrate")
        sp.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)), 19, sp.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvGoToRegister.text = sp

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.editText?.text.toString().trim()
            val pass  = binding.etPassword.editText?.text.toString()
            binding.etEmail.error = null; binding.etPassword.error = null
            var ok = true
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.etEmail.error = "Email inválido"; ok = false }
            if (pass.length < 8) { binding.etPassword.error = "Mínimo 8 caracteres"; ok = false }
            if (ok) doLogin(email, pass)
        }

        binding.tvGoToRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }

    private fun doLogin(email: String, pass: String) {
        binding.btnLogin.isEnabled = false
        lifecycleScope.launch {
            when (val r = auth.login(email, pass)) {
                is Resource.Success -> irAHome()
                is Resource.Error   -> { binding.btnLogin.isEnabled = true; Toast.makeText(this@LoginActivity, r.message, Toast.LENGTH_LONG).show() }
                else -> {}
            }
        }
    }

    private fun irAHome() = startActivity(Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
}