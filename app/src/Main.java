import configuracao.ConexaoMySQL;
import javafx.application.Application;
import repositorios.TxtQuestaoRepositorio;
import servicos.QuestaoServico;
import storage.QuestaoStorage;

public class Main {

    public static void main(String[] args) {

        ConexaoMySQL conexao = new ConexaoMySQL();

        QuestaoStorage storage = new QuestaoStorage(conexao);

        TxtQuestaoRepositorio questaoRepositorio =
                new TxtQuestaoRepositorio(storage);

        QuestaoServico questaoServico =
                new QuestaoServico(questaoRepositorio);

        telas.Questao.setQuestaoServico(questaoServico);
        Application.launch(telas.Questao.class, args);
    }
}