package com.example.petlodge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.databinding.ActivityEditarMascotaBinding

class EditarMascotaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarMascotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToDetalle.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            // Guardar cambios y volver a detalle
            finish()
        }
    }
}
