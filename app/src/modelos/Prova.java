package modelos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Prova {
    private String codigo;
    private String titulo;
    private String materia;
    private LocalDateTime dataHora;
    private String curso;
    private int bimestre;

    public Prova(String codigo, String titulo, String materia, LocalDateTime dataHora, String curso, int bimestre) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.materia = materia;
        this.dataHora = dataHora;
        this.curso = curso;
        this.bimestre = bimestre;
    }

    public Prova(String codigo, String titulo, String materia, LocalDate data, String curso, int bimestre) {
        this(codigo, titulo, materia, data == null ? null : data.atStartOfDay(), curso, bimestre);
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo == null ? null : titulo.trim(); }
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia == null ? null : materia.trim(); }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public LocalDate getData() { return dataHora == null ? null : dataHora.toLocalDate(); }
    public void setData(LocalDate data) { this.dataHora = data == null ? null : data.atStartOfDay(); }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso == null ? null : curso.trim(); }
    public int getBimestre() { return bimestre; }
    public void setBimestre(int bimestre) { this.bimestre = bimestre; }

    @Override
    public String toString() { return codigo + " — " + titulo; }
}
