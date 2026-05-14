package com.app.novabank.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.app.novabank.R
import com.app.novabank.data.repository.AuthRepository
import com.app.novabank.databinding.ActivityHomeBinding
import com.app.novabank.onboarding.signIn.LoginActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sin sesión → regresar a Login
        if (AuthRepository().getUsuarioActual() == null) {
            startActivity(Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nav = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        binding.bottomNav.setupWithNavController(nav)
    }
}