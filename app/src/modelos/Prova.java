package modelos;

import java.time.LocalDate;
import java.util.Objects;

public class Prova {

    private Long codigo;
    private String titulo;
    private String materia;
    private LocalDate data;
    private String curso;
    private int bimestre;

    public Prova(String titulo, String materia, LocalDate data, String curso, int bimestre) {
        this.titulo = titulo;
        this.materia = materia;
        this.data = data;
        this.curso = curso;
        this.bimestre = bimestre;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo.trim();
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia.trim();
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso.trim();
    }

    public int getBimestre() {
        return bimestre;
    }

    public void setBimestre(int bimestre) {
        this.bimestre = bimestre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prova)) return false;
        Prova prova = (Prova) o;
        return Objects.equals(codigo, prova.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Prova{" +
                "codigo=" + codigo +
                ", titulo='" + titulo + '\'' +
                ", materia='" + materia + '\'' +
                ", data=" + data +
                ", curso='" + curso + '\'' +
                ", bimestre=" + bimestre +
                '}';
    }
}