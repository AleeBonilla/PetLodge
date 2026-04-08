package com.example.petlodge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.databinding.ActivityDetalleMascotaBinding

class DetalleMascotaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleMascotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToMisMascotas.setOnClickListener {
            finish()
        }

        binding.btnEditarMascota.setOnClickListener {
            startActivity(Intent(this, EditarMascotaActivity::class.java))
        }
    }
}
