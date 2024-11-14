Link al repositorio: https://github.com/Oyupa/Libreria_Con_Firebase_PDE


# Librería de Novelas

## Antes de empezar
Aunque se puede crear un usuario nuevo si es necesario. Ya existen varios. Uno de ellos es:
- Usuario: admin
- Contraseña: admin

## Descripción de la Aplicación

Esta aplicación es una biblioteca para gestionar novelas. Permite a los usuarios agregar nuevas novelas, eliminar novelas existentes, ver detalles de cada novela, marcarlas como favoritas y añadir reseñas. La interfaz es sencilla e intuitiva, diseñada para mejorar la experiencia del usuario al gestionar sus novelas.

## Cómo Funciona la Aplicación

1. **Pantalla Principal:** Muestra una lista de novelas en un RecyclerView.
2. **Agregar Novela:** Un botón en la parte inferior de la pantalla permite a los usuarios agregar nuevas novelas. Se solicita información como el título, autor, año y sinopsis.
3. **Detalles de Novela:** Al seleccionar una novela de la lista, se muestra una vista detallada con toda la información relevante.
4. **Favoritos:** Los usuarios pueden marcar novelas como favoritas y verlas en una sección designada.
5. **Reseñas:** Se pueden agregar reseñas para cada novela, proporcionando una mejor interacción con el contenido.
6. **Autenticación de Usuarios:** Los usuarios pueden registrarse e iniciar sesión para guardar sus datos de manera segura.

## Pasos Seguidos para Crear la Aplicación
### Versión 1.0:
1. **Planificación:** Definí los requisitos y funcionalidades deseadas para la aplicación.
2. **Configuración del Proyecto:**
    - Creé un nuevo proyecto en Android Studio utilizando Kotlin como lenguaje de programación.
    - Configuré las dependencias necesarias, incluyendo la biblioteca RecyclerView.
3. **Diseño de la Interfaz:**
    - Diseñé el archivo XML para la pantalla principal (`main_activity.xml`), que incluye un RecyclerView y un botón para agregar nuevas novelas.
    - Diseñé la vista de detalles de la novela (`novel_item.xml`) para mostrar la información de cada novela.
4. **Implementación de Funcionalidades:**
    - Implementé la clase `Novel` para representar la estructura de datos de las novelas.
    - Creé una clase `NovelAdapter` para gestionar la visualización de novelas en el RecyclerView.
    - Implementé métodos para agregar, eliminar y marcar novelas como favoritas, así como para gestionar las reseñas.
5. **Persistencia de Datos:**
    - Utilicé SharedPreferences para guardar las novelas y su estado (favoritas, reseñas) de manera persistente.
6. **Manejo de Errores:**
    - Añadí validaciones para asegurar que los datos introducidos por el usuario sean correctos y manejé posibles excepciones durante la ejecución.

### Versión 2.0:
1. **Implementación de una Base de Datos:**
    - Utilicé Firebase Database para almacenar y recuperar los datos de las novelas.
    - Implementé métodos para cargar y guardar datos en la base de datos.
    - Actualicé la aplicación para que utilice la base de datos en lugar de SharedPreferences.
2. **Mejoras en la Interfaz de Usuario:**
    - Añadí animaciones y transiciones para mejorar la experiencia del usuario.
    - Mejoré el diseño de la pantalla principal y de los detalles de la novela.

### Versión 3.0:
1. **Implementación de Autenticación:**
    - Añadí una base de datos de usuarios en Firebase para gestionar la autenticación.
    - Implementé métodos para el registro y el inicio de sesión de los usuarios de manera segura para que las contraseñas no se guarden en texto plano.
    - Añadí una pantalla de inicio de sesión y registro para los usuarios.
    - Implementé el tema oscuro en la aplicación para mejorar la experiencia de los usuarios.

### Versión 4.0:
1. **Implementación de un Fragmento de Detalles:**
    - Añadí un fragmento para mostrar los detalles de las novelas en una vista separada.
    - Implementé la navegación entre el fragmento y la actividad principal.
2. **Widget de Favoritos:** 
    - Implementé un widget para mostrar las novelas favoritas en la pantalla de inicio del dispositivo.
    - Añadí métodos para actualizar el widget cuando se marcan o desmarcan novelas como favoritas.

## Funcionalidades Implementadas

- **Agregar Novelas:** Permite a los usuarios ingresar datos de una nueva novela.
- **Eliminar Novelas:** Posibilidad de eliminar novelas de la lista.
- **Ver Detalles:** Al hacer clic en una novela, se muestran sus detalles completos.
- **Marcar como Favorita:** Los usuarios pueden marcar novelas como favoritas.
- **Agregar Reseñas:** Posibilidad de agregar reseñas a las novelas.
- **Autenticación de Usuarios:** Registro e inicio de sesión de usuarios.
- **Tema Oscuro:** Implementación de un tema oscuro para la aplicación.
- **Widget:** Implementación de un widget para mostrar las novelas favoritas.

## Problemas Encontrados

### Versión 1.0:
- **Errores de Inflación XML:** Tuve problemas iniciales al inflar los archivos de diseño, lo que se solucionó revisando el XML y asegurándome de que no hubiera errores de sintaxis.
- **Manejo de JSON:** Al cargar datos desde SharedPreferences, encontré excepciones de formato de número debido a entradas no válidas en el JSON. Esto se resolvió implementando validaciones antes de agregar datos.
- **Interfaz de Usuario:** Hubo desafíos al diseñar la interfaz de usuario, especialmente en la visualización de los detalles de las novelas. La solución consistió en ajustar los elementos del diseño para que se visualizaran correctamente.

### Versión 2.0:
- **Integración con Firebase:** La integración con Firebase Database presentó desafíos al configurar los parametros de la base de datos y al implementar los métodos de carga y guardado de datos. Se resolvió comparando otros códigos de programas con Firebase.
- **Actualización de la Interfaz de Usuario:** Al añadir animaciones y transiciones, hubo problemas de rendimiento y visualización. Se solucionaron ajustando la duración y la configuración de las animaciones.

### Versión 3.0:
- **Implementación de Autenticación:** La implementación de la autenticación de usuarios con Firebase Authenticator presentó desafíos al configurar las reglas de seguridad y al manejar los datos de los usuarios. Se resolvió cambiando a una base de datos de usuarios en Firebase y utilizando métodos seguros para el registro y el inicio de sesión implementados por mi.
- **Tema Oscuro:** Al implementar el tema oscuro, hubo problemas con la visualización de los elementos de la interfaz de usuario. Se solucionaron cambiando los layouts.

### Versión 4.0:
- **Problemas con el fragmento de detalles:** Al implementar el fragment de detalles de las novelas, se han perdido las funcionalidades de los botones.
- **Incompatibilidad de los datos con el widget**: Al intentar implementar un widget para la aplicación, se ha encontrado problemas con la compatibilidad de los datos de la base de datos con el widget. Se han acabado solucionando buscando la solución en Internet (StackOverflow).