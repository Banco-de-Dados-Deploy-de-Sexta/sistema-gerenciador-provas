package storage;

import interfaces.Storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TxtStorage<K, V> implements Storage<K, V> {

    private final Path arquivo;
    private final Function<String, V> linhaParaValor;
    private final Function<V, String> valorParaLinha;
    private final Function<V, K> extratorChave;

    public TxtStorage(String caminhoArquivo, Function<String, V> linhaParaValor, Function<V, String> valorParaLinha, Function<V, K> extratorChave) {
        this.arquivo = Paths.get(caminhoArquivo);
        this.linhaParaValor = linhaParaValor;
        this.valorParaLinha = valorParaLinha;
        this.extratorChave = extratorChave;
        criarArquivoSeNaoExistir();
    }

    private void criarArquivoSeNaoExistir() {
        try {
            if (!Files.exists(arquivo)) {
                Files.createFile(arquivo);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar arquivo de armazenamento", e);
        }
    }

    @Override
    public V get(K key) {
        return getAll().stream()
                .filter(v -> extratorChave.apply(v).equals(key))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<V> getAll() {
        List<V> valores = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(arquivo)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    valores.add(linhaParaValor.apply(linha));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo de armazenamento", e);
        }
        return valores;
    }

    @Override
    public V set(K key, V value) {
        List<V> valores = getAll();
        valores.removeIf(v -> extratorChave.apply(v).equals(key));
        valores.add(value);
        reescreverArquivo(valores);
        return value;
    }

    @Override
    public void delete(K key) {
        List<V> valores = getAll();
        valores.removeIf(v -> extratorChave.apply(v).equals(key));
        reescreverArquivo(valores);
    }

    @Override
    public void deleteAll() {
        reescreverArquivo(new ArrayList<>());
    }

    private void reescreverArquivo(List<V> valores) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                arquivo, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (V valor : valores) {
                writer.write(valorParaLinha.apply(valor));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao reescrever arquivo de armazenamento", e);
        }
    }

}