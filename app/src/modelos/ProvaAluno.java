package modelos;

import java.time.LocalDateTime;

public class ProvaAluno {
    // Atributos
    private String codigoProva;
    private String registroAluno;
    private LocalDateTime dataRealizacao;
    private Double notaFinal;
    private String status;

    // Construtores, Getters e Setters

    public ProvaAluno() {
        this.status = "PENDENTE";
    }

    public ProvaAluno(String codigoProva, String registroAluno){
        this.codigoProva = codigoProva;
        this.registroAluno = registroAluno;
        this.status = "PENDENTE";
    }

    public ProvaAluno(String codigoProva, String registroAluno, LocalDateTime dataRealizacao, Double notaFinal, String status){
        this.codigoProva = codigoProva;
        this.registroAluno = registroAluno;
        this.dataRealizacao = dataRealizacao;
        this.notaFinal = notaFinal;
        this.status = status;
    }

    public String getCodigoProva() {
        return codigoProva;
    }

    public void setCodigoProva(String codigoProva) {
        this.codigoProva = codigoProva;
    }

    public String getRegistroAluno() {
        return registroAluno;
    }

    public void setRegistroAluno(String registroAluno) {
        this.registroAluno = registroAluno;
    }

    public LocalDateTime getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDateTime dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRealizada(){
        return "REALIZADA".equalsIgnoreCase(status) || "CORRIGIDA".equalsIgnoreCase(status) || dataRealizacao != null;
    }
}