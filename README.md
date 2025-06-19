# 📱 PracticaCRUD - Aplicación Móvil con APIs Públicas

Una aplicación móvil nativa desarrollada en Android (Kotlin) que integra operaciones CRUD, consumo de APIs públicas, sistema de favoritos, recomendaciones personalizadas y gestión de usuarios con diferentes roles.

## 🎯 Características Principales

### 🔐 Sistema de Autenticación
- **Registro e inicio de sesión** con validación
- **Roles de usuario**: Administrador y Usuario regular
- **Persistencia de sesión** con tokens JWT
- **Gestión de perfiles** con imágenes

### 📚 Integración con APIs Públicas
- **Open Library API**: Búsqueda de libros y autores
- **TVMaze API**: Búsqueda de series y películas
- **Búsqueda en tiempo real** con persistencia local
- **Caché inteligente** para funcionamiento offline

### ⭐ Sistema de Favoritos y Recomendaciones
- **Gestión de favoritos** personalizada por usuario
- **Historial de búsquedas** con seguimiento temporal
- **Sistema de recomendaciones** basado en:
  - Favoritos del usuario
  - Historial de búsquedas
  - Patrones de comportamiento
- **Sincronización** entre datos locales y remotos

### 🛠️ Operaciones CRUD (Solo Administradores)
- **Gestión completa de usuarios**: Crear, leer, actualizar, eliminar
- **Subida de imágenes** para perfiles de usuario
- **Interfaz administrativa** con controles de acceso

## 🏗️ Arquitectura Técnica

### 📱 Frontend (Android)
- **Lenguaje**: Kotlin
- **Arquitectura**: MVVM
- **UI**: XML Layouts con Material Design
- **Navegación**: Menu lateral (Navigation Drawer)

### 🔄 Networking
- **Retrofit**: Cliente HTTP para APIs REST
- **OkHttp**: Manejo de interceptores y autenticación
- **Gson**: Serialización/deserialización JSON

### 💾 Base de Datos Local
- **Room Database**: Persistencia local con SQLite
- **Entidades**: Users, Books, Shows, Favorites, SearchHistory
- **DAOs**: Acceso optimizado a datos
- **Sincronización**: Estrategia online-first con fallback offline

### 🔧 Dependencias Principales
```kotlin
// Networking
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.retrofit2:converter-gson:2.9.0"
implementation "com.squareup.okhttp3:okhttp:4.9.3"

// Base de datos local
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"

// UI y Material Design
implementation "com.google.android.material:material:1.9.0"
implementation "com.github.bumptech.glide:glide:4.15.1"
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- Android Studio Arctic Fox o superior
- SDK de Android 24+ (Android 7.0)
- Servidor backend corriendo en puerto 3000
- Conexión a internet para APIs públicas

### Configuración del Backend
1. **Actualizar la IP del servidor** en `RetrofitClient.kt`:
```kotlin
const val BASE_URL = "http://TU_IP_LOCAL:3000/"
```

2. **Configurar seguridad de red** en `network_security_config.xml`:
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">TU_IP_LOCAL</domain>
</domain-config>
```

### Pasos de Instalación
1. **Clonar el repositorio**
```bash
git clone [URL_DEL_REPOSITORIO]
cd PracticaCRUD
```

2. **Abrir en Android Studio**
   - File > Open > Seleccionar la carpeta del proyecto

3. **Sincronizar dependencias**
   - Click en "Sync Now" cuando aparezca la notificación

4. **Ejecutar la aplicación**
   - Conectar dispositivo Android o usar emulador
   - Click en Run (▶️)

## 📖 Guía de Uso

### 👤 Para Usuarios Regulares
1. **Registro/Login**: Crear cuenta o iniciar sesión
2. **Búsqueda**: Explorar libros y series usando las pestañas
3. **Favoritos**: Agregar contenido a favoritos con el botón ⭐
4. **Historial**: Revisar búsquedas anteriores
5. **Recomendaciones**: Descubrir contenido personalizado
6. **Perfil**: Actualizar información personal y foto

