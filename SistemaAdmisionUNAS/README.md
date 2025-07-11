# 🎓 Sistema de Admisión UNAS - Guía de Uso

## 🚀 Funcionalidades Principales

### 📝 Inscripción de Postulantes
- Formulario completo para registro de nuevos postulantes
- Validación automática de datos
- Cálculo automático de puntaje final
- Sincronización en tiempo real con otras pestañas

### 📋 Lista de Postulantes
- **Visualización completa** de todos los postulantes registrados
- **🔍 Búsqueda y filtros** por modalidad, estado académico, etc.
- **📥 IMPORTACIÓN** desde archivos Excel/CSV (botón "Importar")
- **📊 Exportación** de datos a Excel/CSV
- **🗑️ Eliminación** de registros
- **📊 Estadísticas en tiempo real**

### 🏆 Resultados
- Cálculo automático de resultados de admisión
- Ranking por carrera
- Estadísticas de ingreso

### ⚙️ Administración
- Gestión de carreras disponibles
- Configuración del sistema
- Sincronización automática con formularios

## 📥 Cómo Importar Postulantes

### Paso 1: Preparar el Archivo
1. **Para archivos Excel (.xlsx/.xls):**
   - Abra el archivo en Microsoft Excel
   - Vaya a "Archivo" → "Guardar como"
   - Seleccione formato "CSV (separado por comas)"
   - Guarde el archivo

2. **Para archivos CSV/TXT:**
   - Asegúrese de que el archivo esté separado por comas, punto y coma o tabulaciones
   - Use codificación UTF-8 para caracteres especiales

### Paso 2: Formato del Archivo
El archivo debe tener las siguientes columnas (mínimo las primeras 6):

```
CODIGO,APELLIDOS_NOMBRES,OPCION1,OPCION2,MODALIDAD,DNI,SEXO,ESTADO_ACADEMICO,NOTA_AC,NOTA_CO
```

**Ejemplo:**
```
2025001,GARCIA LOPEZ JUAN CARLOS,INGENIERÍA DE SISTEMAS,MEDICINA HUMANA,ORDINARIO,12345678,M,POSTULANTE,16.5,17.2
```

### Paso 3: Importar desde la Pestaña "Lista"
1. Vaya a la pestaña **📋 Lista**
2. Haga clic en el botón **📥 Importar**
3. Seleccione su archivo CSV/TXT
4. Confirme la importación
5. Espere a que se complete el proceso
6. Los datos se mostrarán automáticamente en la lista

## 💾 Persistencia de Datos
- **Base de datos H2** con persistencia completa
- Los datos se guardan automáticamente en `./data/sistemaadmision.mv.db`
- **NO se pierden los datos** al cerrar y reabrir la aplicación
- La base de datos se crea automáticamente al primera ejecución

## 🔄 Sincronización Automática
- Las carreras agregadas en **Administración** aparecen automáticamente en **Inscripción**
- Los postulantes registrados o importados se muestran inmediatamente en **Lista**
- Los resultados se actualizan automáticamente

## 📁 Archivos de Ejemplo
En la carpeta `ejemplos/` encontrará:
- `postulantes_ejemplo.csv` - Archivo de ejemplo con formato correcto

## 🛠️ Solución de Problemas

### Error al importar:
- Verifique que el archivo tenga las columnas requeridas
- Asegúrese de que la primera línea contenga los encabezados
- Para archivos Excel, guarde como CSV

### Datos no aparecen:
- Haga clic en "🔄 Actualizar" en la pestaña Lista
- Verifique los filtros aplicados

### Base de datos no persiste:
- Verifique que la carpeta `data/` tenga permisos de escritura
- No elimine los archivos `.mv.db` en la carpeta `data/`

## 🎯 Funcionalidades Destacadas

### ✨ Nuevas Mejoras en esta Versión:
- **Importación integrada** en la pestaña Lista (no más pestaña separada)
- **Base de datos 100% persistente** - los datos no se pierden
- **Detección automática** de separadores en archivos CSV
- **Validación robusta** de datos importados
- **Feedback visual** durante la importación con barra de progreso
- **Sincronización en tiempo real** entre todas las pestañas

---

**Versión:** 1.0 - Sistema de Admisión UNAS
**Autor:** joe-696
**Universidad:** Universidad Nacional Agraria de la Selva (UNAS)
