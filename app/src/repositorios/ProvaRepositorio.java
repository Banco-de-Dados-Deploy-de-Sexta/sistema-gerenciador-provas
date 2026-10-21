package repositorios;

import modelos.Prova;
import modelos.PesoQuestao;
import storage.ProvaStorage;
import java.util.List;

public class ProvaRepositorio implements interfaces.ProvaRepositorio {

    private final ProvaStorage storage;

    public ProvaRepositorio(ProvaStorage storage) {
        this.storage = storage;
    }

    @Override
    public Prova findByKey(String codigo) {
        return storage.get(codigo);
    }

    @Override
    public List<Prova> findAll() {
        return storage.getAll();
    }

    @Override
    public Prova save(Prova prova) {
        return storage.set(prova.getCodigo(), prova);
    }

    @Override
    public void remove(String codigo) {
        storage.delete(codigo);
    }

    @Override
    public void removeAll() {
        storage.deleteAll();
    }

    @Override
    public List<PesoQuestao> listarQuestoes(String codigo) {
        return storage.listarQuestoes(codigo);
    }

    @Override
    public void salvarQuestoes(String codigo, List<PesoQuestao> questoes) {
        storage.salvarQuestoes(codigo, questoes);
    }

}
