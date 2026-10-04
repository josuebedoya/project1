# Proyecto Profundización Programación Orientada a Objetos II
 
Creación de un proyecto **API Rest** con el framework **Spring Boot**, motor de base de datos **MySQL**, **JAVA**, **JPA** y repositorio de dependencias **Maven**.
 
---
 
# Fase 1
 
*(Fuente: Proyecto_E1_V2.pdf)*
 
## Arquitectura
 
Capas: `@Controller` → `@Service` → `@Repository` (varios), y el `@Controller` apunta a `View`.
 
- En esta entrega el componente **View** del diseño será reemplazado por las invocaciones de los servicios a través de la aplicación **Postman**, dado que no se profundizará en el curso temas de frontEnd.
- Diseño tomado de https://www.arquitecturajava.com/spring-stereotypes/
## Estructura Base de Datos
 
Se requiere diseñar y construir las siguientes entidades a nivel de base de datos.
 
### Vehículos
 
- Identificador de vehículo (**Primary Key**).
- Tipo de vehículo (**Automóvil - Motocicleta**).
- Placa.
  - Debe tener seis caracteres exactos.
    - Automóvil: los tres primeros caracteres letras seguido de tres caracteres numéricos.
    - Motocicleta: los tres primeros caracteres letras seguido de dos caracteres numéricos y terminar en un carácter de letra.
  - El campo de placa debe ser único.
- Tipo de servicios (Público (Pu) o Privado (Pr)).
- Tipo de combustible (Gasolina – Gas – Disel).
- Capacidad de pasajeros. Debe ser numérico y de tipo entero.
- Color. Debe registrar la respectiva representación del código de color en hexadecimal.
- Modelo. Debe ser numérico y de tipo entero.
- Marca. Ejemplo: Toyota.
- Línea. Ejemplo: Fortuner SW.
### Documentos
 
Entidad **paramétrica o de configuración**, que contiene los diferentes documentos que pueden estar asociados a los tipos de vehículos que se manejan según la entidad vehículos.
 
- Identificador de documento (**Primary Key**).
- Código de documento parametrizado.
- Nombre del documento. Ejemplo: SOAT – Técnico Mecánica – Seguro Todo Riesgo, etc.
- Tipos de vehículos a los que aplica el documento:
  - `A` si aplica para automóvil.
  - `M` si aplica para motocicletas.
  - `AM` si aplica para ambos.
- Parametrizar si el documento es de carácter obligatorio según el tipo de vehículo:
  - `RA` obligatorio para automóvil.
  - `RM` obligatorio para motocicleta.
  - `RR` requerido para ambos.
- Descripción del documento parametrizado.
> **Nota:** Para esta entidad crear restricciones de chequeo o validación sobre los campos que contienen valores específicos.
 
### Relación Vehículo y Documentos
 
Se deberá crear la relación entre las dos entidades anteriores, donde un vehículo tiene como mínimo un documento asociado o muchos documentos, y adicionalmente agregar los siguientes campos complementarios a dicha relación:
 
- Fecha de expedición del documento.
- Fecha de vencimiento del documento.
- Estado de documento asociado al vehículo: **Habilitado – Vencido – En Verificación**.
> **Nota:** Para esta entidad crear restricciones de chequeo o validación sobre el campo de estado del documento asociado al vehículo.
 
## Requerimiento de Servicio
 
- Realizar el **CRUD (POST – GET – DELETE – PUT)** de un vehículo, teniendo presente que la aplicación deberá poner como estado inicial de los documentos asociados al vehículo **En Verificación**, esto al momento de crear un vehículo.
  - **Importante:** No se puede crear un vehículo sin que tenga un documento asociado.
- Realizar un servicio por cada una de las siguientes opciones de búsqueda:
  - Buscar vehículo por número de placa.
  - Buscar vehículos por tipo de vehículo.
  - Buscar vehículos que tengan en común un tipo de documento.
  - Buscar vehículos según el estado del documento asociado al vehículo, es decir, documentos Habilitados, Vencidos o En Verificación.
- Crear un servicio que permita agregar documentos asociados a un vehículo.
- Realizar el **CRUD (POST – GET – DELETE – PUT)** de la entidad paramétrica de documentos.
---
 
# Fase 2
 
*(Fuente: Proyecto_E2_V2.pdf)*
 
## Arquitectura
 
Misma arquitectura de la Fase 1 (`@Controller` → `@Service` → `@Repository`, con `View`).
 
- El componente **View** será reemplazado por las invocaciones de los servicios a través de **Postman**.
- Diseño tomado de https://www.arquitecturajava.com/spring-stereotypes/
- **Nota:** Se mantienen las mismas definiciones de arquitectura y tecnologías sugeridas en la entrega 1 del proyecto final.
## Estructura Base de Datos
 
