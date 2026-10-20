import configuracao.ConexaoMySQL;
import javafx.application.Application;
import repositorios.QuestaoRepositorio;
import repositorios.ProvaRepositorio;
import servicos.ProvaServico;
import servicos.QuestaoServico;
import storage.QuestaoStorage;
import storage.ProvaStorage;

public class Main {

    public static void main(String[] args) {

        ConexaoMySQL conexao = new ConexaoMySQL();
        

        // QuestaoStorage storage = new QuestaoStorage(conexao);

        // QuestaoRepositorio questaoRepositorio = new QuestaoRepositorio(storage);

        // QuestaoServico questaoServico = new QuestaoServico(questaoRepositorio);

        // telas.Questao.setQuestaoServico(questaoServico);
        // Application.launch(telas.Questao.class, args);


        ProvaStorage storage = new ProvaStorage(conexao);

        ProvaRepositorio repositorio = new ProvaRepositorio(storage);

        ProvaServico servico = new ProvaServico(repositorio);

        telas.Prova.setProvaServico(servico);

        Application.launch(telas.Prova.class, args);
    }
}