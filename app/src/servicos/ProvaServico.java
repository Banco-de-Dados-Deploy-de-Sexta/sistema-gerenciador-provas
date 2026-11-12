package servicos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import interfaces.ProvaRepositorio;
import modelos.Alternativa;
import modelos.PesoQuestao;
import modelos.Prova;
import modelos.ProvaAluno;
import modelos.RespostaAluno;
import modelos.Questao;
import modelos.QuestaoComPeso;
import modelos.ResultadoCorrecao;
import modelos.RespostaAluno;
import modelos.ProvaAluno;

public class ProvaServico {
    public static final int MAX_TITULO = 100;
    public static final int MAX_MATERIA = 50;
    public static final int MAX_CURSO = 50;

    private final ProvaRepositorio provaRepositorio;
    private final QuestaoServico questaoServico;
    private final storage.ProvaAlunoStorage provaAlunoStorage;
    private final storage.RespostaAlunoStorage respostaAlunoStorage;
    // Registro do professor logado (RN2): só o dono edita, exclui, agenda, compõe
    // e aplica a prova. Substituível nos testes por definirProfessorLogado.
    private Supplier<String> professorLogado = auxiliares.Sessao::getRegistroUsuarioLogado;

    public ProvaServico(ProvaRepositorio provaRepositorio, QuestaoServico questaoServico) {
        this(provaRepositorio, questaoServico, null, null);
    }

    public ProvaServico(ProvaRepositorio provaRepositorio, QuestaoServico questaoServico,
                        storage.ProvaAlunoStorage provaAlunoStorage,
                        storage.RespostaAlunoStorage respostaAlunoStorage) {
        this.provaRepositorio = provaRepositorio;
        this.questaoServico = questaoServico;
        this.provaAlunoStorage = provaAlunoStorage;
        this.respostaAlunoStorage = respostaAlunoStorage;
    }

    public void definirProfessorLogado(Supplier<String> fornecedor) {
        this.professorLogado = fornecedor == null ? () -> null : fornecedor;
    }

    public Prova criarProva(String titulo, String materia, LocalDate data, String curso, int bimestre,
            String registro_professor) {
        String codigo = gerarCodigo();
        return salvarProva(new Prova(codigo, titulo, materia, data, curso, bimestre, registro_professor));
    }

    public Prova salvarProva(Prova prova) {
        validar(prova);
        Prova atual = prova.getCodigo() == null || prova.getCodigo().isBlank()
                ? null
                : provaRepositorio.findByKey(prova.getCodigo());
        if (atual == null) {
            // Prova nova: nasce em nome do professor logado
            if (prova.getRegistroProfessor() == null || prova.getRegistroProfessor().isBlank())
                throw new IllegalArgumentException("A prova precisa estar vinculada ao professor logado.");
            if (prova.getCodigo() == null || prova.getCodigo().isBlank())
                prova.setCodigo(gerarCodigo());
        } else {
            garantirDona(atual, "editar esta prova");
            // A edição não transfere a prova para outro professor
            prova.setRegistroProfessor(atual.getRegistroProfessor());
            garantirAgendamentoInalteradoComEntregas(atual, prova);
        }
        return provaRepositorio.save(prova);
    }

    // Depois que algum aluno entregou a prova, a data/horário não pode mais mudar:
    // reagendar reabriria a prova (com as mesmas questões) para quem ainda não fez.
    private void garantirAgendamentoInalteradoComEntregas(Prova atual, Prova prova) {
        boolean mudouAgendamento = !java.util.Objects.equals(atual.getData(), prova.getData())
                || !java.util.Objects.equals(atual.getDataHora(), prova.getDataHora())
                || !java.util.Objects.equals(atual.getDataHoraFim(), prova.getDataHoraFim());
        if (mudouAgendamento)
            garantirSemEntregas(prova.getCodigo(), "reagendada");
    }

    public Prova buscarProva(String codigo) {
        return provaRepositorio.findByKey(codigo);
    }

    public List<Prova> listarProvas() {
        return provaRepositorio.findAll();
    }

    public void excluirProva(String codigo) {
        Prova prova = buscarProva(codigo);
        if (prova == null)
            throw new IllegalArgumentException("A prova não existe.");
        garantirDona(prova, "excluir esta prova");
        garantirNaoAplicada(codigo, "excluída");
        provaRepositorio.remove(codigo);
    }

