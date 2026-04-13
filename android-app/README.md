# PetLodge Android App

Aplicacion Android nativa para la fase movil de PetLodge. Esta app consumira la
API Flask del backend para registro, login, mascotas, reservas, habitaciones,
servicios y notificaciones.

## Estado Actual

- Proyecto Android nativo con Kotlin.
- UI basada en Activities XML + ViewBinding.
- El proyecto ya compila con `assembleDebug`.
- Integracion Retrofit/OkHttp contra el backend Flask.
- Login, registro, perfil, cambio de contrasena y cierre de sesion listos.
- Mascotas: listar, registrar, ver detalle, editar y eliminar.
- Catalogo: listar habitaciones y servicios reales del backend.
- Reservas: crear, listar y cancelar.
- Notificaciones: listar registros generados por el backend.

## Requisitos

- Android Studio reciente.
- Android SDK instalado desde Android Studio.
- JDK incluido con Android Studio, o JDK compatible con Gradle.
- Backend PetLodge corriendo en Docker o localmente.

## Abrir El Proyecto

Abrir directamente esta carpeta en Android Studio:

```text
PetLodge/PetLodge/android-app
```

No abras la raiz completa del repo como proyecto Android. La raiz contiene
tambien backend y web-app.

En Android Studio:

```text
File > Open > .../PetLodge/PetLodge/android-app
```

Espera a que termine el Gradle Sync antes de compilar.

## Configurar Android SDK

Android Studio normalmente crea automaticamente `local.properties`.

Debe verse parecido a esto:

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

Este archivo es local de cada maquina y no se sube a Git.

Si aparece este error:

```text
SDK location not found
```

Soluciones:

- Abrir el proyecto desde Android Studio y dejar que detecte el SDK.
- Crear `local.properties` manualmente.
- Verificar que Android SDK este instalado en:

```text
File > Settings > Languages & Frameworks > Android SDK
```

## Compilar Desde Android Studio

Usa:

```text
Build > Make Project
```

o:

```text
Build > Rebuild Project
```

## Compilar Desde Terminal

Desde `android-app`:

```powershell
.\gradlew.bat assembleDebug
```

APK generado:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Si Gradle intenta escribir en una carpeta rara o sin permisos, puedes usar un
cache local:

```powershell
$env:GRADLE_USER_HOME = (Join-Path (Get-Location) '.gradle-user')
.\gradlew.bat assembleDebug
```

`.gradle-user` esta ignorado por Git.

## Emulador Recomendado

Si, conviene instalar un emulador Android para desarrollar esta app.

Recomendacion:

```text
Pixel 7
API 35 o API 36
System Image: Google APIs x86_64
```

Por que:

- Es un perfil comun y estable.
- Tiene buen tamano de pantalla para probar formularios.
- `Google APIs` es suficiente; no hace falta `Google Play` para esta app.
- `x86_64` suele ser mas rapido en Windows.

Alternativa mas liviana:

```text
Pixel 5
API 35
Google APIs x86_64
```

Usa esta alternativa si tu PC tiene poca RAM.

## Crear Emulador En Android Studio

Ruta:

```text
Tools > Device Manager > Create Device
```

Selecciona:

```text
Phone > Pixel 7 > Next
```

Luego descarga una imagen:

```text
API 35 o API 36 > Google APIs x86_64
```

Finaliza y ejecuta el emulador desde Device Manager.

## Ejecutar La App

1. Levanta el backend:

```powershell
cd ../backend
docker compose up --build
```

2. Confirma que Swagger abre:

```text
http://localhost:5000/api/v1/docs
```

3. En Android Studio selecciona el emulador.

4. Presiona:

```text
Run > Run 'app'
```

Tambien puedes instalar el APK desde terminal si el emulador esta abierto:

```powershell
.\gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.example.petlodge/.LoginActivity
```

Credenciales utiles para probar login cuando el backend ya corrio `flask seed`:

```text
Email: admin@petlodge.com
Password: Admin123!
```

Para probar como usuario movil con mascota demo:

```text
Email: cliente@petlodge.com
Password: Cliente123
```

