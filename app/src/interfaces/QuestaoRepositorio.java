package interfaces;

import modelos.Questao;

import java.util.List;

public interface QuestaoRepositorio {

    Questao findByKey(Long numero);

    List<Questao> findAll();

    Questao save(Questao questao);

    void remove(Long numero);

    void removeAll();
}
