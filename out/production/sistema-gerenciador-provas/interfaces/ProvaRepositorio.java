package interfaces;

import modelos.Prova;
import modelos.PesoQuestao;

import java.util.List;

public interface ProvaRepositorio {

    Prova findByKey(String codigo);

    List<Prova> findAll();

    Prova save(Prova prova);

    void remove(String codigo);

    void removeAll();

    List<PesoQuestao> listarQuestoes(String codigo);

    void salvarQuestoes(String codigo, List<PesoQuestao> questoes);

}
