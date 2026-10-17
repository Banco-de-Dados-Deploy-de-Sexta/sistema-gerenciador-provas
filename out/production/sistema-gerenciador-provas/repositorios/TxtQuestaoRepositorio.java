package repositorios;

import modelos.Questao;
import interfaces.QuestaoRepositorio;
import interfaces.Storage;
import java.util.List;

public class TxtQuestaoRepositorio implements QuestaoRepositorio {

    private final Storage<Long, Questao> storage;

    public TxtQuestaoRepositorio(Storage<Long, Questao> storage) {
        this.storage = storage;
    }

    @Override
    public Questao findByKey(Long numero) {
        return storage.get(numero);
    }

    @Override
    public List<Questao> findAll() {
        return storage.getAll();
    }

    @Override
    public Questao save(Questao questao) {
        return storage.set(questao.getNumero(), questao);
    }

    @Override
    public void remove(Long numero) {
        storage.delete(numero);
    }

    @Override
    public void removeAll() {
        storage.deleteAll();
    }

}