    public List<QuestaoComPeso> listarQuestoes(String codigoProva) {
        List<QuestaoComPeso> resultado = new ArrayList<>();
        for (PesoQuestao vinculo : provaRepositorio.listarQuestoes(codigoProva)) {
            Questao questao = questaoServico.buscarQuestao(vinculo.getIdQuestao());
            if (questao != null)
                resultado.add(new QuestaoComPeso(questao, vinculo.getPeso()));
        }
        return resultado;
    }

    public int contarQuestoes(String codigoProva) {
        return provaRepositorio.listarQuestoes(codigoProva).size();
    }

    // Estado exibido nos detalhes da prova (PB05), derivado das questões vinculadas
    // e da data:
    // sem questões = Rascunho; sem data = Pronta; data de hoje em diante =
    // Agendada; data passada = Encerrada; aplicada a alunos = Aplicada
    public String estado(Prova prova) {
        if (aplicada(prova.getCodigo()))
            return "Aplicada";
        return estado(prova, contarQuestoes(prova.getCodigo()));
    }

    public String estado(Prova prova, int totalQuestoes) {
        if (totalQuestoes == 0) return "Rascunho";
        if (prova.getDataHora() != null) {
            return prova.getDataHora().isBefore(auxiliares.Relogio.agora()) ? "Encerrada" : "Agendada";
        }
        if (prova.getData() == null) return "Pronta";
        return prova.getData().isBefore(auxiliares.Relogio.hoje()) ? "Encerrada" : "Agendada";
    }

    public String estadoParaAluno(Prova prova) {
        java.time.LocalDateTime agora = auxiliares.Relogio.agora();
        java.time.LocalDateTime inicio = prova.getDataHora();
        java.time.LocalDateTime fim = prova.getDataHoraFim();

        if (inicio == null) {
            return "Agendada";
        }

        if (fim != null) {
            if (agora.isBefore(inicio)) {
                return "Agendada";
            } else if (!agora.isAfter(fim)) {
                return "Disponível";
            } else {
                return "Encerrada";
            }
        }

        // Sem horário de fim explícito: disponível durante todo o dia agendado
        LocalDate hoje = agora.toLocalDate();
        LocalDate diaProva = inicio.toLocalDate();
        if (diaProva.isAfter(hoje)) {
            return "Agendada";
        } else if (diaProva.isEqual(hoje)) {
            return agora.isBefore(inicio) ? "Agendada" : "Disponível";
        } else {
            return "Encerrada";
        }
    }

    public String estadoParaAluno(Prova prova, String registroAluno) {
        if (alunoJaRealizou(prova.getCodigo(), registroAluno)) {
            ProvaAluno pa = buscarRealizacaoAluno(prova.getCodigo(), registroAluno);
            if (pa != null && "CORRIGIDA".equalsIgnoreCase(pa.getStatus())) {
                return "Corrigida";
            }
            return "Realizada";
        }
        return estadoParaAluno(prova);
    }

    public void salvarQuestoes(String codigoProva, List<PesoQuestao> questoes) {
        Prova prova = buscarProva(codigoProva);
        if (prova == null)
            throw new IllegalArgumentException("A prova não existe.");
        garantirDona(prova, "alterar as questões desta prova");
        garantirNaoAplicada(codigoProva, "alterada");
        if (questoes == null)
            throw new IllegalArgumentException("Informe as questões da prova.");
        java.util.Set<Long> vistas = new java.util.HashSet<>();
        for (PesoQuestao item : questoes) {
            if (item == null)
                throw new IllegalArgumentException("Uma das questões selecionadas é inválida.");
            if (!vistas.add(item.getIdQuestao()))
                throw new IllegalArgumentException("Uma mesma questão não pode ser adicionada duas vezes à prova.");
            if (questaoServico.buscarQuestao(item.getIdQuestao()) == null)
                throw new IllegalArgumentException("Uma das questões selecionadas não existe.");
            if (!Double.isFinite(item.getPeso()) || item.getPeso() <= 0)
                throw new IllegalArgumentException("O peso deve ser um número maior que zero.");
        }
        provaRepositorio.salvarQuestoes(codigoProva, questoes);
    }

