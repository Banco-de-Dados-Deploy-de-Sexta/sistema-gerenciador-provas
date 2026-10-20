import configuracao.ConexaoMySQL;
import javafx.application.Application;
import repositorios.QuestaoRepositorio;
import servicos.QuestaoServico;
import storage.QuestaoStorage;

public class Main {

    public static void main(String[] args) {

        ConexaoMySQL conexao = new ConexaoMySQL();

        QuestaoStorage storage = new QuestaoStorage(conexao);

        QuestaoRepositorio questaoRepositorio = new QuestaoRepositorio(storage);

        QuestaoServico questaoServico = new QuestaoServico(questaoRepositorio);

        telas.Questao.setQuestaoServico(questaoServico);
        Application.launch(telas.Questao.class, args);
    }
}