Se requiere diseñar y construir las siguientes entidades a nivel de base de datos.
 
### Persona
 
Almacena los datos básicos de una persona que represente ser un conductor o, en su respectivo caso, un administrador del sistema.
 
- Identificador de persona (**Primary Key**).
- Identificación de la persona.
- Tipo de identificación de la persona (CC – Cédula de Ciudadanía).
- Nombres.
- Apellidos.
- Correo electrónico.
- Tipo de persona (**C** – Conductor, **A** – Administrativo).
> **Nota:** Crear restricciones de chequeo o validación sobre los campos que contienen valores específicos, como el tipo de identificación y el tipo de persona.
 
### Usuario
 
Registro de los diferentes usuarios del sistema. Condición: solo las personas de tipo **ADMINISTRATIVO** tendrán usuarios, y uno y solo un usuario está relacionado a una y solo una persona. La entidad tendrá como **primary key compuesta** los campos `idpersona` y `login`, para cumplir el requerimiento (una persona, uno y solo un usuario).
 
- `login` (usuario del sistema). Regla de nemotecnia: primera letra del nombre, seguida de la primera letra del apellido y por último el número de identificación de la persona.
- `idpersona`. **Foreign Key** que referencia la clave primaria de la entidad Persona.
- `password`.
- `apikey`. Valor de generación automática que el sistema asigna al usuario al momento de crearlo.
### Relación Vehículo y Persona
 
Se deberá crear la relación entre las dos entidades, donde un vehículo tiene como mínimo un conductor asociado o muchos conductores, y adicionalmente agregar los siguientes campos complementarios:
 
> **Importante:** Aquí se asocian únicamente personas de tipo **CONDUCTOR**.
 
- Fecha de asociación conductor al vehículo.
- Estado del conductor en relación con el vehículo:
  - `PO` – Puede Operar.
  - `EA` – Espera de Aprobación.
  - `RO` – Restringido para Operar.
> **Nota:** Crear restricciones de chequeo o validación sobre el campo de estado del conductor asociado al vehículo.
 
### Adición de campo en la relación Vehículo y Documento
 
En la Fase 1 se solicitó la creación de esta relación. Ahora se requiere **agregar un campo para almacenar el documento PDF**, de tipo **BLOB**, registrando el documento en **BASE64**.
 
## Requerimiento de Servicio
 
- Desarrollar las funciones **(POST – GET – PUT)** de una persona, teniendo presente que la aplicación deberá generar un usuario en el caso de la persona tipo ADMINISTRATIVO.
  - **Importante:**
    - No se puede crear una persona de tipo ADMINISTRATIVO sin que tenga un usuario asociado.
    - El campo `login` de la entidad usuario deberá seguir la regla de nemotecnia descrita.
    - El campo `password` al momento de crear el usuario será uno generado de forma automática.
    - El campo `APIKey` al momento de crear el usuario será uno generado de forma automática.
- Servicio que permita el **cambio de password** de un usuario específico: la nueva contraseña se envía por el **body** y el `login` del usuario por la **URL**.
- Servicio **GET** que permita nuevamente la **generación del APIKey** de un usuario específico.
- Servicio **POST** que permita el **cargue y/o actualización de los documentos** relacionados con los vehículos, pudiendo cargar uno o varios documentos a la vez. Para esta entrega se contempla el cargue de un archivo **PDF en Base64**.
- Servicio que permita **asociar los vehículos** que puede operar un conductor específico.
- Servicio que permita **cambiar el estado del conductor** en relación con el vehículo (PO – Puede Operar, EA – Espera de Aprobación, RO – Restringido para Operar).
> **Nota:** Los requerimientos anteriores deberán estar configurados de forma segura para que sean consumidos por un usuario que tenga como tipo de persona **ADMINISTRADOR**, mediante la configuración de un **token** y adicionalmente la configuración del **APIKey en la cabecera** del servicio.
 
### Servicios públicos (no requieren token)
 
- Consultar todos los vehículos que tengan documentos vencidos.
- Consultar todos los conductores que puedan operar.
- Consultar un vehículo por placa, donde se relacione la información de los conductores asociados, así como los documentos.
- Consultar los vehículos que tienen documentos por vencer, con un tiempo que se especifique como parámetro en la consulta.
- Consultar el total de las personas agrupadas por tipo.
Por último, los requerimientos de servicios desarrollados en la **Fase 1** deberán ser configurados de forma segura, es decir, que requieran configuración de **token y APIKey**.
 
---
 
# Fase 3
 
*(Fuente: Proyecto_E3_V2.pdf)*
 
