package servicos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import modelos.PesoQuestao;
import modelos.Prova;

// Repositório de provas em memória para os testes de serviço (sem banco)
class ProvaRepositorioMemoria implements interfaces.ProvaRepositorio {
    final Map<String, Prova> provas = new LinkedHashMap<>();
    final Map<String, List<PesoQuestao>> questoes = new HashMap<>();
    final Map<String, List<String>> alunos = new HashMap<>();

    @Override
    public Prova findByKey(String codigo) {
        return provas.get(codigo);
    }

    @Override
    public List<Prova> findAll() {
        return new ArrayList<>(provas.values());
    }

    @Override
    public Prova save(Prova prova) {
        provas.put(prova.getCodigo(), prova);
        return prova;
    }

    @Override
    public void remove(String codigo) {
        provas.remove(codigo);
        questoes.remove(codigo);
        alunos.remove(codigo);
    }

    @Override
    public void removeAll() {
        provas.clear();
        questoes.clear();
        alunos.clear();
    }

    @Override
    public List<PesoQuestao> listarQuestoes(String codigo) {
        return new ArrayList<>(questoes.getOrDefault(codigo, List.of()));
    }

    @Override
    public void salvarQuestoes(String codigo, List<PesoQuestao> lista) {
        questoes.put(codigo, new ArrayList<>(lista));
    }

    @Override
    public List<String> listarAlunos(String codigo) {
        return new ArrayList<>(alunos.getOrDefault(codigo, List.of()));
    }

    @Override
    public void salvarAlunos(String codigo, List<String> registros) {
        alunos.put(codigo, new ArrayList<>(registros));
    }

    @Override
    public List<Prova> listarPorAluno(String registroAluno) {
        List<Prova> resultado = new ArrayList<>();
        for (Map.Entry<String, List<String>> entrada : alunos.entrySet())
            if (entrada.getValue().contains(registroAluno) && provas.containsKey(entrada.getKey()))
                resultado.add(provas.get(entrada.getKey()));
        return resultado;
    }
}
