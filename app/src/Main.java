import interfaces.QuestaoRepositorio;
import interfaces.Storage;
import modelos.Questao;
import repositorios.TxtQuestaoRepositorio;
import servicos.QuestaoServico;
import storage.TxtStorage;
import telas.Home;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Composition Root
        Storage<Long, Questao> storage = new TxtStorage<>(
                "src/storage/save.txt",
                Main::linhaParaQuestao,
                Main::questaoParaLinha,
                Questao::getNumero
        );

        TxtQuestaoRepositorio questaoRepositorio =
                new TxtQuestaoRepositorio(storage);

        QuestaoServico questaoServico =
                new QuestaoServico(questaoRepositorio);

        // Renderização da tela de início
        Home home = new Home();

        System.out.println("Insira o número da questão: ");
        long numero = Long.parseLong(scanner.nextLine());

        System.out.println("Insira o título da questão: ");
        String titulo = scanner.nextLine();

        System.out.println("Insira a descrição da questão: ");
        String descricao = scanner.nextLine();

        System.out.println("Insira as alternativas da questão: ");
        String alternativas = scanner.nextLine();

        System.out.println("Insira a alternativa correta: ");
        String alternativaCorreta = scanner.nextLine();

        Questao questao = questaoServico.cadastrarQuestao(
                titulo,
                descricao,
                alternativas,
                alternativaCorreta
        );

        System.out.println("Questão cadastrada:");
        System.out.println(questao);

        scanner.close();
    }

    private static String questaoParaLinha(Questao questao) {

        return questao.getNumero() + ";" +
                questao.getTitulo() + ";" +
                questao.getDescricao() + ";" +
                questao.getAlternativas() + ";" +
                questao.getAlternativa_correta();
    }

    private static Questao linhaParaQuestao(String linha) {

        String[] campos = linha.split(";");

        return new Questao(
                Long.parseLong(campos[0]),
                campos[1],
                campos[2],
                campos[3],
                campos[4]
        );
    }
}