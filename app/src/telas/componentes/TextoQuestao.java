package telas.componentes;

import modelos.Alternativa;
import modelos.Questao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Versão em texto de uma questão, usada nos arquivos .txt de provas (PB10) e de questões (PB27).
// Segue os mesmos tipos exibidos pelo CardQuestao.
public final class TextoQuestao {
    private static final String RECUO = "   ";

    private TextoQuestao() { }

    // comResposta == false omite qualquer indicação da resposta correta
    public static String formatar(Questao questao, boolean comResposta) {
        StringBuilder texto = new StringBuilder();
        List<Alternativa> alternativas = questao.getAlternativas();
        switch (CardQuestao.rotuloTipo(questao.getDescricao())) {
            case "Correspondência" -> correspondencia(texto, alternativas, comResposta);
            case "Verdadeiro ou falso em sequência" -> verdadeiroFalso(texto, alternativas, comResposta);
            case "Dissertativa" -> texto.append(RECUO).append("Resposta: ______________________________________________\n");
            default -> multiplaEscolha(texto, alternativas, comResposta);
        }
        if (alternativas.isEmpty() && !"Dissertativa".equals(CardQuestao.rotuloTipo(questao.getDescricao())))
            texto.append(RECUO).append("(sem alternativas cadastradas)\n");
        return texto.toString();
    }

    private static void multiplaEscolha(StringBuilder texto, List<Alternativa> alternativas, boolean comResposta) {
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa alternativa = alternativas.get(i);
            texto.append(RECUO).append(CardQuestao.letra(i)).append(' ').append(alternativa.getTexto());
            if (comResposta && alternativa.isCorreta()) texto.append("  (correta)");
            texto.append('\n');
        }
    }

    private static void verdadeiroFalso(StringBuilder texto, List<Alternativa> afirmacoes, boolean comResposta) {
        for (int i = 0; i < afirmacoes.size(); i++) {
            Alternativa afirmacao = afirmacoes.get(i);
            String marcacao = comResposta ? (afirmacao.isCorreta() ? "(V)" : "(F)") : "(  )";
            texto.append(RECUO).append(marcacao).append(' ').append(i + 1).append(". ").append(afirmacao.getTexto()).append('\n');
        }
    }

    // Sem resposta, a coluna B sai em ordem alfabética para não revelar os pares
    private static void correspondencia(StringBuilder texto, List<Alternativa> pares, boolean comResposta) {
        if (comResposta) {
            for (int i = 0; i < pares.size(); i++) {
                Alternativa par = pares.get(i);
                texto.append(RECUO).append(i + 1).append(". ").append(par.getTexto())
                        .append("  →  ").append(valor(par.getTextoCorrespondencia())).append('\n');
            }
            return;
        }
        texto.append(RECUO).append("Coluna A:\n");
        for (int i = 0; i < pares.size(); i++)
            texto.append(RECUO).append("(  ) ").append(i + 1).append(". ").append(pares.get(i).getTexto()).append('\n');
        List<String> colunaB = new ArrayList<>();
        for (Alternativa par : pares) colunaB.add(valor(par.getTextoCorrespondencia()));
        colunaB.sort(Comparator.comparing(String::toLowerCase));
        texto.append(RECUO).append("Coluna B:\n");
        for (int i = 0; i < colunaB.size(); i++)
            texto.append(RECUO).append(CardQuestao.letra(i)).append(' ').append(colunaB.get(i)).append('\n');
    }

    private static String valor(String texto) { return texto == null || texto.isBlank() ? "Não informado" : texto; }
}
