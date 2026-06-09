package com.curso.banca.home.transferencia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.curso.banca.databinding.ActivityTransferirBinding

class TransferirActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTransferirBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}