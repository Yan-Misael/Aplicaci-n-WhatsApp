package whatsapp.common.models;

/**
 * Representación inmutable de un mensaje. Se extiende de PaqueteRed pues es la padre base abstracta.
 * Diseñamos los atributos como 'final' para garantizar que, una vez deserializado 
 * en el servidor, ningún hilo pueda alterar el contenido del mensaje accidentalmente 
 * mientras se enruta a su destino, asgurando persistenci y consistencia de la información).
 */
public class PaqueteMensaje extends PaqueteRed {
    private static final long serialVersionUID = 1L;
    private final String idDestinatario; // Puede ser un ID de un usuario o de un grupo
    private final String contenido;
    private final boolean esGrupo;

    public PaqueteMensaje(String remitente, String destinatario, String contenido, boolean esGrupo) {
        super(remitente);
        this.idDestinatario = destinatario;
        this.contenido = contenido;
        this.esGrupo = esGrupo;
    }

    public String getIdDestinatario() { return idDestinatario; }
    public String getContenido() { return contenido; }
    public boolean isEsGrupo() { return esGrupo; }
}