# PetLodge Backend API

Backend REST API para el sistema de gestión del hotel de mascotas PetLodge, construido con Flask.

## Stack tecnológico

- **Framework:** Flask
- **ORM:** SQLAlchemy + Flask-Migrate (Alembic)
- **Auth:** Flask-JWT-Extended (JWT con access + refresh tokens)
- **Validación:** Marshmallow
- **Documentación:** flask-smorest + Swagger UI
- **Correo:** Flask-Mail
- **Base de datos:** PostgreSQL (prod) / SQLite (dev)
- **Contenedores:** Docker + docker-compose

## Setup local (sin Docker)

### 1. Crear entorno virtual

```bash
cd backend
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows
```

### 2. Instalar dependencias

```bash
pip install -r requirements.txt
```

### 3. Configurar variables de entorno

```bash
cp .env.example .env
# Editar .env con tus credenciales
```

### 4. Inicializar base de datos

```bash
flask db init
flask db migrate -m "Initial migration"
flask db upgrade
```

### 5. Poblar datos iniciales

```bash
flask seed
```

Esto crea:
- 10 habitaciones (6 estándar + 4 especiales)
- 6 servicios adicionales
- 1 usuario administrador (credenciales en `.env`)

### 6. Ejecutar servidor

```bash
python run.py
```

El servidor inicia en `http://localhost:5000`.

## Setup con Docker

```bash
docker-compose up --build
```

Esto levanta PostgreSQL + la API con migraciones y seeds automáticos.

## Documentación API (Swagger)

Disponible en: `http://localhost:5000/api/v1/docs`

## Endpoints principales

| Grupo | Prefijo | Descripción |
|-------|---------|-------------|
| Auth | `/api/v1/auth` | Registro, login, refresh, logout |
| Users | `/api/v1/users` | Perfil, listar usuarios, toggle status |
| Pets | `/api/v1/pets` | CRUD de mascotas |
| Reservations | `/api/v1/reservations` | Crear, listar, detalle, cancelar reservas |
| Notifications | `/api/v1/notifications` | Historial y plantillas de notificación |

## Tests

```bash
pip install pytest
pytest tests/ -v
```

## Estructura del proyecto

```
backend/
├── app/
│   ├── __init__.py          # Application factory
│   ├── config.py            # Configuración por entorno
│   ├── extensions.py        # Instancias de extensiones
│   ├── models/              # Modelos SQLAlchemy
│   ├── schemas/             # Schemas Marshmallow
│   ├── routes/              # Blueprints (endpoints)
│   ├── services/            # Lógica de negocio
│   └── utils/               # Decoradores y helpers
├── seeds/                   # Scripts de seed
├── tests/                   # Tests
├── migrations/              # Migraciones Alembic
├── docker-compose.yml
├── Dockerfile
├── requirements.txt
└── run.py
```
