package interfaces;

import java.util.List;

import modelos.PesoQuestao;
import modelos.Prova;

public interface ProvaRepositorio {

    Prova findByKey(String codigo);

    List<Prova> findAll();

    Prova save(Prova prova);

    void remove(String codigo);

    void removeAll();

    List<PesoQuestao> listarQuestoes(String codigo);

    void salvarQuestoes(String codigo, List<PesoQuestao> questoes);

    List<String> listarAlunos(String codigo);

    void salvarAlunos(String codigo, List<String> registros);

}
