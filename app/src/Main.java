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

        // Horário confiável: API externa -> banco -> (último recurso) relógio local.
        // Evita que o aluno burle o horário da prova alterando a data/hora do Windows.
        auxiliares.Relogio.configurarFonteBanco(conexao::conectar);
        Thread sincronizadorRelogio = new Thread(auxiliares.Relogio::sincronizar, "sincronizador-relogio");
        sincronizadorRelogio.setDaemon(true);
        sincronizadorRelogio.start();

        // Usuário / Login
        UsuarioStorage usuarioStorage = new UsuarioStorage(conexao);
        Login.setUsuarioStorage(usuarioStorage);

        // Questões e Provas
        QuestaoStorage questaoStorage = new QuestaoStorage(conexao);
        QuestaoRepositorio questaoRepositorio = new QuestaoRepositorio(questaoStorage);
        QuestaoServico questaoServico = new QuestaoServico(questaoRepositorio);

        ProvaStorage provaStorage = new ProvaStorage(conexao);
        storage.ProvaAlunoStorage provaAlunoStorage = new storage.ProvaAlunoStorage(conexao);
        storage.RespostaAlunoStorage respostaAlunoStorage = new storage.RespostaAlunoStorage(conexao);
        ProvaRepositorio provaRepositorio = new ProvaRepositorio(provaStorage);
        ProvaServico provaServico = new ProvaServico(provaRepositorio, questaoServico, provaAlunoStorage, respostaAlunoStorage);

        telas.questao.Questao.setQuestaoServico(questaoServico);
        telas.bancoquestoes.BancoQuestoes.setQuestaoServico(questaoServico);
        telas.prova.Prova.setProvaServico(provaServico);
        telas.prova.Prova.setQuestaoServico(questaoServico);
        telas.prova.Prova.setUsuarioStorage(usuarioStorage);

        telas.aluno.Aluno.setProvaServico(provaServico);
        telas.aluno.Aluno.setQuestaoServico(questaoServico);
        telas.aluno.Aluno.setUsuarioStorage(usuarioStorage);

        // O programa começa pelo menu de provas
        Application.launch(telas.login.Login.class , args);
    }
}
