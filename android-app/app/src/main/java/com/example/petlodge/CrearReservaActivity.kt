package com.example.petlodge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.databinding.ActivityCrearReservaBinding

class CrearReservaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrearReservaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearReservaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToReservas.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            finish()
        }
    }
}
