import configuracao.ConexaoMySQL;
import javafx.application.Application;
import repositorios.ProvaRepositorio;
import repositorios.QuestaoRepositorio;
import servicos.ProvaServico;
import servicos.QuestaoServico;
import storage.ProvaStorage;
import storage.QuestaoStorage;
import storage.UsuarioStorage;
import telas.login.Login;

public class Main {

    public static void main(String[] args) {

        // Application.launch(telas.login.Login.class, args);

        ConexaoMySQL conexao = new ConexaoMySQL();

        // Usuário / Login
        UsuarioStorage usuarioStorage = new UsuarioStorage(conexao);
        Login.setUsuarioStorage(usuarioStorage);

        // Questões e Provas
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
        telas.prova.Prova.setUsuarioStorage(usuarioStorage);

        telas.aluno.Aluno.setProvaServico(provaServico);
        telas.aluno.Aluno.setQuestaoServico(questaoServico);
        telas.aluno.Aluno.setUsuarioStorage(usuarioStorage);

        // O programa começa pelo menu de provas
        //Application.launch(telas.questao.Questao.class, args);
        Application.launch(telas.questao.Questao.class , args);
    }
}
