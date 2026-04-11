# PetLodge – Backend API (Fase 1: App Móvil Android)

## Contexto del proyecto

**PetLodge** es un sistema de gestión para un hotel de mascotas. El proyecto se divide en dos fases:

- **Fase 1 (este backend):** Soporte a la aplicación móvil Android nativa. Cubre registro de usuarios, registro de mascotas, reservas y notificaciones por correo.
- **Fase 2 (futura):** Plataforma web con gestión operativa completa.

Tu trabajo es construir el **backend REST API** con Flask que sirva a la app móvil. No construirás la app en sí.

---

## Tu responsabilidad

1. Diseñar e implementar el **modelo de datos** completo (detallado, escalable y profesional).
2. Implementar la **API REST** con Flask.
3. Gestionar la **autenticación y autorización** con JWT.
4. Implementar el **centro de notificaciones** por correo electrónico.
5. Documentar los endpoints con Swagger.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Framework | Flask |
| ORM | SQLAlchemy |
| Base de datos | PostgreSQL (producción) / SQLite (desarrollo) |
| Migraciones | Flask-Migrate (Alembic) |
| Autenticación | Flask-JWT-Extended |
| Validación | Marshmallow o Pydantic |
| Correo | Flask-Mail o SMTP directo |
| Documentación API | flask-smorest + Swagger UI |
| Contenedores | Docker + docker-compose (recomendado) |

---
****
## Modelo de datos

> **Tu responsabilidad:** Diseñar el modelo de datos completo desde cero. Debe ser **detallado, escalable y profesional**. Se espera buen criterio en: normalización, tipos de datos apropiados, integridad referencial, índices en columnas de búsqueda frecuente, campos de auditoría (`created_at`, `updated_at`), y soft delete donde tenga sentido. No te limites a lo mínimo; anticipá la Fase 2.

### Entidades del dominio

El sistema gestiona las siguientes entidades. Vos definís la estructura exacta de cada una:

**Usuarios** — Pueden ser dueños de mascotas o administradores del hotel. Los dueños se **registran** desde la app móvil. Los admins se crean vía seed. Ambos se autentican con email y contraseña. Un admin puede activar o desactivar cuentas de dueños.

**Mascotas** — Pertenecen a un dueño. Un dueño puede tener varias. Tienen información médica, de cuidado y una foto (por ahora se guarda solo la URL; la lógica de subida a nube se define después). Pueden ser eliminadas por su dueño, pero no si tienen reservas activas o futuras.

**Habitaciones** — Son los espacios físicos del hotel. Existen dos tipos: estándar y especial. Las habitaciones se crean vía seed; no hay un CRUD público para crearlas desde la app.

**Reservas** — Un dueño reserva una habitación para una mascota en un rango de fechas. El tipo de hospedaje (estándar o especial) determina si se pueden contratar servicios adicionales. Una habitación no puede tener dos reservas con fechas solapadas. Las reservas tienen un ciclo de vida con distintos estados.

**Servicios adicionales** — Solo disponibles en reservas de tipo especial. Son servicios contratados junto con la reserva (baño, paseo, alimentación especial, entre otros).

**Plantillas de notificación** — Creadas y editadas por admins. Cada plantilla corresponde a un evento del sistema y contiene asunto y cuerpo en HTML con variables interpolables (ej. nombre del usuario, nombre de la mascota, fechas).

**Notificaciones** — Historial de todos los correos enviados o intentados. Se registra siempre, haya fallado o no el envío.

### Eventos que disparan notificaciones

- Registro exitoso de un usuario
- Confirmación de una reserva
- Modificación de una reserva
- Inicio del hospedaje
- Finalización del hospedaje
- Aviso sobre el estado de una mascota (enviado manualmente por un admin)

---

## Funcionalidades y endpoints requeridos

### 1. Autenticación (`/api/v1/auth`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/register` | Registro de nuevo usuario dueño |
| POST | `/login` | Login, retorna JWT access + refresh token |
| POST | `/refresh` | Renovar access token |
| POST | `/logout` | Invalidar token (blacklist) |

**Reglas:**
- La contraseña debe hashearse con `bcrypt`.
- El JWT debe incluir `user_id` y `role` en el payload.
- Al registrar un usuario, disparar notificación de tipo `user_registered`.

---

### 2. Usuarios (`/api/v1/users`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/me` | owner | Ver perfil propio |
| PUT | `/me` | owner | Editar datos personales |
| GET | `/` | admin | Listar todos los usuarios |
| PATCH | `/<id>/toggle-status` | admin | Activar / desactivar cuenta |

---

### 3. Mascotas (`/api/v1/pets`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/` | owner | Registrar mascota |
| GET | `/` | owner | Listar mis mascotas |
| GET | `/<id>` | owner | Ver detalle de una mascota |
| PUT | `/<id>` | owner | Editar mascota |
| DELETE | `/<id>` | owner | Eliminar mascota (soft delete) |

**Reglas:**
- Un dueño solo puede acceder a sus propias mascotas.
- No permitir eliminar una mascota con reservas activas o futuras.

---

### 4. Reservas (`/api/v1/reservations`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/` | owner | Crear reserva |
| GET | `/` | owner | Ver historial (activas + finalizadas) |
| GET | `/<id>` | owner | Detalle de reserva |
| DELETE | `/<id>` | owner | Cancelar reserva |

