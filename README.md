# PrintersServer

El proyecto `PrintersServer` es un microservicio diseñado para gestionar las solicitudes de impresión y coordinarse con [PrintersClient](https://github.com/IESJandula/Reaktor_PrintersClient) para realizar las tareas de impresión. Este proyecto depende de [BaseServer](https://github.com/IESJandula/Base_Server/) para funcionalidades comunes.

## Descripción de los Servicios y Componentes

### CORSConfig
`CORSConfig` configura Cross-Origin Resource Sharing (CORS) para permitir solicitudes desde diferentes dominios. Define qué orígenes, métodos HTTP y encabezados están permitidos al interactuar con el microservicio `PrintersServer`.

### InicializacionSistema
`InicializacionSistema` es una clase encargada de la inicialización y configuración del sistema cuando se inicia el microservicio. Actualmente se encarga de montar la estructura de carpetas externa para el fat-jar, es decir, preparar del entorno de ejecución para el servidor de impresoras, y de almacenar en BBDD los días festivos.

## Data Transfer Objects (DTOs)

Los DTOs son objetos que se utilizan para transferir datos entre los sistemas de cliente y servidor. En el proyecto `PrintersServer`, los DTOs en el paquete `dto` son:

- **DtoPrinters**: Representa los datos de una impresora que se envían o reciben desde el cliente, incluyendo el nombre, estado, cola de impresión, última actualización, entre otros.
- **RequestDtoPrintQuery**: Utilizado para recibir solicitudes de consulta de impresión dede la Web. Contiene los parámetros de búsqueda como usuario, impresora, estado y rango de fechas.
- **ResponseDtoGlobalState**: Utilizado para enviar el estado global del sistema, incluyendo información sobre impresoras y posibles errores globales que afecten las operaciones.
- **ResponseDtoPrintAction**: Utilizado para enviar detalles sobre las acciones de impresión, como el estado de la impresión, el usuario que envió la tarea, y otros parámetros relevantes.

## Modelos

Los modelos en `PrintersServer` representan las entidades principales manejadas por el sistema:

- **Printer**: Representa una impresora registrada en el sistema, incluyendo atributos como el nombre de la impresora, ubicación, tipo de impresora, y estado actual entre otros.
- **PrintAction**: Representa una tarea de impresión, con detalles como el ID de la tarea, el usuario que la envió, el documento a imprimir, la impresora asignada, el número de copias, y el estado de la tarea de impresión, entre otros.
- **DiaFestivo**: Representa un día festivo registrado en el sistema, que puede afectar la disponibilidad de las impresoras.

## Repositorio

El `repository` en `PrintersServer` proporciona la capa de acceso a datos para los modelos:

- **IPrinterRepository**: Interfaz que extiende `JpaRepository`, proporcionando métodos para interactuar con la base de datos de impresoras. Incluye métodos personalizados para buscar impresoras por atributos específicos.
- **IPrintActionRepository**: Interfaz para gestionar las acciones de impresión, incluyendo la búsqueda y actualización de tareas de impresión basadas en diferentes criterios.
- **IDiaFestivoRepository**: Interfaz para gestionar los días festivos, permitiendo consultas y operaciones sobre los días festivos registrados en la base de datos.

## Controlador REST: PrinterRest

`PrinterRest` es el controlador REST principal para manejar todas las solicitudes HTTP relacionadas con las impresoras y las tareas de impresión. Proporciona los siguientes endpoints:

### Endpoints de `PrinterRest`

Los siguientes endpoints están securizados en base al JWT que viene del cliente, esto es, el endpoint define qué role se necesita, y se confirma con el AuthorizationService si el JWT contiene los roles esperados. 

- **`GET /printers/web/printers`**: Devuelve la lista de todas las impresoras registradas en el sistema.
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Response**: Devuelve una lista de impresoras o un mensaje de error en caso de falta de permisos.

- **`GET /printers/web/states`**: Obtiene la lista de estados disponibles para las impresoras.
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Response**: Devuelve la lista de estados o un mensaje de error en caso de falta de permisos.

- **`GET /printers/web/orientations`**: Devuelve la lista de orientaciones disponibles (horizontal o vertical).
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Response**: Devuelve la lista de orientaciones o un mensaje de error en caso de falta de permisos.

- **`GET /printers/web/colors`**: Devuelve la lista de colores disponibles (color o blanco y negro).
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Response**: Devuelve la lista de colores o un mensaje de error en caso de falta de permisos.

- **`GET /printers/web/sides`**: Devuelve la lista de caras disponibles (una cara o doble cara).
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Response**: Devuelve la lista de caras o un mensaje de error en caso de falta de permisos.

- **`GET /printers/web/validations`**: Realiza validaciones globales antes de la impresión y devuelve el estado del sistema.
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Response**: Devuelve el estado global del sistema o un mensaje de error en caso de falta de permisos.

- **`POST /printers/web/filter`**: Filtra las impresiones en función de los parámetros proporcionados.
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Request Body**: Recibe un `RequestDtoPrintQuery` con los parámetros de búsqueda.
  - **Response**: Devuelve una lista de `ResponseDtoPrintAction` con las impresiones encontradas o un mensaje de error.

- **`POST /printers/web/print`**: Procesa una solicitud de impresión y guarda el documento en el servidor.
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: PROFESOR.
  - **Request Params**: Detalles de la impresora, número de copias, orientación, color, caras, usuario, y archivo PDF.
  - **Response**: Devuelve un mensaje de éxito o un error si la solicitud no se pudo procesar.

- **`POST /printers/client/printers`**: Actualiza las impresoras actuales basándose en la información recibida del cliente.
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: CLIENTE_IMPRESORA.
  - **Request Body**: Lista de `DtoPrinters` con los detalles de las impresoras.
  - **Response**: Devuelve un mensaje de éxito o un error si la actualización falla.

- **`GET /printers/client/print`**: Busca una tarea de impresión pendiente y la envía al cliente para imprimirla. Role esperado: CLIENTE_IMPRESORA
  - **Headers**: `Authorization` (Token de autenticación JWT). Role esperado: CLIENTE_IMPRESORA
  - **Response**: Devuelve el archivo a imprimir o un mensaje de error si no hay tareas disponibles.

- **`POST /printers/client/status`**: Actualiza el estado de una tarea de impresión según la respuesta del cliente. Role esperado: CLIENTE_IMPRESORA
  - **Headers**: `Authorization` (Token de autenticación JWT), `id`, `status`, `message`, `exception`. Role esperado: CLIENTE_IMPRESORA
  - **Response**: Devuelve un mensaje de éxito o un error si la actualización falla.

## Variables de Configuración

Las variables anotadas con `@Value` en este proyecto deben configurarse adecuadamente para que el microservicio `PrintersServer` funcione correctamente. Estas variables se definen en el archivo `application.yml` del microservicio.

### Detalles de las Variables `@Value` y cómo obtenerlas:

1. **`reaktor.publicKeyFile`**: Ruta al archivo de clave pública.
   - **Cómo obtenerlo**: Genera la clave pública utilizando el siguiente comando:
     ```bash
     openssl rsa -in C:\\claves\\private_key.pem -pubout -out C:\\claves\\public_key.pem
     ```
   - **Dónde almacenarlo**: Coloca el archivo `public_key.pem` en `C:\\claves`.

2. **`reaktor.urlCors`**: Lista de orígenes permitidos para las solicitudes CORS.
   - **Cómo configurarlo**: Define los orígenes permitidos, por ejemplo: `http://localhost:5173, http://192.168.1.209:5173, http://192.168.1.181:5173, http://192.168.1.137:5173`.
   - **Dónde almacenarlo**: En el archivo `application.yml`.

3. **`reaktor.firebase_server_url`**: URL del servidor Firebase.
   - **Cómo configurarlo**: Define la URL del servidor Firebase, por ejemplo: `http://localhost:8083`.
   - **Dónde almacenarlo**: En el archivo `application.yml`.

4. **`reaktor.uidFile`**: Ruta al archivo que contiene el UID del usuario CLIENTE_IMPRESORA.

   - **Cómo obtenerlo**: Crea un archivo de texto llamado `uid_file.txt` que contenga el UID del usuario que tenga este role.

   - **Dónde almacenarlo**: Colócalo en `C:\claves`.

Asegúrate de que todos estos archivos estén correctamente ubicados y accesibles por el microservicio para garantizar su funcionamiento.
"""
## Dependencias

  

Este proyecto depende de [BaseServer](https://github.com/IESJandula/Base_Server/) para funcionalidades básicas como la autorización, almacenamiento de sesión y actualización de JARs.

  

## Creación de Elementos en la Colección de Firebase

  

Para que el sistema funcione correctamente, es necesario crear una colección en Firebase llamada `usuarios` donde se almacenarán los datos de los usuarios. Sigue los siguientes pasos para crear elementos en esta colección:

  

1. **Accede a la consola de Firebase**:

Ve a [Firebase Console](https://console.firebase.google.com/) e inicia sesión con tu cuenta de Google.

  

2. **Selecciona tu proyecto**:

Haz clic en tu proyecto para abrir el panel de control.

  

3. **Ve a Firestore Database**:

En el menú de la izquierda, selecciona **Firestore Database** y haz clic en **Crear base de datos** si aún no lo has hecho. Asegúrate de seleccionar el modo de producción.

  

4. **Crear la colección `usuarios`**:

- Haz clic en **Iniciar colección** y escribe `usuarios` como nombre de la colección.

- Haz clic en **Siguiente** para añadir el primer documento.

  

5. **Añadir un documento**:

- Define el **ID del documento**: Este será el UID del usuario, que puedes obtener desde la pestaña de Firebase Authentication.

- Añade los siguientes campos al documento:

- **email** (tipo: `string`): El correo electrónico del usuario.

- **nombre** (tipo: `string`): El nombre del usuario.

- **apellidos** (tipo: `string`): Los apellidos del usuario.

- **roles** (tipo: `array`): Lista de roles asignados al usuario, por ejemplo: `["PROFESOR", "DIRECCIÓN"]`.

6. **Guardar el documento**:

Haz clic en **Guardar** para crear el documento en la colección `usuarios`.

Repite estos pasos para cada usuario que necesites agregar al sistema.