## Arquitectura
 
Misma arquitectura de las fases anteriores (`@Controller` → `@Service` → `@Repository`, con `View`).
 
- El componente **View** será reemplazado por las invocaciones de los servicios a través de **Postman**.
- Diseño tomado de https://www.arquitecturajava.com/spring-stereotypes/
- **Nota:** Se mantienen las mismas definiciones de arquitectura y tecnologías sugeridas en la entrega 1 del proyecto final.
## Estructura Base de Datos
 
Se requiere diseñar y construir las siguientes entidades a nivel de base de datos.
 
### Trayecto
 
Almacena los datos correspondientes al trayecto que realiza un respectivo conductor con un vehículo específico. Dicho trayecto tiene como mínimo una parada inicial y una parada final o, en su defecto, un máximo de **5 paradas intermedias** sin contar la inicial y la final.
 
- Identificador de trayecto (**Primary Key**).
- `idpersona`. **Foreign Key** que referencia la clave primaria de la entidad Persona. Solo deben realizarse asociaciones de personas de tipo **conductor**.
- `idvehiculo`. **Foreign Key** que referencia la clave primaria de la entidad Vehículo, para identificar el vehículo que realiza el trayecto.
- Código de ruta. Representa la llave que agrupa varios trayectos, con requisito mínimo de un trayecto inicial y final, y un máximo de 5 trayectos intermedios; es decir, el código representa la ruta realizada por el conductor relacionado en el vehículo especificado.
- Ubicación. Cadena de texto que especifica el lugar de parada. Ejemplo: *Conservatorio del Tolima, Ibagué, Tolima*.
- Orden de parada. `0` corresponde a la parada inicial, el número mayor a la parada final, y los valores intermedios a las paradas intermedias.
- Latitud. Coordenada geográfica de la ubicación.
- Longitud. Coordenada geográfica de la ubicación.
- Login de usuario que registra el trayecto.
> **Nota:** La creación de un trayecto debe estar condicionada a que la relación de los documentos con el vehículo esté en estado **Habilitado** y que el conductor asociado al trayecto pueda operar (**PO**) según el estado del conductor en relación con el vehículo.
 
### Adición de campos a la entidad Persona
 
Dos campos nuevos que aplican cuando la persona es tipo conductor (**C**):
 
- Licencia de conducción. Tipo de dato **BLOB**; el documento se registra en formato **BASE64** y representa la licencia de conducción.
- Fecha de vigencia de la licencia de conducción.
## Requerimientos Técnicos
 
Crear las siguientes **tareas programadas** a nivel de la aplicación Spring Boot:
 
- **Cada 2 minutos:** verificar la fecha de vigencia de las licencias de los conductores registrados en el sistema. A quienes estén vencidas se les cambia el estado del conductor en la relación con el vehículo a **RO – Restringido para Operar**.
  - **CEREZA (opcional):** enviar un correo electrónico según la información del conductor, para informarle de la restricción de operar las rutas que tiene a su cargo.
- **Cada 2 minutos:** verificar la fecha de vigencia de los documentos asociados al vehículo. Los documentos vencidos cambian su estado (documento asociado al vehículo) a **VENCIDO**.
- **Cada 90 segundos:** verificar los trayectos que no contienen longitud y latitud asociadas a la ubicación registrada. Estos valores se extraen con apoyo de la **API externa Google Maps** y deben persistir en la base de datos.
## Requerimiento de Servicio
 
- Servicio de consulta de rutas con el **código de ruta** como parámetro de entrada. Debe ser un servicio **protegido (requiere token)** y mostrar en orden los trayectos y su respectiva información asociada.
- Servicio de consulta con el **número de identificación de un conductor** como parámetro, que muestre únicamente los códigos de ruta de forma agrupada. Servicio **protegido (requiere token)**.
- Servicio de consulta con la **placa de un vehículo** como parámetro, que muestre el código de ruta y el conductor asociado al trayecto de forma agrupada. Servicio **protegido (requiere token)**.
- Servicio de consulta de las rutas y la información de los trayectos donde el **vehículo NO esté habilitado** o el **conductor esté Restringido para Operar**.
## Requerimiento FrontEnd
 
Crear una **interfaz visual web** en la que se puedan visualizar los diferentes trayectos o paradas de un código de ruta específico.
 
*Ejemplo (imagen del PDF):* una captura de Google Maps de una ruta en Ibagué con 5 paradas (Conservatorio del Tolima, Museo Panóptico de Ibagué, La Estación Centro Comercial Ibagué, Acqua Power Center y Parque Deportivo), listadas a la izquierda y marcadas sobre el mapa, con la ruta trazada entre ellas.