    public List<String> listarAlunos(String codigoProva) {
        return provaRepositorio.listarAlunos(codigoProva);
    }

    public List<Prova> listarProvasPorAluno(String registroAluno) {
        if (registroAluno == null || registroAluno.isBlank()) {
            return List.of();
        }
        return provaRepositorio.listarPorAluno(registroAluno);
    }

    // Aplica a prova aos alunos informados (PB07): só o professor dono, exige ao
    // menos uma questão, todas com gabarito, e ignora registros repetidos ou vazios
    public void salvarAlunos(String codigoProva, List<String> registros) {
        Prova prova = buscarProva(codigoProva);
        if (prova == null)
            throw new IllegalArgumentException("A prova não existe.");
        garantirDona(prova, "aplicar esta prova");
        List<String> unicos = new ArrayList<>();
        if (registros != null) {
            for (String registro : new LinkedHashSet<>(registros)) {
                if (registro != null && !registro.isBlank() && !unicos.contains(registro.trim()))
                    unicos.add(registro.trim());
            }
        }
        if (!unicos.isEmpty()) {
            if (contarQuestoes(codigoProva) == 0)
                throw new IllegalArgumentException("A prova precisa ter pelo menos uma questão antes de ser aplicada.");
            garantirQuestoesCompletas(codigoProva);
        }
        provaRepositorio.salvarAlunos(codigoProva, unicos);
    }

    // Regra da PB07: toda questão da prova precisa estar completa (gabarito
    // definido) antes da aplicação
    private void garantirQuestoesCompletas(String codigoProva) {
        for (QuestaoComPeso item : listarQuestoes(codigoProva)) {
            if (!questaoCompleta(item.getQuestao()))
                throw new IllegalArgumentException("A questão #" + item.getQuestao().getNumero()
                        + " não tem gabarito definido. Complete-a antes de aplicar a prova.");
        }
    }

    // O tipo da questão fica guardado na descrição (ver QuestaoController):
    // correspondência exige a definição de cada par; V/F em sequência guarda um
    // booleano por afirmação (sempre completa); múltipla escolha exige uma correta.
    static boolean questaoCompleta(Questao questao) {
        if (questao == null)
            return false;
        List<Alternativa> alternativas = questao.getAlternativas();
        if (alternativas == null || alternativas.isEmpty())
            return false;
        String tipo = questao.getDescricao() == null ? "" : questao.getDescricao().trim();
        if ("Correspondência".equalsIgnoreCase(tipo))
            return alternativas.stream()
                    .allMatch(a -> a.getTextoCorrespondencia() != null && !a.getTextoCorrespondencia().isBlank());
        if ("Verdadeiro ou falso em sequência".equalsIgnoreCase(tipo))
            return true;
        return alternativas.stream().anyMatch(Alternativa::isCorreta);
    }

    private void garantirDona(Prova prova, String acao) {
        String logado = professorLogado.get();
        String dona = prova.getRegistroProfessor();
        if (logado == null || logado.isBlank() || dona == null || !dona.equals(logado))
            throw new IllegalArgumentException("Só o professor dono da prova pode " + acao + ".");
    }

    public boolean aplicada(String codigoProva) {
        return !listarAlunos(codigoProva).isEmpty();
    }

    private void garantirNaoAplicada(String codigoProva, String acao) {
        if (aplicada(codigoProva))
            throw new IllegalArgumentException("A prova já foi aplicada a alunos e não pode mais ser " + acao + ".");
    }

    // Indica se ao menos um aluno já entregou a prova
    public boolean temEntregas(String codigoProva) {
        if (provaAlunoStorage == null || codigoProva == null)
            return false;
        return provaAlunoStorage.contarEntregas(codigoProva) > 0;
    }

    private void garantirSemEntregas(String codigoProva, String acao) {
        if (temEntregas(codigoProva))
            throw new IllegalArgumentException("Alunos já entregaram esta prova e ela não pode mais ser " + acao + ".");
    }

