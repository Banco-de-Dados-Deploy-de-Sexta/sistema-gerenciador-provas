package modelos;

import java.time.LocalDate;
import java.util.Objects;

public class Prova {

    private Long codigo;
    private String titulo;
    private String materia;
    private LocalDate data;
    private String curso;
    private Integer bimestre;

    public Prova() {
    }

    public Prova(Long codigo, String titulo, String materia, LocalDate data, String curso, Integer bimestre) {
        setCodigo(codigo);
        setTitulo(titulo);
        setMateria(materia);
        setData(data);
        setCurso(curso);
        setBimestre(bimestre);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        if (codigo != null && codigo <= 0) {
        }
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
        }
        if (titulo.length() > 150) {
        }
        this.titulo = titulo.trim();
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        if (materia == null || materia.trim().isEmpty()) {
        }
        this.materia = materia.trim();
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        if (data == null) {
        }
        if (data.isBefore(LocalDate.now())) {
        }
        this.data = data;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        if (curso == null || curso.trim().isEmpty()) {
        }
        this.curso = curso.trim();
    }

    public Integer getBimestre() {
        return bimestre;
    }

    public void setBimestre(Integer bimestre) {
        if (bimestre == null || bimestre < 1 || bimestre > 4) {
        }
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