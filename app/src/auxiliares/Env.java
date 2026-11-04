package auxiliares;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Env {

    public static Map<String, String> carregar(String caminho) {
        Map<String, String> valores = new HashMap<>();

        Path path = resolverCaminho(caminho);

        try {
            for (String linha : Files.readAllLines(path)) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#") || !linha.contains("=")) {
                    continue;
                }

                int posicao = linha.indexOf('=');
                String chave = linha.substring(0, posicao).trim();
                String valor = linha.substring(posicao + 1).trim();

                // Remove aspas ao redor do valor
                if (valor.length() >= 2
                        && ((valor.startsWith("\"") && valor.endsWith("\""))
                        || (valor.startsWith("'") && valor.endsWith("'")))) {
                    valor = valor.substring(1, valor.length() - 1);
                }

                valores.put(chave, valor);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o arquivo " + caminho, e);
        }

        return valores;
    }

    private static Path resolverCaminho(String caminho) {
        Path p = Path.of(caminho);
        if (Files.exists(p)) {
            return p;
        }

        for (String tentativa : List.of(caminho, "app/" + caminho, "../" + caminho)) {
            Path candidato = Path.of(tentativa);
            if (Files.exists(candidato)) {
                return candidato;
            }
        }

        return p;
    }
}