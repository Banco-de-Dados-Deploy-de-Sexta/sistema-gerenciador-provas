package interfaces;

import modelos.Prova;

import java.util.List;

public interface ProvaRepositorio {

    Prova findByKey(Long codigo);

    List<Prova> findAll();

    Prova save(Prova prova);

    void remove(Long codigo);

    void removeAll();

}