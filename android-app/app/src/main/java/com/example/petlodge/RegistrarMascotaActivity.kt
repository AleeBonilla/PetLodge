package com.example.petlodge

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petlodge.data.ApiClient
import com.example.petlodge.data.dto.ApiResponse
import com.example.petlodge.data.dto.PetRequest
import com.example.petlodge.data.dto.PetResponse
import com.example.petlodge.databinding.ActivityRegistrarMascotaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarMascotaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarMascotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToMisMascotas.setOnClickListener {
            finish()
        }

        binding.btnSeleccionarImagen.setOnClickListener {
            Toast.makeText(
                this,
                "La carga de imagen aún no está implementada.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnSave.setOnClickListener {
            submitPet()
        }
    }

    private fun submitPet() {
        val name = binding.etNombre.text.toString().trim()
        val species = mapSpecies(binding.etTipo.text.toString())
        val breed = binding.etRaza.text.toString().trim().ifBlank { null }
        val ageYears = binding.etEdad.text.toString().trim().toIntOrNull()
        val sex = mapSex(binding.etSexo.text.toString())
        val size = mapSize(binding.etTamano.text.toString())
        val vaccinated = parseBooleanField(binding.etVacunacionEstado.text.toString())
        val vaccinationNotes = binding.etVacunacionDetalles.text.toString().trim().ifBlank { null }
        val hasMedicalConditions = parseBooleanField(
            binding.etCondicionesMedicasEstado.text.toString()
        )
        val medicalConditionsNotes =
            binding.etCondicionesMedicasDetalles.text.toString().trim().ifBlank { null }
        val veterinarianName = binding.etNombreVet.text.toString().trim().ifBlank { null }
        val veterinarianPhone = binding.etTelefonoVet.text.toString().trim().ifBlank { null }
        val careNotes = buildCareNotes(
            binding.etCuidadosEspeciales.text.toString().trim(),
            binding.etAlimentacion.text.toString().trim()
        )

        if (!validateFields(
                name = name,
                rawSpecies = binding.etTipo.text.toString(),
                species = species,
                rawSex = binding.etSexo.text.toString(),
                sex = sex,
                rawSize = binding.etTamano.text.toString(),
                size = size,
                rawVaccinated = binding.etVacunacionEstado.text.toString(),
                vaccinated = vaccinated,
                vaccinationNotes = vaccinationNotes,
                rawHasMedicalConditions = binding.etCondicionesMedicasEstado.text.toString(),
                hasMedicalConditions = hasMedicalConditions,
                medicalConditionsNotes = medicalConditionsNotes,
                rawAge = binding.etEdad.text.toString()
            )
        ) {
            return
        }

        val request = PetRequest(
            name = name,
            species = species,
            breed = breed,
            ageYears = ageYears,
            sex = sex,
            size = size,
            vaccinated = vaccinated,
            vaccinationNotes = vaccinationNotes,
            hasMedicalConditions = hasMedicalConditions,
            medicalConditionsNotes = medicalConditionsNotes,
            veterinarianName = veterinarianName,
            veterinarianPhone = veterinarianPhone,
            careNotes = careNotes
        )

        setLoading(true)
        ApiClient.service.createPet(request)
            .enqueue(object : Callback<ApiResponse<PetResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<PetResponse>>,
                    response: Response<ApiResponse<PetResponse>>
                ) {
                    setLoading(false)
                    val body = response.body()

                    if (response.isSuccessful && body?.success == true) {
                        Toast.makeText(
                            this@RegistrarMascotaActivity,
                            body.message ?: "Mascota registrada exitosamente.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegistrarMascotaActivity,
                            body?.error ?: "No se pudo registrar la mascota.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<PetResponse>>,
                    throwable: Throwable
                ) {
                    setLoading(false)
                    Toast.makeText(
                        this@RegistrarMascotaActivity,
                        "No se pudo conectar con el servidor.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun validateFields(
        name: String,
        rawSpecies: String,
        species: String?,
        rawSex: String,
        sex: String?,
        rawSize: String,
        size: String?,
        rawVaccinated: String,
        vaccinated: Boolean?,
        vaccinationNotes: String?,
        rawHasMedicalConditions: String,
        hasMedicalConditions: Boolean?,
        medicalConditionsNotes: String?,
        rawAge: String
    ): Boolean {
        if (name.isBlank()) {
            binding.etNombre.error = "Ingresa el nombre."
            return false
        }

        if (rawSpecies.isBlank()) {
            binding.etTipo.error = "Ingresa el tipo de mascota."
            return false
        }

        if (species == null) {
            binding.etTipo.error = "Usa perro, gato, ave, conejo, hámster, reptil u otro."
            return false
        }

        if (rawAge.isNotBlank() && rawAge.toIntOrNull() == null) {
            binding.etEdad.error = "Ingresa una edad válida."
            return false
        }

        if (rawSex.isNotBlank() && sex == null) {
            binding.etSexo.error = "Usa macho o hembra."
            return false
        }

        if (rawSize.isNotBlank() && size == null) {
            binding.etTamano.error = "Usa pequeño, mediano o grande."
            return false
        }

        if (rawVaccinated.isNotBlank() && vaccinated == null) {
            binding.etVacunacionEstado.error = "Usa sí o no."
            return false
        }

        if (vaccinated == true && vaccinationNotes.isNullOrBlank()) {
            binding.etVacunacionDetalles.error = "Describe las vacunas."
            return false
        }

        if (rawHasMedicalConditions.isNotBlank() && hasMedicalConditions == null) {
            binding.etCondicionesMedicasEstado.error = "Usa sí o no."
            return false
        }

        if (hasMedicalConditions == true && medicalConditionsNotes.isNullOrBlank()) {
            binding.etCondicionesMedicasDetalles.error = "Describe las condiciones médicas."
            return false
        }

        return true
    }

    private fun buildCareNotes(care: String, feeding: String): String? {
        val sections = mutableListOf<String>()
        if (care.isNotBlank()) {
            sections.add("Cuidados especiales: $care")
        }
        if (feeding.isNotBlank()) {
            sections.add("Alimentación y comportamiento: $feeding")
        }
        return sections.takeIf { it.isNotEmpty() }?.joinToString("\n\n")
    }

    private fun mapSpecies(value: String): String? = when (value.trim().lowercase()) {
        "" -> null
        "perro", "dog" -> "dog"
        "gato", "cat" -> "cat"
        "ave", "pajaro", "pájaro", "pajaro/a", "pájaro/a", "bird" -> "bird"
        "conejo", "rabbit" -> "rabbit"
        "hamster", "hámster" -> "hamster"
        "reptil", "reptile" -> "reptile"
        "otro", "other" -> "other"
        else -> null
    }

    private fun mapSex(value: String): String? = when (value.trim().lowercase()) {
        "" -> null
        "macho", "male" -> "male"
        "hembra", "female" -> "female"
        else -> null
    }

    private fun mapSize(value: String): String? = when (value.trim().lowercase()) {
        "" -> null
        "pequeno", "pequeño", "small" -> "small"
        "mediano", "medium" -> "medium"
        "grande", "large" -> "large"
        else -> null
    }

    private fun parseBooleanField(value: String): Boolean? = when (value.trim().lowercase()) {
        "" -> null
        "si", "sí", "s", "yes", "true" -> true
        "no", "n", "false" -> false
        else -> null
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnSave.isEnabled = !isLoading
        binding.btnBackToMisMascotas.isEnabled = !isLoading
        binding.btnSeleccionarImagen.isEnabled = !isLoading
        binding.btnSave.text = if (isLoading) "Guardando..." else "Guardar Mascota"
    }
}
