package whatsapp.common.models;

import java.io.Serializable;

/**
 * Clse padre abstracta para cualquier estructura de datos que transite por los Sockets.
 * Obliga a que todos los mensajes (clases hijas) del sistema sean serializables (soporten Marshalling)
 * y tengan un remitente identificable desde el origen.
 */
public abstract class PaqueteRed implements Serializable {
    private final String idRemitente;
    
    protected PaqueteRed(String idRemitente) {
        this.idRemitente = idRemitente;
    }
    
    public String getIdRemitente() {
        return idRemitente;
    }
}