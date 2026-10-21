package modelos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Questao {
    private long numero;
    private String titulo;
    private String descricao;
    private List<Alternativa> alternativas;

    public Questao(long numero, String titulo, String descricao, List<Alternativa> alternativas) {
        this.numero = numero;
        this.titulo = titulo;
        this.descricao = descricao;
        this.alternativas = new ArrayList<>(alternativas);
    }

    public long getNumero() { return numero; }
    public void setNumero(long numero) { this.numero = numero; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo == null ? null : titulo.trim(); }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao == null ? null : descricao.trim(); }
    public List<Alternativa> getAlternativas() { return alternativas; }
    public void setAlternativas(List<Alternativa> alternativas) { this.alternativas = new ArrayList<>(alternativas); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Questao questao)) return false;
        return numero == questao.numero;
    }

    @Override
    public int hashCode() { return Objects.hash(numero); }

    @Override
    public String toString() { return titulo; }
}
