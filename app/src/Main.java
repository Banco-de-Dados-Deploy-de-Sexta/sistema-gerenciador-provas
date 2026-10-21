import configuracao.ConexaoMySQL;
import javafx.application.Application;
import repositorios.ProvaRepositorio;
import repositorios.QuestaoRepositorio;
import servicos.ProvaServico;
import servicos.QuestaoServico;
import storage.ProvaStorage;
import storage.QuestaoStorage;

public class Main {

    public static void main(String[] args) {

        ConexaoMySQL conexao = new ConexaoMySQL();

        QuestaoStorage questaoStorage = new QuestaoStorage(conexao);
        QuestaoRepositorio questaoRepositorio = new QuestaoRepositorio(questaoStorage);
        QuestaoServico questaoServico = new QuestaoServico(questaoRepositorio);

        ProvaStorage provaStorage = new ProvaStorage(conexao);
        ProvaRepositorio provaRepositorio = new ProvaRepositorio(provaStorage);
        ProvaServico provaServico = new ProvaServico(provaRepositorio, questaoServico);

        telas.Questao.setQuestaoServico(questaoServico);
        telas.BancoQuestoes.setQuestaoServico(questaoServico);
        telas.Prova.setProvaServico(provaServico);
        telas.Prova.setQuestaoServico(questaoServico);

        // O programa agora começa pela Home
        Application.launch(telas.Home.class, args);
    }
}
