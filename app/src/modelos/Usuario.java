package modelos;

public class Usuario {
    private final String registro;
    private final String nome;
    private final String tipoUsuario; // "Professor" ou "Aluno"

    public Usuario(String registro, String nome, String tipoUsuario) {
        this.registro = registro;
        this.nome = nome;
        this.tipoUsuario = tipoUsuario;
    }

    public String getRegistro() { return registro; }
    public String getNome() { return nome; }
    public String getTipoUsuario() { return tipoUsuario; }

    public boolean isProfessor() {
        return "Professor".equalsIgnoreCase(tipoUsuario);
    }

    public boolean isAluno() {
        return "Aluno".equalsIgnoreCase(tipoUsuario);
    }
}