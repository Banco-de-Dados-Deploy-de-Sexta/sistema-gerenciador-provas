package repositorios;

import modelos.Questao;
import storage.QuestaoStorage;
import java.util.List;

public class QuestaoRepositorio implements interfaces.QuestaoRepositorio {

    private final QuestaoStorage storage;

    public QuestaoRepositorio(QuestaoStorage storage) {
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

    @Override
    public boolean estaEmAlgumaProva(Long numero) {
        return storage.estaEmAlgumaProva(numero);
    }

}