    public ResultadoCorrecao corrigir(String codigoProva, Map<Long, Integer> respostas) {
        List<QuestaoComPeso> questoes = listarQuestoes(codigoProva);
        double possiveis = 0;
        double obtidos = 0;
        for (QuestaoComPeso item : questoes) {
            possiveis += item.getPeso();
            Integer resposta = respostas.get(item.getQuestao().getNumero());
            if (resposta == null)
                continue;
            boolean correta = item.getQuestao().getAlternativas().stream()
                    .anyMatch(a -> a.getId() == resposta && a.isCorreta());
            if (correta)
                obtidos += item.getPeso();
        }
        return new ResultadoCorrecao(obtidos, possiveis);
    }

    public ProvaAluno buscarRealizacaoAluno(String codigoProva, String registroAluno) {
        if (provaAlunoStorage == null || codigoProva == null || registroAluno == null) {
            return null;
        }
        return provaAlunoStorage.buscar(codigoProva, registroAluno);
    }

    public boolean alunoJaRealizou(String codigoProva, String registroAluno) {
        ProvaAluno pa = buscarRealizacaoAluno(codigoProva, registroAluno);
        return pa != null && pa.isRealizada();
    }

    public List<RespostaAluno> listarRespostasAluno(String codigoProva, String registroAluno) {
        if (respostaAlunoStorage == null || codigoProva == null || registroAluno == null) {
            return List.of();
        }
        return respostaAlunoStorage.listarPorAlunoEProva(codigoProva, registroAluno);
    }

    // Regra da PB09: entrega de prova com tentativa única e correção automática
    public void entregarProva(String codigoProva, String registroAluno, List<RespostaAluno> respostas) {
        Prova prova = buscarProva(codigoProva);
        if (prova == null) {
            throw new IllegalArgumentException("Prova não encontrada.");
        }

        if (alunoJaRealizou(codigoProva, registroAluno)) {
            throw new IllegalStateException("Esta prova já foi realizada e entregue anteriormente.");
        }

        List<QuestaoComPeso> questoesComPeso = listarQuestoes(codigoProva);
        double pesoTotal = questoesComPeso.stream().mapToDouble(QuestaoComPeso::getPeso).sum();
        double pesoObtido = 0.0;
        boolean temDissertativa = false;

        // Correção das questões
        for (RespostaAluno resp : respostas) {
            resp.setCodigoProva(codigoProva);
            resp.setRegistroAluno(registroAluno);

            QuestaoComPeso qcp = questoesComPeso.stream()
                    .filter(q -> q.getQuestao().getNumero() == resp.getIdQuestao())
                    .findFirst().orElse(null);

            if (qcp == null) continue;

            if (resp.getIdAlternativaEscolhida() != null) {
                // Questão de múltipla escolha
                boolean acertou = qcp.getQuestao().getAlternativas().stream()
                        .anyMatch(alt -> alt.getId() == resp.getIdAlternativaEscolhida() && alt.isCorreta());
                resp.setAcertou(acertou);
                double notaQuestao = acertou ? qcp.getPeso() : 0.0;
                resp.setNotaObtida(notaQuestao);
                if (acertou) {
                    pesoObtido += qcp.getPeso();
                }
            } else if (resp.getRespostaTexto() != null && !resp.getRespostaTexto().isBlank()) {
                // Dissertativa ou correspondência (correção manual ou pendente)
                temDissertativa = true;
                resp.setAcertou(null);
                resp.setNotaObtida(null);
            } else {
                // Em branco
                resp.setAcertou(false);
                resp.setNotaObtida(0.0);
            }
        }

        if (respostaAlunoStorage != null) {
            respostaAlunoStorage.salvarRespostas(respostas);
        }

        Double notaFinal = null;
        String status = "REALIZADA";
        if (!temDissertativa && pesoTotal > 0) {
            notaFinal = Math.round(((pesoObtido / pesoTotal) * 10.0) * 100.0) / 100.0;
            status = "CORRIGIDA";
        }

        if (provaAlunoStorage != null) {
            ProvaAluno pa = new ProvaAluno(
                    codigoProva,
                    registroAluno,
                    auxiliares.Relogio.agora(),
                    notaFinal,
                    status
            );
            provaAlunoStorage.registrarEntrega(pa);
        }
    }

