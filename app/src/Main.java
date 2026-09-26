import interfaces.ProvaRepositorio;
import interfaces.Storage;
import modelos.Prova;
import repositorios.TxtProvaRepositorio;
import servicos.ProvaServico;
import storage.TxtStorage;
import telas.Home;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //Composition Root
        Storage<Long, Prova> storage = new TxtStorage<>(
                "src/storage/save.txt",
                Main::linhaParaProva,
                Main::provaParaLinha,
                Prova::getCodigo
        );
        ProvaRepositorio provaRepositorio = new TxtProvaRepositorio(storage);
        ProvaServico provaServico = new ProvaServico(provaRepositorio);

        // Renderização da tela de início
        Home home = new Home();

        System.out.println("Insira o título da prova: ");
        String titulo = scanner.nextLine();

        System.out.println("Insira a matéria da prova: ");
        String materia = scanner.nextLine();

        System.out.println("Insira a data da prova (dd/MM/yyyy): ");
        String entradaData = scanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(entradaData, formatter);

        System.out.println("Insira o curso da prova: ");
        String curso = scanner.nextLine();

        System.out.println("Insira o bimestre da prova (1 a 4): ");
        int bimestre = Integer.parseInt(scanner.nextLine());

        Prova prova = provaServico.cadastrarProva(titulo, materia, data, curso, bimestre);

        System.out.println("Prova cadastrada:");
        System.out.println(prova);

        scanner.close();
    }

    private static String provaParaLinha(Prova prova) {
        return prova.getCodigo() + ";" +
                prova.getTitulo() + ";" +
                prova.getMateria() + ";" +
                prova.getData() + ";" +
                prova.getCurso() + ";" +
                prova.getBimestre();
    }

    private static Prova linhaParaProva(String linha) {
        String[] campos = linha.split(";");
        Prova prova = new Prova(
                campos[1],
                campos[2],
                LocalDate.parse(campos[3]),
                campos[4],
                Integer.parseInt(campos[5])
        );
        prova.setCodigo(Long.parseLong(campos[0]));
        return prova;
    }
}