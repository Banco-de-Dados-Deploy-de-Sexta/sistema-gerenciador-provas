package modelos;

import java.util.Objects;

public class Questao {

    private long numero;
    private String titulo;
    private String descricao;
    private String alternativas;
    private String alternativa_correta;

    public Questao(long numero, String titulo, String descricao, String alternativas, String alternativa_correta){
        this.numero = numero;
        this.titulo = titulo;
        this.descricao = descricao;
        this.alternativas = alternativas;
        this.alternativa_correta = alternativa_correta;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(String alternativas) {
        this.alternativas = alternativas;
    }

    public String getAlternativa_correta() {
        return alternativa_correta;
    }

    public void setAlternativa_correta(String alternativa_correta) {
        this.alternativa_correta = alternativa_correta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof  Questao)) return false;
        Questao questao = (Questao) o;
        return Objects.equals(numero, questao.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return "Questao{" +
                "numero=" + numero +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", alternativas=" + alternativas +
                ", alternativa_correta='" + alternativa_correta + '\'' +
                '}';
    }

}
