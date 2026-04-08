package com.example.petlodge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.databinding.ActivityMisMascotasBinding

class MisMascotasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMisMascotasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisMascotasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToMain.setOnClickListener {
            finish()
        }

        binding.btnDetalleMascota1.setOnClickListener {
            startActivity(Intent(this, DetalleMascotaActivity::class.java))
        }

        binding.btnDetalleMascota2.setOnClickListener {
            startActivity(Intent(this, DetalleMascotaActivity::class.java))
        }

        binding.btnRegistrarMascota.setOnClickListener {
            startActivity(Intent(this, RegistrarMascotaActivity::class.java))
        }
    }
}
