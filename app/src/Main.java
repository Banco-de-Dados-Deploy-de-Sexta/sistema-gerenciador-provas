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

        telas.questao.Questao.setQuestaoServico(questaoServico);
        telas.bancoquestoes.BancoQuestoes.setQuestaoServico(questaoServico);
        telas.prova.Prova.setProvaServico(provaServico);
        telas.prova.Prova.setQuestaoServico(questaoServico);

        // O programa começa pelo menu de provas
        Application.launch(telas.prova.Prova.class, args);
    }
}
