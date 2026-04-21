package whatsapp.common.models;

public class PaqueteError extends PaqueteRed{
    private static final long serialVersionUID = 1L;
    private final String razon;
    
    public PaqueteError(String userId, String razon) {
        super(userId);
        this.razon = razon;
    }
    
    public String getRazon() {
        return razon;
    }
}
