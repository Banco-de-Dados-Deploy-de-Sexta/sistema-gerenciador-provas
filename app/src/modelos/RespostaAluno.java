package modelos;

public class RespostaAluno {
    // Atributos
    private int id;
    private String codigoProva;
    private String registroAluno;
    private int idQuestao;
    private Integer idAlternativaEscolhida;
    private String respostaTexto;
    private int ordemQuestao;
    private Boolean acertou;
    private Double notaObtida;

    // Contrutores, Getteres e Setters
    public RespostaAluno() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getIdQuestao() {
        return idQuestao;
    }

    public void setIdQuestao(int idQuestao) {
        this.idQuestao = idQuestao;
    }

    public Integer getIdAlternativaEscolhida() {
        return idAlternativaEscolhida;
    }

    public void setIdAlternativaEscolhida(Integer idAlternativaEscolhida) {
        this.idAlternativaEscolhida = idAlternativaEscolhida;
    }

    public String getRespostaTexto() {
        return respostaTexto;
    }

    public void setRespostaTexto(String respostaTexto) {
        this.respostaTexto = respostaTexto;
    }

    public int getOrdemQuestao() {
        return ordemQuestao;
    }

    public void setOrdemQuestao(int ordemQuestao) {
        this.ordemQuestao = ordemQuestao;
    }

    public Boolean getAcertou() {
        return acertou;
    }

    public void setAcertou(Boolean acertou) {
        this.acertou = acertou;
    }

    public Double getNotaObtida() {
        return notaObtida;
    }

    public void setNotaObtida(Double notaObtida) {
        this.notaObtida = notaObtida;
    }
}