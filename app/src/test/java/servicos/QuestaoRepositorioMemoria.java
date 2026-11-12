package servicos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import modelos.Questao;

// Repositório de questões em memória para os testes de serviço (sem banco)
class QuestaoRepositorioMemoria implements interfaces.QuestaoRepositorio {
    final Map<Long, Questao> questoes = new LinkedHashMap<>();

    @Override
    public Questao findByKey(Long numero) {
        return questoes.get(numero);
    }

    @Override
    public List<Questao> findAll() {
        return new ArrayList<>(questoes.values());
    }

    @Override
    public Questao save(Questao questao) {
        questoes.put(questao.getNumero(), questao);
        return questao;
    }

    @Override
    public void remove(Long numero) {
        questoes.remove(numero);
    }

    @Override
    public void removeAll() {
        questoes.clear();
    }

    @Override
    public boolean estaEmAlgumaProva(Long numero) {
        return false;
    }
}
