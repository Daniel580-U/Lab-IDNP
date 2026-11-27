# Lab 09 - Registro de datos locales con Room

Proyecto Android con Jetpack Compose, Room, Corrutinas y `LazyColumn` para el curso **Introducción al Desarrollo de Nuevas Plataformas** (EPIS - UNSA, 2026-B).

## Estructura

- `data/local/ProductoEntity.kt`: Entidad Room (`productos`) con clave primaria autogenerada y 4 atributos.
- `data/local/ProductoDao.kt`: Operaciones `@Query` (`Flow`), `@Insert`, `@Update` y `@Delete`.
- `data/local/AppDatabase.kt`: Configuración Singleton de `RoomDatabase`.
- `data/repository/ProductoRepository.kt`: Capa de repositorio que desacopla el DAO de la UI.
- `ui/ProductoViewModel.kt`: `ViewModel` con `StateFlow` y corrutinas en `Dispatchers.IO`.
- `ui/ProductoScreen.kt`: Formulario de registro, listado reactivo en `LazyColumn` y acciones de incremento/eliminación.
- `MainActivity.kt`: Punto de entrada que inicializa la base de datos, repositorio y `ProductoViewModel`.
