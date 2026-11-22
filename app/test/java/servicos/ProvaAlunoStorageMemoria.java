package servicos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import modelos.ProvaAluno;

// Entregas de prova em memória para os testes de serviço (sem banco).
// Substitui o storage real sobrescrevendo os métodos que o ProvaServico usa.
class ProvaAlunoStorageMemoria extends storage.ProvaAlunoStorage {
    final Map<String, ProvaAluno> entregas = new LinkedHashMap<>();

    ProvaAlunoStorageMemoria() {
        super(null);
    }

    private static String chave(String codigoProva, String registroAluno) {
        return codigoProva + "|" + registroAluno;
    }

    @Override
    public ProvaAluno buscar(String codigoProva, String registroAluno) {
        return entregas.get(chave(codigoProva, registroAluno));
    }

    @Override
    public List<ProvaAluno> listarPorAluno(String registroAluno) {
        List<ProvaAluno> resultado = new ArrayList<>();
        for (ProvaAluno pa : entregas.values())
            if (registroAluno.equals(pa.getRegistroAluno()))
                resultado.add(pa);
        return resultado;
    }

    @Override
    public int contarEntregas(String codigoProva) {
        int total = 0;
        for (ProvaAluno pa : entregas.values())
            if (codigoProva.equals(pa.getCodigoProva()) && pa.isRealizada())
                total++;
        return total;
    }

    @Override
    public void registrarEntrega(ProvaAluno provaAluno) {
        entregas.put(chave(provaAluno.getCodigoProva(), provaAluno.getRegistroAluno()), provaAluno);
    }
}
