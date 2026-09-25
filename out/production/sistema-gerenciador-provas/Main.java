import modelos.Prova;
import telas.Home;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // Renderização da tela de início
        Home home = new Home();

        // Instanciando uma Prova
        Prova prova = new Prova(
                1L,
                "Prova de Java",
                "Programação Orientada a Objetos",
                LocalDate.of(2026, 10, 15),
                "Banco de Dados",
                2
        );

        System.out.println(prova);
    }
}