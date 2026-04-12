# PetLodge Backend API

Backend REST API para PetLodge, el sistema de gestion de un hotel de mascotas.
La API sirve a la app movil Android y cubre autenticacion, usuarios, mascotas,
reservas, catalogo de habitaciones/servicios y notificaciones.

## Stack

- Flask 3
- SQLAlchemy + Flask-Migrate/Alembic
- PostgreSQL 16
- Flask-JWT-Extended
- Marshmallow
- flask-smorest + Swagger UI
- Flask-Mail
- Docker + Docker Compose

## Requisitos

- Docker Desktop
- Python 3.12, solo si vas a correr Flask fuera de Docker
- PowerShell en Windows

En Windows, instala Python 3.12 desde `python.org`. Evita crear el entorno con
los aliases de Microsoft Store/WindowsApps porque pueden dejar un `venv` roto.

## Inicio Rapido Con Docker

Este es el camino recomendado para levantar el backend completo.

```powershell
cd backend
docker compose up --build
```

Si tu instalacion usa el comando viejo:

```powershell
docker-compose up --build
```

Esto levanta:

- PostgreSQL en `localhost:5432`
- API Flask en `http://localhost:5000`
- Migraciones automaticas con `flask db upgrade`
- Seeds automaticos con `flask seed`

Swagger queda disponible en:

```text
http://localhost:5000/api/v1/docs
```

Credenciales admin del seed:

```text
Email: admin@petlodge.com
Password: Admin123!
```

Para detener:

```powershell
docker compose down
```

Para borrar tambien la base local de desarrollo:

```powershell
docker compose down -v
```

Usa `down -v` solo si quieres resetear PostgreSQL local. Borra el volumen
`backend_pgdata`.

## Setup Local Con PostgreSQL En Docker

Usa este flujo si quieres correr `python run.py` desde tu maquina, pero usar
PostgreSQL en Docker.

### 1. Crear entorno virtual

```powershell
cd backend
py -3.12 -m venv venv
.\venv\Scripts\activate
```

Linux/Mac:

```bash
cd backend
python3.12 -m venv venv
source venv/bin/activate
```

### 2. Instalar dependencias

```powershell
python -m pip install --upgrade pip setuptools wheel
pip install -r requirements.txt
```

### 3. Configurar variables de entorno

```powershell
Copy-Item .env.example .env
```

El `.env` local debe apuntar a `localhost`, porque Flask corre fuera de Docker:

```env
DATABASE_URL=postgresql://petlodge:petlodge@localhost:5432/petlodge
```

Dentro del contenedor `api`, la URL usa el hostname interno `db`:

```env
DATABASE_URL=postgresql://petlodge:petlodge@db:5432/petlodge
```

### 4. Levantar solo PostgreSQL

```powershell
docker compose up -d db
```

### 5. Aplicar migraciones

Este repo ya trae `migrations/` y una migracion inicial. No ejecutes
`flask db init` en este proyecto clonado.

```powershell
flask --app run:app db upgrade
```

### 6. Poblar datos iniciales

```powershell
flask --app run:app seed
```

Esto crea:

- 10 habitaciones
- 6 servicios adicionales
- 1 usuario administrador

### 7. Ejecutar API

```powershell
python run.py
```

La API inicia en:

```text
http://localhost:5000
```

## Migraciones

Comandos correctos para este repo:

```powershell
flask --app run:app db upgrade
```

Solo cuando cambies modelos y necesites generar una migracion nueva:

```powershell
flask --app run:app db migrate -m "descripcion del cambio"
flask --app run:app db upgrade
```

No uses `flask db init`; ya existe la carpeta `migrations/`.

## Tests

Con entorno local:

```powershell
.\venv\Scripts\activate
pip install pytest
pytest tests/ -v
```

Con Docker:

```powershell
docker compose exec -T api python -m pip install pytest
docker compose exec -T api python -m pytest tests -q
```

Resultado esperado:

```text
24 passed
```

## Pruebas Rapidas Con Curl

Catalogo publico para la app movil:

```powershell
curl.exe http://localhost:5000/api/v1/rooms
curl.exe http://localhost:5000/api/v1/services
```

Registro:

```powershell
curl.exe -X POST http://localhost:5000/api/v1/auth/register `
  -H "Content-Type: application/json" `
  --data-raw '{"email":"cliente@test.com","password":"Password123","full_name":"Cliente Test","id_number":"1234567890"}'
```

Login:

```powershell
curl.exe -X POST http://localhost:5000/api/v1/auth/login `
  -H "Content-Type: application/json" `
  --data-raw '{"email":"cliente@test.com","password":"Password123"}'
```

Para endpoints protegidos, usa:

```text
Authorization: Bearer <access_token>
```

## Endpoints Principales

| Grupo | Prefijo | Descripcion |
| --- | --- | --- |
| Auth | `/api/v1/auth` | Registro, login, refresh, logout |
| Users | `/api/v1/users` | Perfil, listar usuarios, activar/desactivar |
| Pets | `/api/v1/pets` | CRUD de mascotas |
| Reservations | `/api/v1/reservations` | Crear, listar, detalle y cancelar reservas |
| Notifications | `/api/v1/notifications` | Historial y plantillas de notificacion |
| Catalog | `/api/v1/rooms`, `/api/v1/services` | Habitaciones y servicios disponibles |

## Flujo Movil Cubierto

La API ya permite a la app movil:

- Registrar usuario dueno de mascota
- Iniciar sesion y recibir JWT
- Ver/editar perfil propio
- Registrar, listar, editar y eliminar mascotas
- Listar habitaciones disponibles
- Listar servicios adicionales
- Crear reservas estandar y especiales
- Validar solapamientos de reservas
- Cancelar reservas
- Consultar historial de notificaciones

## Estructura

```text
backend/
|-- app/
|   |-- __init__.py          # Application factory
|   |-- config.py            # Configuracion por entorno
|   |-- extensions.py        # Extensiones Flask
|   |-- models/              # Modelos SQLAlchemy
|   |-- schemas/             # Schemas Marshmallow
|   |-- routes/              # Endpoints
|   |-- services/            # Logica de negocio
|   `-- utils/               # Helpers y decoradores
|-- migrations/              # Migraciones Alembic
|-- seeds/                   # Datos iniciales
|-- tests/                   # Tests
|-- docker-compose.yml
|-- Dockerfile
|-- requirements.txt
`-- run.py
```

## Errores Frecuentes

### `Failed to build psycopg2-binary`

Normalmente pasa si estas usando Python 3.14. Usa Python 3.12:

```powershell
py -3.12 -m venv venv
```

### `Unable to create process ... WindowsApps ... python.exe`

El `venv` fue creado con el alias de Microsoft Store. Borralo y recrealo con
Python 3.12 instalado desde `python.org`:

```powershell
Remove-Item -Recurse -Force .\venv
py -3.12 -m venv venv
```

### `password authentication failed for user "petlodge"`

PostgreSQL esta usando un volumen viejo con otras credenciales. Para resetear la
base local de desarrollo:

```powershell
docker compose down -v
docker compose up --build
```

### `could not translate host name "db"`

Estas usando `db` desde fuera de Docker. Usa `localhost` en `.env` local:

```env
DATABASE_URL=postgresql://petlodge:petlodge@localhost:5432/petlodge
```

El hostname `db` solo funciona entre contenedores de Docker Compose.

### `No module named pytest`

Instala pytest en el entorno donde vas a correr la suite:

```powershell
pip install pytest
```
