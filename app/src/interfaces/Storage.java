package interfaces;

import java.util.List;

public interface Storage<K, V> {

    V get(K key);

    List<V> getAll();

    V set(K key, V value);

    void delete(K key);

    void deleteAll();

}