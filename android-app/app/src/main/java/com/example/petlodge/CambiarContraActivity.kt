package com.example.petlodge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.databinding.ActivityCambiarContraBinding

class CambiarContraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCambiarContraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarContraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToProfile.setOnClickListener {
            finish()
        }
        
        binding.btnSave.setOnClickListener {
            finish()
        }
    }
}
