package modelos;

public class Alternativa {
    private int id;
    private String texto;
    private boolean correta;

    public Alternativa(int id, String texto, boolean correta) {
        this.id = id;
        this.texto = texto;
        this.correta = correta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public boolean isCorreta() { return correta; }
    public void setCorreta(boolean correta) { this.correta = correta; }

    @Override
    public String toString() { return texto; }
}
