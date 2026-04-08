package com.example.petlodge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.databinding.ActivityRegistrarMascotaBinding

class RegistrarMascotaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarMascotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToMisMascotas.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            finish()
        }
    }
}
