package modelos;

import java.time.LocalDate;

public class Prova {
    private String codigo;
    private String titulo;
    private String materia;
    private LocalDate data;
    private String curso;
    private int bimestre;

    public Prova(String codigo, String titulo, String materia, LocalDate data, String curso, int bimestre) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.materia = materia;
        this.data = data;
        this.curso = curso;
        this.bimestre = bimestre;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo == null ? null : titulo.trim(); }
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia == null ? null : materia.trim(); }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso == null ? null : curso.trim(); }
    public int getBimestre() { return bimestre; }
    public void setBimestre(int bimestre) { this.bimestre = bimestre; }

    @Override
    public String toString() { return codigo + " — " + titulo; }
}