**Reglas:**
- Validar disponibilidad de habitación en el rango de fechas solicitado (sin solapamientos).
- Los servicios adicionales solo aplican si `lodging_type = 'special'`.
- Al crear una reserva, disparar notificación `reservation_confirmed`.
- No permitir reservas con fecha de ingreso en el pasado.

---

### 5. Notificaciones (`/api/v1/notifications`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/` | owner | Ver historial de mis notificaciones |
| GET | `/templates` | admin | Ver plantillas |
| POST | `/templates` | admin | Crear plantilla |
| PUT | `/templates/<id>` | admin | Editar plantilla |

**Reglas:**
- Las notificaciones se envían de forma **asíncrona** (threading como mínimo; Celery si el proyecto escala).
- El cuerpo de la plantilla soporta variables interpoladas: `{{user_name}}`, `{{pet_name}}`, `{{check_in_date}}`, `{{check_out_date}}`, `{{room_number}}`.
- Guardar siempre registro en la tabla de notificaciones independientemente de si el envío fue exitoso.

---

## Estructura de proyecto recomendada

```
petlodge-backend/
├── app/
│   ├── __init__.py          # Application factory (create_app)
│   ├── config.py            # Config por entorno (Dev, Prod, Test)
│   ├── extensions.py        # db, migrate, jwt, mail
│   ├── models/              # Un archivo por entidad
│   ├── schemas/             # Marshmallow schemas (validación + serialización)
│   ├── routes/
│   │   ├── auth.py
│   │   ├── users.py
│   │   ├── pets.py
│   │   ├── reservations.py
│   │   └── notifications.py
│   ├── services/            # Lógica de negocio separada de las rutas
│   └── utils/
│       ├── decorators.py    # @admin_required, @owner_required
│       └── helpers.py
├── migrations/
├── seeds/
│   ├── seed_rooms.py        # Poblar habitaciones iniciales
│   └── seed_admin.py        # Crear usuario administrador por defecto
├── tests/
├── .env.example
├── requirements.txt
├── docker-compose.yml
└── run.py
```

---

## Seeding inicial

El proyecto debe incluir scripts de seed ejecutables con Flask CLI (`flask seed`). Se registran como comandos personalizados en `app/__init__.py`.

### `seeds/seed_rooms.py` — Habitaciones del hotel

Poblar la base de datos con un set inicial de habitaciones realista (definí vos la cantidad y nomenclatura apropiada para un hotel de mascotas pequeño-mediano). El seed debe ser **idempotente**: si las habitaciones ya existen, no duplicar.

### `seeds/seed_admin.py` — Usuario administrador por defecto

Crear un usuario admin inicial para poder gestionar el sistema desde el primer arranque. Las credenciales se leen desde variables de entorno (`ADMIN_EMAIL`, `ADMIN_PASSWORD`, etc.). También idempotente.

### Registro de comandos CLI

```python
@app.cli.command("seed")
def seed():
    seed_rooms()
    seed_admin()
    click.echo("Seed completado.")
```

Ejecución:
```bash
flask seed
```

---

## Documentación de la API con Swagger

Usar **flask-smorest** para generar documentación interactiva automática disponible en `/api/v1/docs`.

### Convenciones

- Cada Blueprint debe tener una `description` clara.
- Todos los endpoints deben declarar sus schemas de entrada (`@blp.arguments`) y salida (`@blp.response`) para que Swagger los refleje automáticamente.
- Los endpoints protegidos deben incluir el esquema de seguridad `BearerAuth` (JWT Bearer) en la spec OpenAPI.

---

## Convenciones y buenas prácticas

- Usar **application factory pattern** (`create_app()`).
- Separar la lógica de negocio en **servicios** (`services/`), no en los controladores de ruta.
- Todas las respuestas deben seguir una estructura JSON consistente:

```json
{ "success": true, "data": { }, "message": "Operación exitosa" }
```
```json
{ "success": false, "error": "Descripción del error", "code": "RESERVATION_CONFLICT" }
```

- Usar códigos HTTP correctos: `200`, `201`, `400`, `401`, `403`, `404`, `409`, `500`.
- Versionar la API bajo `/api/v1/`.
- Usar variables de entorno para secretos. Nunca hardcodear credenciales.
- Escribir al menos tests unitarios básicos para los servicios críticos (disponibilidad de habitaciones, validación de reservas).

---

## Variables de entorno requeridas (`.env`)

```
FLASK_ENV=development
SECRET_KEY=...
DATABASE_URL=postgresql://user:pass@localhost:5432/petlodge
JWT_SECRET_KEY=...
MAIL_SERVER=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=...
MAIL_PASSWORD=...
MAIL_DEFAULT_SENDER=noreply@petlodge.com
ADMIN_EMAIL=...
ADMIN_PASSWORD=...
ADMIN_FULL_NAME=...
ADMIN_ID_NUMBER=...
```

---

## Entregables esperados

- [ ] Modelo de datos diseñado e implementado en SQLAlchemy con relaciones y restricciones.
- [ ] Migraciones funcionales con Flask-Migrate.
- [ ] Scripts de seed idempotentes (`flask seed`) para habitaciones y admin por defecto.
- [ ] Endpoints funcionando con validación y manejo de errores.
- [ ] Swagger UI disponible en `/api/v1/docs`.
- [ ] Autenticación JWT operativa con roles `owner` y `admin`.
- [ ] Sistema de notificaciones por correo con plantillas variables.
- [ ] Validación de disponibilidad de habitaciones sin solapamiento de fechas.
- [ ] `requirements.txt` y `docker-compose.yml`.
- [ ] README con instrucciones de setup, ejecución y seed.
