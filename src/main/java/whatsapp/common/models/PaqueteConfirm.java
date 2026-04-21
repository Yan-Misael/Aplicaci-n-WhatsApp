package whatsapp.common.models;

public class PaqueteConfirm extends PaqueteRed {
    private static final long serialVersionUID = 1L;
    private final boolean exito;
    private final String mensaje;
    
    public PaqueteConfirm(String destinatario, boolean exito, String mensaje) {
        super(destinatario);  // aquí el remitente es el servidor, pero usamos el campo para indicar el destinatario
        this.exito = exito;
        this.mensaje = mensaje;
    }
    
    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
}