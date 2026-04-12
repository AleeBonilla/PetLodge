# Contrato de Datos de Mascotas - V1

Este documento fija el contrato de datos para el modulo de mascotas de la fase 1.
Sirve como base para las siguientes tareas de backend, migraciones y conexion con
la app Android.

## Fuente funcional

Los campos se definieron a partir de `Hotel de Mascotas.pdf`, especificamente del
apartado "Registro de mascotas".

## Campos acordados

### Respuesta

```json
{
  "id": 1,
  "owner_id": 10,
  "name": "Luna",
  "species": "dog",
  "breed": "French Poodle",
  "age_years": 4,
  "sex": "female",
  "size": "small",
  "photo_url": "https://example.com/luna.jpg",
  "vaccinated": true,
  "vaccination_notes": "Vacunas al dia, refuerzo enero 2026",
  "has_medical_conditions": true,
  "medical_conditions_notes": "Alergia alimentaria",
  "veterinarian_name": "Dr. Carlos Mendez",
  "veterinarian_phone": "8888-7777",
  "care_notes": "Necesita alimento hipoalergenico y es nerviosa con ruidos fuertes",
  "is_deleted": false,
  "created_at": "2026-04-11T12:00:00Z",
  "updated_at": "2026-04-11T12:00:00Z"
}
```

### Creacion y edicion

```json
{
  "name": "Luna",
  "species": "dog",
  "breed": "French Poodle",
  "age_years": 4,
  "sex": "female",
  "size": "small",
  "photo_url": "https://example.com/luna.jpg",
  "vaccinated": true,
  "vaccination_notes": "Vacunas al dia, refuerzo enero 2026",
  "has_medical_conditions": true,
  "medical_conditions_notes": "Alergia alimentaria",
  "veterinarian_name": "Dr. Carlos Mendez",
  "veterinarian_phone": "8888-7777",
  "care_notes": "Necesita alimento hipoalergenico y es nerviosa con ruidos fuertes"
}
```

## Reglas del contrato

- `name` es requerido.
- `species` es requerido.
- `breed`, `age_years`, `sex`, `size`, `photo_url`, `vaccinated`,
  `vaccination_notes`, `has_medical_conditions`, `medical_conditions_notes`,
  `veterinarian_name`, `veterinarian_phone` y `care_notes` son opcionales.
- `photo_url` se mantiene como URL o texto en V1. La subida real de imagen queda
  para una etapa posterior.
- `age_years` se maneja como entero.
- `vaccinated` y `has_medical_conditions` se manejan como booleanos.
- Si `vaccinated = true`, `vaccination_notes` debe venir informado.
- Si `has_medical_conditions = true`, `medical_conditions_notes` debe venir
  informado.
- `care_notes` concentra alimentacion, comportamiento y cuidados especiales en un
  solo campo.

## Valores controlados

- `species`: `dog`, `cat`, `bird`, `rabbit`, `hamster`, `reptile`, `other`
- `sex`: `male`, `female`
- `size`: `small`, `medium`, `large`

## Mapeo con la UI Android actual

- `etNombre` -> `name`
- `etTipo` -> `species`
- `etRaza` -> `breed`
- `etEdad` -> `age_years`
- `etSexo` -> `sex`
- `etTamano` -> `size`
- `btnSeleccionarImagen` -> `photo_url` en V1
- `etVacunacionEstado` -> `vaccinated`
- `etVacunacionDetalles` -> `vaccination_notes`
- `etTelefonoVet` -> `veterinarian_phone`
- `etCuidadosEspeciales` y `etAlimentacion` deben consolidarse en `care_notes`
- Falta agregar un campo para `veterinarian_name`
- `etVacunas` no forma parte del contrato final y deberia eliminarse o
  reinterpretarse en la UI

## Decision para siguientes pasos

En la siguiente iteracion se debe alinear:

- `backend/app/models/pet.py`
- `backend/app/schemas/pet_schema.py`
- migracion de la tabla `pets`
- layouts y Activities de mascotas en Android
