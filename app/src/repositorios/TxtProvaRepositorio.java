package repositorios;

import modelos.Prova;
import interfaces.ProvaRepositorio;
import interfaces.Storage;
import java.util.List;

public class TxtProvaRepositorio implements ProvaRepositorio {

    private final Storage<Long, Prova> storage;

    public TxtProvaRepositorio(Storage<Long, Prova> storage) {
        this.storage = storage;
    }

    @Override
    public Prova findByKey(Long codigo) {
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
    public void remove(Long codigo) {
        storage.delete(codigo);
    }

    @Override
    public void removeAll() {
        storage.deleteAll();
    }

}