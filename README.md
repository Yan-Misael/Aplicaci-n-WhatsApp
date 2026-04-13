# Aplicación WhatsApp Distribuida

Este sistema consiste en una plataforma de mensajería distribuida programada en Java, diseñada para funcionar mediante procesos independientes que manejan funciones de gran escala como el chat privado y la creación de grupos. El proyecto se enfoca en resolver la comunicación remota, la seguridad de los datos en memoria y la estabilidad ante fallos.

## 1. Modelo Arquitectónico 

* **Tipo de Arquitectura:**
    El sistema utiliza una arquitectura centralizada de tipo **Cliente-Servidor (Broker)**. El servidor funciona como el punto central y único intermediario de toda la comunicación.Esto asegura la **transparencia de ubicación**, ya que los clientes no necesitan conocer la dirección IP de los demás usuarios, solo la del servidor central para encontrarse en la red.

* **Capas del Sistema:**
    * **Capa de Presentación (Cliente):** Interfaz donde el usuario interactúa para enviar mensajes o crear grupos.
    * **Capa de Datos (Models):** Clases que definen el formato de los paquetes. Usan atributos `final` para asegurar que la información no cambie una vez que entra al flujo de red.
    * **Capa de Lógica (Managers):** Gestores que administran el estado global, validan reglas y aplican los bloqueos de seguridad en la memoria.
    * **Capa de Red (Handlers):** Hilos individuales (`ManejadorCliente`) que se encargan del tráfico de cada usuario de forma aislada.
    * **Capa Núcleo (Core):** Clase principal que inicia el servidor, define el puerto y enlaza los componentes.

* **Flujo de Comunicación:**
    1.  **Conexión:** El servidor acepta la solicitud mediante `ServerSocket.accept()` y le entrega la conexión a un hilo nuevo.
    2.  **Solicitud:** El cliente transforma sus datos en un objeto (ej. `PaqueteMensaje`) y lo envía por la red.
    3.  **Procesamiento:** El servidor recibe el objeto, identifica qué se está pidiendo y utiliza los Managers para actualizar o consultar el estado.
    4.  **Difusión:** El servidor busca el destino del paquete y lo inyecta en el flujo de salida del destinatario.

## 2. Gestión de Concurrencia y Estado Global

Para manejar múltiples usuarios al mismo tiempo sin que la memoria se corrompa, el sistema aplica las siguientes técnicas de sincronización.

* **Modelo de atención:** Se utiliza un **hilo por cliente**. Esto permite que el servidor pueda atender a un usuario mientras espera que otro termine de enviar sus datos, evitando que el sistema se detenga.
* **Sincronización en Sesiones (`SessionManager`):** Aplicamos bloques `synchronized` sobre un candado privado para que el registro de usuarios sea una operación indivisible. Esto evita que dos personas tomen el mismo nombre de usuario si intentan conectarse en el mismo milisegundo.
* **Gestión de Grupos (`GroupManager`):** Usamos `ReentrantReadWriteLock` para manejar el acceso a las salas de chat. Este cerrojo permite que muchos usuarios lean la lista de miembros al mismo tiempo para enviar mensajes, pero bloquea el paso cuando alguien está entrando o saliendo del grupo, manteniendo la lista siempre coherente.
* **Copias Defensivas:** Al consultar los miembros de un grupo, el sistema entrega una copia nueva de la lista (`ArrayList`). Esto asegura que si el grupo cambia mientras el servidor está recorriendo la lista para enviar mensajes, el proceso no falle ni lance errores de modificación.

## 3. Comunicación y Marshalling

* **Intercambio de datos:** No enviamos texto plano. El sistema utiliza **Marshalling nativo de Java** a través de la interfaz `Serializable`. Esto permite enviar objetos complejos (con múltiples atributos y tipos) manteniendo la estructura de los datos.
* **Polimorfismo en Red:** El servidor recibe una clase base (`PaqueteRed`) y utiliza `instanceof` para saber qué tipo de mensaje es. Esto permite que el sistema sea fácil de ampliar con nuevas funciones sin tocar la base de la conexión.

## 4. Transparencia y Manejo de Fallos

* **Transparencia de Acceso:** El proyecto está unificado con **Maven**. El archivo `pom.xml` define las coordenadas del sistema (`com.whatsapp`), permitiendo que cualquier desarrollador lo importe en NetBeans, VS Code o IntelliJ y el proyecto funcione igual de inmediato.
* **Manejo de Fallos (Crash):** Cada conexión corre dentro de bloques `try-catch-finally`. Si un cliente se desconecta a la fuerza o su aplicación falla, el servidor detecta el error en su hilo específico, limpia sus datos del `SessionManager` y cierra el socket sin que el resto de los usuarios se den cuenta..

## 5. Instrucciones de Ejecución

1.  **Requisitos:** Tener instalado JDK 17 y Apache Maven.
2.  **Configuración:** Abrir la carpeta raíz como proyecto Maven en el IDE.
3.  **Arranque:** Ejecutar la clase `whatsapp.server.core.ServidorPrincipal`. El servidor utilizará el puerto `5000` por defecto.

## 6. Fundamentación de Ingeniería y Diseño

El sistema se diseñó siguiendo estándares de software para asegurar que cada parte sea independiente y fácil de mantener.

### 6.1 Patrones de Diseño

* **Monitor:** Utilizado en el `SessionManager` para proteger el mapa de sesiones. El uso de un candado interno asegura que solo un hilo modifique el estado crítico a la vez, garantizando la consistencia.
* **Read-Write Lock:** Implementado en el `GroupManager` para optimizar el tráfico. Permite lecturas paralelas masivas, lo cual es ideal para un sistema de chat donde se lee mucho más de lo que se escribe.
* **DTO (Data Transfer Object):** Las clases en `models` funcionan como contenedores de datos inmutables que viajan por el socket, separando la información de la lógica que la procesa.

### 6.2 Principios SOLID

* **Responsabilidad Única (SRP):** Cada componente tiene una tarea clara: el servidor conecta, el manejador lee la red y los gestores manejan los datos.
* **Abierto/Cerrado (OCP):** El sistema puede crecer con nuevos tipos de mensajes (ej. `PaqueteLlamada`) simplemente extendiendo la base de `PaqueteRed`, sin necesidad de cambiar el código que ya funciona.
* **Inversión de Dependencias (DIP):** Los hilos de red no crean sus propios gestores; los reciben desde el `ServidorPrincipal`. Esto facilita las pruebas y hace que el sistema sea menos rígido.

### 6.3 Toma de Decisiones Técnicas

* **Inmutabilidad:** Se decidió que los paquetes de red no puedan ser modificados tras su creación. Esto quita el riesgo de que el contenido de un mensaje cambie accidentalmente mientras pasa por diferentes partes del servidor.
* **Bloqueo Segmentado:** En lugar de bloquear todo el servidor cuando alguien se conecta, solo se bloquea el recurso mínimo necesario. Esto asegura que el sistema siga respondiendo rápido aunque haya mucha gente entrando al mismo tiempo.
* **Estandarización:** Se eligió Maven para asegurar que el proyecto sea reproducible. Esto elimina errores por diferencias en la configuración de las computadoras de los desarrolladores.