Tambien puedes crear un usuario nuevo desde la app, Swagger o
`POST /api/v1/auth/register`.

## Prueba Rapida En La App

Con backend y emulador arriba:

1. Registra un usuario desde la app o desde Swagger.
2. Inicia sesion con ese usuario.
3. Entra a `Mascotas` y registra una mascota. Si usas `cliente@petlodge.com`,
   ya existe una mascota demo.
4. Entra a `+ Nueva Reserva`.
5. Selecciona mascota, habitacion y fechas futuras con el calendario.
6. Confirma la reserva.
7. Entra a `Reservas` y verifica que aparece como `Confirmada`.
8. Toca `Cancelar reserva` y verifica que cambia a `Cancelada`.
9. Entra a `Notificaciones` y verifica que aparecen los registros enviados por
   el backend.

Nota: si el correo SMTP del backend no esta configurado, las notificaciones
pueden aparecer con estado `Fallida`. Eso no bloquea la app; indica que el
backend genero el registro, pero no pudo autenticarse contra el servidor SMTP.

## URL Del Backend Desde Android

Importante: dentro del emulador Android, `localhost` apunta al emulador, no a tu
PC.

Para conectar al backend de tu PC desde el emulador usa:

```text
http://10.0.2.2:5000/api/v1/
```

La app usa esa URL por defecto en:

```text
app/src/main/java/com/example/petlodge/data/ApiClient.kt
```

Para telefono fisico conectado a la misma red Wi-Fi usa la IP LAN de tu PC:

```text
http://192.168.x.x:5000/api/v1/
```

El backend debe estar escuchando en `0.0.0.0`, que ya ocurre con `python run.py`
y con Docker.

## Permiso De Internet

Cuando se conecte Retrofit/OkHttp, el manifest debe incluir:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Debe ir como hijo directo de `<manifest>`, antes de `<application>`.

## Flujo A Conectar Primero

Orden recomendado:

1. Login
2. Registro
3. Guardar `access_token`
4. Perfil propio
5. Listar mascotas
6. Registrar mascota
7. Listar habitaciones
8. Listar servicios
9. Crear reserva
10. Listar reservas
11. Cancelar reserva
12. Notificaciones

Estado de integracion:

- Login real contra backend: listo.
- Registro real contra backend: listo.
- Guardado de `access_token` y datos basicos de usuario: listo.
- Perfil, edicion de perfil, cierre de sesion y cambio de contrasena: listo.
- Mascotas: listar, registrar, ver detalle, editar y eliminar: listo.
- Catalogo de habitaciones y servicios: listo.
- Reservas: crear, listar y cancelar: listo.
- Notificaciones: listar registros del backend: listo.

## Endpoints Moviles Del Backend

Base URL para emulador:

```text
http://10.0.2.2:5000/api/v1/
```

Endpoints:

| Funcion | Metodo | Ruta |
| --- | --- | --- |
| Registro | POST | `/auth/register` |
| Login | POST | `/auth/login` |
| Perfil | GET/PUT | `/users/me` |
| Mascotas | GET/POST | `/pets/` |
| Mascota detalle | GET/PUT/DELETE | `/pets/{id}` |
| Habitaciones | GET | `/rooms` |
| Servicios | GET | `/services` |
| Reservas | GET/POST | `/reservations/` |
| Reserva detalle/cancelar | GET/DELETE | `/reservations/{id}` |
| Notificaciones | GET | `/notifications/` |

## Errores Frecuentes

### `SDK location not found`

Falta `local.properties` o Android SDK no esta instalado. Abrir Android Studio y
configurar SDK desde Settings.

### `android.useAndroidX property is not enabled`

Debe existir en `gradle.properties`:

```properties
android.useAndroidX=true
```

### `compileDebugKotlin` con metadata incompatible

El proyecto usa AndroidX reciente. La version Kotlin debe estar alineada en
`gradle/libs.versions.toml`.

Actualmente:

```toml
kotlin = "2.1.20"
```

### La app no conecta al backend desde emulador

No uses:

```text
http://localhost:5000
```

Usa:

```text
http://10.0.2.2:5000
```

Tambien revisa que el backend este levantado y que el manifest tenga permiso de
internet.