### 👨‍💼 Para Administradores
- **Todo lo anterior** más:
- **Operaciones CRUD**: Gestionar usuarios del sistema
- **Crear usuarios**: Asignar roles y configuraciones
- **Editar perfiles**: Modificar información de cualquier usuario
- **Eliminar usuarios**: Control total del sistema

## 📁 Estructura del Proyecto

```
app/src/main/java/com/example/practicacrud/
├── 📱 Activities/
│   ├── LoginActivity.kt
│   ├── MainActivity.kt
│   ├── SearchActivity.kt
│   ├── FavoritesActivity.kt
│   ├── HistoryActivity.kt
│   ├── RecommendationsActivity.kt
│   ├── ProfileActivity.kt
│   └── CrudActivity.kt
├── 🔧 Adapters/
│   ├── SearchResultsAdapter.kt
│   ├── RecommendationsAdapter.kt
│   └── HistoryAdapter.kt
├── 🌐 API/
│   ├── ApiService.kt
│   ├── RetrofitClient.kt
│   ├── OpenLibraryService.kt
│   └── TVMazeService.kt
├── 💾 Database/
│   ├── AppDatabase.kt
│   ├── entities/
│   └── dao/
├── 📦 Models/
│   ├── AuthModels.kt
│   ├── OpenLibraryModels.kt
│   └── TVMazeModels.kt
├── 🏪 Repository/
│   └── DataRepository.kt
├── 🎯 Services/
│   └── RecommendationService.kt
└── 🛠️ Utils/
    └── AuthManager.kt
```

## 🔑 APIs Utilizadas

### 📚 Open Library API
- **Base URL**: `https://openlibrary.org/`
- **Endpoints**: 
  - `/search.json` - Búsqueda de libros
  - `/search/authors.json` - Búsqueda de autores
- **Documentación**: [Open Library API Docs](https://openlibrary.org/developers/api)

### 📺 TVMaze API
- **Base URL**: `https://api.tvmaze.com/`
- **Endpoints**:
  - `/search/shows` - Búsqueda de series
  - `/shows/{id}` - Detalles de serie
- **Documentación**: [TVMaze API Docs](https://www.tvmaze.com/api)

## 🔧 Características Técnicas Avanzadas

### 💾 Persistencia de Datos
- **Estrategia híbrida**: Online-first con fallback offline
- **Caché inteligente**: Limpieza automática de datos antiguos
- **Sincronización**: Actualización periódica entre local y remoto

### 🔐 Seguridad
- **Autenticación JWT**: Tokens seguros para sesiones
- **Interceptores HTTP**: Inyección automática de headers
- **Validación de roles**: Control granular de accesos
- **Configuración de red**: Soporte para HTTP en desarrollo

### 📱 Experiencia de Usuario
- **Material Design**: Interfaz moderna y consistente
- **SwipeRefresh**: Actualización manual de listas
- **Navegación fluida**: Menu lateral responsivo
- **Feedback visual**: Indicadores de carga y estados vacíos
- **Gestión de imágenes**: Carga optimizada con Glide

## 🐛 Solución de Problemas Comunes

### ❌ La aplicación se cierra al abrir Search/Favorites/History
**Solución**: Verificar que todas las actividades estén declaradas en `AndroidManifest.xml`

### 🌐 Error de conexión al backend
**Solución**: 
1. Verificar que la IP en `RetrofitClient.kt` sea correcta
2. Asegurar que el servidor backend esté corriendo
3. Verificar configuración de `network_security_config.xml`

### 📱 Problemas con imágenes de perfil
**Solución**: Verificar permisos de almacenamiento en el dispositivo

### 💾 Base de datos no se crea
**Solución**: Limpiar caché de la app (Settings > Apps > PracticaCRUD > Storage > Clear Cache)

## 🤝 Contribuciones

Este proyecto fue desarrollado como práctica académica para el curso de **Desarrollo de Aplicaciones Móviles Nativas**. Las contribuciones son bienvenidas siguiendo las mejores prácticas de desarrollo Android.

## 📄 Licencia

Proyecto educativo desarrollado para fines académicos.

---

## 📞 Soporte

Para dudas o problemas:
1. Revisar la sección de **Solución de Problemas**
2. Verificar la configuración del backend
3. Consultar logs de Android Studio para errores específicos

**¡Feliz desarrollo! 🚀**
