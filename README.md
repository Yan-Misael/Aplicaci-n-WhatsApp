# Aplicacion-WhatsApp
Desarrollo de una aplicación distribuida en Java compuesta por procesos independientes tales como la creación de grupos y manejo de llamadas.


## Modelo Arquitectónico 

- Tipo de Arquitectura:

El sistema emplea una arquitectura centralizada de tipo Cliente-Servidor. 
El servidor actúa como el "broker" o intermediario absoluto de toda la comunicación, 
garantizando la transparencia de ubicación; los clientes no necesitan conocer la 
dirección IP de los demás usuarios, solo la del servidor central.

- Capas del Sistema:

Capa de Presentación (Cliente): Interfaz gráfica (GUI) o consola en NetBeans donde el usuario interactúa (crea grupos, inicia llamadas).

Capa de Lógica de Negocio (Servidor): Compuesta por los Managers que validan la creación de grupos, gestionan los estados de las llamadas y enrutan los mensajes.

Capa de Red (Comunicación): Implementada mediante Sockets TCP para asegurar la entrega fiable de los datos, apoyada por el proceso de Marshalling 
(Serialización de Java) para transformar los objetos de las solicitudes en flujos de bytes.

- Flujo de Comunicación General:

Conexión: El Cliente solicita conexión; el Servidor acepta mediante ServerSocket.accept() y despacha un hilo (ManejadorCliente).

Solicitud: El Cliente serializa un objeto (ej. PaqueteGrupo) y lo envía por el ObjectOutputStream.

Procesamiento: El hilo del Servidor deserializa el objeto, identifica la función solicitada e interactúa con los recursos compartidos utilizando exclusión mutua (synchronized).

Respuesta/Difusión: El Servidor localiza los sockets de los destinatarios correspondientes e inyecta los objetos de respuesta en sus respectivos flujos de salida.
