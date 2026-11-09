package modelos;

public class Alternativa {
    private int id;
    private String texto;
    private String textoCorrespondencia;
    private boolean correta;

    public Alternativa(int id, String texto, boolean correta) {
        this(id, texto, null, correta);
    }

    public Alternativa(int id, String texto, String textoCorrespondencia, boolean correta) {
        this.id = id;
        this.texto = texto;
        this.textoCorrespondencia = textoCorrespondencia;
        this.correta = correta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getTextoCorrespondencia() { return textoCorrespondencia; }
    public void setTextoCorrespondencia(String textoCorrespondencia) { this.textoCorrespondencia = textoCorrespondencia; }
    public boolean isCorreta() { return correta; }
    public void setCorreta(boolean correta) { this.correta = correta; }

    @Override
    public String toString() { return texto; }
}
