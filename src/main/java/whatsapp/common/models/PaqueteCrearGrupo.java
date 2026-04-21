package whatsapp.common.models;

public class PaqueteCrearGrupo extends PaqueteRed {
    private static final long serialVersionUID = 1L;
    private final String idGrupo;
    
    public PaqueteCrearGrupo(String idCreador, String idGrupo) {
        super(idCreador);
        this.idGrupo = idGrupo;
    }
    
    public String getIdGrupo() { return idGrupo; }
}