    private String gerarCodigo() {
        int proximo = provaRepositorio.findAll().stream()
                .map(Prova::getCodigo)
                .filter(codigo -> codigo != null && codigo.matches("PRV[0-9]{3,}"))
                .mapToInt(codigo -> Integer.parseInt(codigo.substring(3)))
                .max().orElse(0) + 1;
        if (proximo > 9999)
            throw new IllegalStateException("Limite de provas cadastradas atingido.");
        return "PRV%03d".formatted(proximo);
    }

    private void validar(Prova prova) {
        if (prova == null)
            throw new IllegalArgumentException("Informe os dados da prova.");
        if (prova.getTitulo() == null || prova.getTitulo().isBlank())
            throw new IllegalArgumentException("Informe o título.");
        if (prova.getTitulo().length() > MAX_TITULO)
            throw new IllegalArgumentException("O título deve ter até " + MAX_TITULO + " caracteres.");
        if (prova.getMateria() == null || prova.getMateria().isBlank() || prova.getMateria().length() > MAX_MATERIA)
            throw new IllegalArgumentException("Informe a matéria (até " + MAX_MATERIA + " caracteres).");
        if (prova.getCurso() != null && prova.getCurso().length() > MAX_CURSO)
            throw new IllegalArgumentException("O curso deve ter até " + MAX_CURSO + " caracteres.");
        if (prova.getBimestre() < 1 || prova.getBimestre() > 4)
            throw new IllegalArgumentException("O bimestre deve estar entre 1 e 4.");
        LocalDateTime inicio = prova.getDataHora();
        LocalDateTime fim = prova.getDataHoraFim();
        if (inicio == null) {
            if (fim != null)
                throw new IllegalArgumentException("Informe a data e o horário de início antes do término.");
            return;
        }
        // Prova salva só com a data (sem horário) fica em 00:00: valida apenas o dia,
        // senão uma prova de hoje seria recusada como "horário que já passou".
        boolean somenteData = inicio.toLocalTime().equals(java.time.LocalTime.MIDNIGHT);
        if (somenteData) {
            validarDataAgendamento(inicio.toLocalDate());
            if (fim != null)
                throw new IllegalArgumentException("Informe o horário de início para definir o horário de término.");
            return;
        }
        validarAgendamento(inicio);
        if (fim != null) {
            if (!fim.toLocalDate().equals(inicio.toLocalDate()))
                throw new IllegalArgumentException("O término da prova deve ser no mesmo dia do início.");
            if (!fim.isAfter(inicio))
                throw new IllegalArgumentException("O horário de término deve ser posterior ao horário de início.");
        }
    }

    public void validarDataAgendamento(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("Informe a data da prova.");
        }
        if (data.isBefore(auxiliares.Relogio.hoje())) {
            throw new IllegalArgumentException("Não é possível agendar para uma data passada.");
        }
        java.time.DayOfWeek diaSemana = data.getDayOfWeek();
        if (diaSemana == java.time.DayOfWeek.SATURDAY || diaSemana == java.time.DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("A data da prova deve ser em um dia útil (segunda a sexta-feira).");
        }
        LocalDate hoje = auxiliares.Relogio.hoje();
        int semestreAtual = hoje.getMonthValue() <= 6 ? 1 : 2;
        int semestreData = data.getMonthValue() <= 6 ? 1 : 2;
        if (data.getYear() != hoje.getYear() || semestreData != semestreAtual) {
            throw new IllegalArgumentException(
                    "A prova deve ser agendada no semestre letivo atual (" + semestreAtual + "º semestre de " + hoje.getYear() + ").");
        }
    }

    public void validarAgendamento(String codigoProva, java.time.LocalDateTime dataHora) {
        Prova prova = buscarProva(codigoProva);
        if (prova == null)
            throw new IllegalArgumentException("A prova não existe.");
        garantirDona(prova, "agendar esta prova");
        garantirSemEntregas(codigoProva, "reagendada");
        validarAgendamento(dataHora);
    }

    public void validarAgendamento(java.time.LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new IllegalArgumentException("Informe a data e o horário da prova.");
        }
        validarDataAgendamento(dataHora.toLocalDate());
        if (dataHora.isBefore(auxiliares.Relogio.agora())) {
            throw new IllegalArgumentException("Não é possível agendar para um horário que já passou.");
        }
    }
}
