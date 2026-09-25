package servicos;

import interfaces.ProvaRepositorio;
import modelos.Prova;

import java.time.LocalDate;
import java.util.List;

public class ProvaServico {

    private final ProvaRepositorio provaRepositorio;

    public ProvaServico(ProvaRepositorio provaRepositorio) {
        this.provaRepositorio = provaRepositorio;
    }

    public Prova cadastrarProva(String titulo, String materia, LocalDate data, String curso, int bimestre) {
        //Aqui a gente cria uma instância, não está salvando no banco ainda
        Prova prova = new Prova(titulo, materia, data, curso, bimestre);

        long proximoCodigo = provaRepositorio.findAll().stream()
                .mapToLong(Prova::getCodigo)
                .max()
                .orElse(0) + 1;

        prova.setCodigo(proximoCodigo);

        return provaRepositorio.save(prova);
    }

    public List<Prova> listarProvas() {
        return provaRepositorio.findAll();
    }

}