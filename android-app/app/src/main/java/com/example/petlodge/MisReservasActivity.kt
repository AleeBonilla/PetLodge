package com.example.petlodge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.databinding.ActivityMisReservasBinding

class MisReservasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMisReservasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToMain.setOnClickListener {
            finish()
        }

        binding.btnCrearReserva.setOnClickListener {
            startActivity(Intent(this, CrearReservaActivity::class.java))
        }
    }
}
