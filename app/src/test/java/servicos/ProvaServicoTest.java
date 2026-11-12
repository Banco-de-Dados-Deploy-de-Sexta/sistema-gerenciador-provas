package servicos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import auxiliares.Relogio;
import modelos.Alternativa;
import modelos.PesoQuestao;
import modelos.Prova;
import modelos.ProvaAluno;
import modelos.Questao;

// Casos de teste das regras de negócio de prova (PB02, PB03, PB04, PB07, PB08,
// PB14) sem banco de dados: repositórios em memória e professor logado fixo.
class ProvaServicoTest {

    private static final String PROFESSOR = "PROF001";
    private static final String OUTRO_PROFESSOR = "PROF002";

    private ProvaRepositorioMemoria provas;
    private QuestaoRepositorioMemoria questoes;
    private ProvaServico servico;

    @BeforeEach
    void preparar() {
        provas = new ProvaRepositorioMemoria();
        questoes = new QuestaoRepositorioMemoria();
        servico = new ProvaServico(provas, new QuestaoServico(questoes));
        servico.definirProfessorLogado(() -> PROFESSOR);
    }

    // ---- auxiliares ---------------------------------------------------------

    private Prova novaProva() {
        return servico.criarProva("Prova de teste", "Matemática", (LocalDate) null, "ADS", 1, PROFESSOR);
    }

    private Questao multiplaEscolha(long numero, boolean comCorreta) {
        return questoes.save(new Questao(numero, "Enunciado da questão " + numero, "Múltipla escolha", List.of(
                new Alternativa(1, "a", comCorreta),
                new Alternativa(2, "b", false),
                new Alternativa(3, "c", false),
                new Alternativa(4, "d", false))));
    }

    private Questao correspondencia(long numero, boolean completa) {
        return questoes.save(new Questao(numero, "Associe as colunas " + numero, "Correspondência", List.of(
                new Alternativa(1, "Item 1", "Definição 1", false),
                new Alternativa(2, "Item 2", completa ? "Definição 2" : "  ", false))));
    }

    private Questao verdadeiroFalso(long numero) {
        return questoes.save(new Questao(numero, "Julgue as afirmações " + numero, "Verdadeiro ou falso em sequência", List.of(
                new Alternativa(1, "Afirmação 1", false),
                new Alternativa(2, "Afirmação 2", false))));
    }

    private void compor(Prova prova, long... ids) {
        List<PesoQuestao> lista = new ArrayList<>();
        for (long id : ids)
            lista.add(new PesoQuestao(id, 1.0));
        servico.salvarQuestoes(prova.getCodigo(), lista);
    }

    private Prova provaPronta() {
        Prova prova = novaProva();
        multiplaEscolha(1, true);
        compor(prova, 1);
        return prova;
    }

    private static boolean diaUtil(LocalDate data) {
        return data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    private static boolean mesmoSemestre(LocalDate a, LocalDate b) {
        int sa = a.getMonthValue() <= 6 ? 1 : 2;
        int sb = b.getMonthValue() <= 6 ? 1 : 2;
        return a.getYear() == b.getYear() && sa == sb;
    }

    // Próximo dia útil depois de hoje dentro do semestre letivo; null se não houver
    private static LocalDate proximoDiaUtil() {
        LocalDate hoje = Relogio.hoje();
        for (LocalDate d = hoje.plusDays(1); mesmoSemestre(hoje, d); d = d.plusDays(1))
            if (diaUtil(d))
                return d;
        return null;
    }

    private static LocalDate proximoFimDeSemana() {
        LocalDate hoje = Relogio.hoje();
        for (LocalDate d = hoje.plusDays(1); mesmoSemestre(hoje, d); d = d.plusDays(1))
            if (!diaUtil(d))
                return d;
        return null;
    }

    private static String mensagem(Executable acao) {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, acao::executar);
        return erro.getMessage();
    }

    @FunctionalInterface
    private interface Executable {
        void executar();
    }

    // ---- criação e código --------------------------------------------------

    @Nested
    @DisplayName("Criar prova (PB02)")
    class CriarProva {

        @Test
        @DisplayName("gera códigos sequenciais PRV001, PRV002, ... sem sobrescrever")
        void codigoSequencial() {
            Prova primeira = novaProva();
            Prova segunda = novaProva();
            Prova terceira = novaProva();
            assertEquals("PRV001", primeira.getCodigo());
            assertEquals("PRV002", segunda.getCodigo());
            assertEquals("PRV003", terceira.getCodigo());
            assertEquals(3, provas.findAll().size());
        }

        @Test
        @DisplayName("continua a sequência a partir do maior código existente")
        void continuaSequencia() {
            provas.save(new Prova("PRV007", "Antiga", "Física", (LocalDate) null, null, 1, PROFESSOR));
            assertEquals("PRV008", novaProva().getCodigo());
        }

        @Test
        @DisplayName("rejeita título vazio ou acima do limite")
        void titulo() {
            assertEquals("Informe o título.",
                    mensagem(() -> servico.criarProva("   ", "Matemática", (LocalDate) null, null, 1, PROFESSOR)));
            String longo = "x".repeat(ProvaServico.MAX_TITULO + 1);
            assertTrue(mensagem(() -> servico.criarProva(longo, "Matemática", (LocalDate) null, null, 1, PROFESSOR))
                    .contains("título"));
        }

        @Test
        @DisplayName("rejeita matéria vazia ou acima de 50 caracteres")
        void materia() {
            assertTrue(mensagem(() -> servico.criarProva("Prova", "", (LocalDate) null, null, 1, PROFESSOR))
                    .contains("matéria"));
            assertTrue(mensagem(() -> servico.criarProva("Prova", "m".repeat(51), (LocalDate) null, null, 1, PROFESSOR))
                    .contains("matéria"));
        }

        @Test
        @DisplayName("rejeita curso acima de 50 caracteres")
        void curso() {
            assertTrue(mensagem(() -> servico.criarProva("Prova", "Matemática", (LocalDate) null, "c".repeat(51), 1, PROFESSOR))
                    .contains("curso"));
        }

        @Test
        @DisplayName("rejeita bimestre fora de 1 a 4")
        void bimestre() {
            for (int bimestre : new int[] { 0, 5, -1, Integer.MAX_VALUE })
                assertTrue(mensagem(() -> servico.criarProva("Prova", "Matemática", (LocalDate) null, null, bimestre, PROFESSOR))
                        .contains("bimestre"), "bimestre " + bimestre);
        }

        @Test
        @DisplayName("rejeita prova nova sem professor")
        void semProfessor() {
            assertTrue(mensagem(() -> servico.criarProva("Prova", "Matemática", (LocalDate) null, null, 1, null))
                    .contains("professor"));
            assertTrue(mensagem(() -> servico.criarProva("Prova", "Matemática", (LocalDate) null, null, 1, "  "))
                    .contains("professor"));
        }
    }

    // ---- agendamento -------------------------------------------------------

    @Nested
    @DisplayName("Agendar prova (PB08)")
    class Agendar {

        @Test
        @DisplayName("aceita prova só com a data de hoje (sem horário)")
        void somenteDataDeHoje() {
            LocalDate hoje = Relogio.hoje();
            assumeTrue(diaUtil(hoje), "hoje não é dia útil");
            Prova prova = assertDoesNotThrow(
                    () -> servico.criarProva("Prova", "Matemática", hoje, null, 1, PROFESSOR));
            assertEquals(hoje, prova.getData());
        }

        @Test
        @DisplayName("rejeita data passada")
        void dataPassada() {
            LocalDate ontem = Relogio.hoje().minusDays(1);
            assertTrue(mensagem(() -> servico.criarProva("Prova", "Matemática", ontem, null, 1, PROFESSOR))
                    .contains("passada"));
        }

        @Test
        @DisplayName("rejeita fim de semana")
        void fimDeSemana() {
            LocalDate sabadoOuDomingo = proximoFimDeSemana();
            assumeTrue(sabadoOuDomingo != null, "sem fim de semana restante no semestre");
            assertTrue(mensagem(() -> servico.criarProva("Prova", "Matemática", sabadoOuDomingo, null, 1, PROFESSOR))
                    .contains("dia útil"));
        }

        @Test
        @DisplayName("rejeita data fora do semestre letivo atual")
        void outroSemestre() {
            LocalDate candidata = Relogio.hoje().plusYears(1).withMonth(3).withDayOfMonth(2);
            while (!diaUtil(candidata))
                candidata = candidata.plusDays(1);
            LocalDate proximoAno = candidata;
            assertTrue(mensagem(() -> servico.validarDataAgendamento(proximoAno)).contains("semestre"));
        }

        @Test
        @DisplayName("rejeita horário de hoje que já passou")
        void horarioPassado() {
            LocalDateTime agora = Relogio.agora();
            assumeTrue(diaUtil(agora.toLocalDate()) && agora.toLocalTime().isAfter(LocalTime.of(0, 5)),
                    "muito cedo ou fim de semana");
            LocalDateTime antes = agora.minusMinutes(2);
            assertTrue(mensagem(() -> servico.validarAgendamento(antes)).contains("já passou"));
        }

        @Test
        @DisplayName("rejeita término antes do início e término sem início")
        void terminoInvalido() {
            LocalDate dia = proximoDiaUtil();
            assumeTrue(dia != null, "sem dia útil restante no semestre");
            LocalDateTime inicio = dia.atTime(10, 0);
            assertTrue(mensagem(() -> servico.salvarProva(
                    new Prova(null, "Prova", "Matemática", inicio, inicio.minusHours(1), null, 1, PROFESSOR)))
                    .contains("término"));
            assertTrue(mensagem(() -> servico.salvarProva(
                    new Prova(null, "Prova", "Matemática", inicio, inicio, null, 1, PROFESSOR)))
                    .contains("término"));
            assertTrue(mensagem(() -> servico.salvarProva(
                    new Prova(null, "Prova", "Matemática", dia.atStartOfDay(), dia.atTime(9, 0), null, 1, PROFESSOR)))
                    .contains("início"));
            assertTrue(mensagem(() -> servico.salvarProva(
                    new Prova(null, "Prova", "Matemática", (LocalDateTime) null, dia.atTime(9, 0), null, 1, PROFESSOR)))
                    .contains("início"));
        }

        @Test
        @DisplayName("aceita início e término válidos no mesmo dia")
        void agendamentoValido() {
            LocalDate dia = proximoDiaUtil();
            assumeTrue(dia != null, "sem dia útil restante no semestre");
            Prova prova = assertDoesNotThrow(() -> servico.salvarProva(
                    new Prova(null, "Prova", "Matemática", dia.atTime(10, 0), dia.atTime(11, 30), null, 1, PROFESSOR)));
            assertEquals(dia.atTime(11, 30), prova.getDataHoraFim());
        }
    }

    // ---- composição --------------------------------------------------------

    @Nested
    @DisplayName("Questões e pesos (PB12, PB14)")
    class Composicao {

        @Test
        @DisplayName("rejeita questão repetida, inexistente e peso inválido")
        void entradasInvalidas() {
            Prova prova = novaProva();
            multiplaEscolha(1, true);
            assertTrue(mensagem(() -> servico.salvarQuestoes(prova.getCodigo(),
                    List.of(new PesoQuestao(1, 1.0), new PesoQuestao(1, 2.0)))).contains("duas vezes"));
            assertTrue(mensagem(() -> servico.salvarQuestoes(prova.getCodigo(),
                    List.of(new PesoQuestao(99, 1.0)))).contains("não existe"));
            for (double peso : new double[] { 0, -1, Double.NaN, Double.POSITIVE_INFINITY })
                assertTrue(mensagem(() -> servico.salvarQuestoes(prova.getCodigo(),
                        List.of(new PesoQuestao(1, peso)))).contains("peso"), "peso " + peso);
            assertTrue(mensagem(() -> servico.salvarQuestoes(prova.getCodigo(), null)).contains("questões"));
            assertTrue(mensagem(() -> servico.salvarQuestoes(prova.getCodigo(), Arrays.asList((PesoQuestao) null)))
                    .contains("inválida"));
        }

        @Test
        @DisplayName("rejeita prova inexistente")
        void provaInexistente() {
            assertTrue(mensagem(() -> servico.salvarQuestoes("PRV999", List.of())).contains("não existe"));
        }
    }

    // ---- aplicação ---------------------------------------------------------

    @Nested
    @DisplayName("Aplicar prova a alunos (PB07)")
    class Aplicar {

        @Test
        @DisplayName("exige pelo menos uma questão")
        void semQuestoes() {
            Prova prova = novaProva();
            assertTrue(mensagem(() -> servico.salvarAlunos(prova.getCodigo(), List.of("ALU001")))
                    .contains("pelo menos uma questão"));
            assertFalse(servico.aplicada(prova.getCodigo()));
        }

        @Test
        @DisplayName("exige gabarito em questão de múltipla escolha")
        void multiplaEscolhaSemCorreta() {
            Prova prova = novaProva();
            multiplaEscolha(1, true);
            multiplaEscolha(2, false);
            compor(prova, 1, 2);
            String erro = mensagem(() -> servico.salvarAlunos(prova.getCodigo(), List.of("ALU001")));
            assertTrue(erro.contains("#2"), erro);
            assertFalse(servico.aplicada(prova.getCodigo()));
        }

        @Test
        @DisplayName("exige todos os pares em questão de correspondência")
        void correspondenciaIncompleta() {
            Prova prova = novaProva();
            correspondencia(3, false);
            compor(prova, 3);
            assertTrue(mensagem(() -> servico.salvarAlunos(prova.getCodigo(), List.of("ALU001"))).contains("#3"));
        }

        @Test
        @DisplayName("aceita correspondência completa e verdadeiro/falso em sequência")
        void tiposCompletos() {
            Prova prova = novaProva();
            correspondencia(3, true);
            verdadeiroFalso(4);
            compor(prova, 3, 4);
            assertDoesNotThrow(() -> servico.salvarAlunos(prova.getCodigo(), List.of("ALU001")));
            assertTrue(servico.aplicada(prova.getCodigo()));
        }

        @Test
        @DisplayName("ignora registros repetidos, vazios e nulos")
        void registrosRepetidosOuVazios() {
            Prova prova = provaPronta();
            servico.salvarAlunos(prova.getCodigo(),
                    Arrays.asList("ALU001", "ALU001", " ALU002 ", "ALU002", "", "   ", null));
            assertEquals(List.of("ALU001", "ALU002"), servico.listarAlunos(prova.getCodigo()));
        }

        @Test
        @DisplayName("lista vazia desaplica a prova e libera edição")
        void desaplicar() {
            Prova prova = provaPronta();
            servico.salvarAlunos(prova.getCodigo(), List.of("ALU001"));
            assertEquals("Aplicada", servico.estado(prova));
            servico.salvarAlunos(prova.getCodigo(), List.of());
            assertFalse(servico.aplicada(prova.getCodigo()));
            assertDoesNotThrow(() -> servico.excluirProva(prova.getCodigo()));
        }

        @Test
        @DisplayName("prova aplicada não pode ser editada, excluída nem recomposta")
        void aplicadaBloqueiaAlteracoes() {
            Prova prova = provaPronta();
            servico.salvarAlunos(prova.getCodigo(), List.of("ALU001"));
            assertTrue(mensagem(() -> servico.excluirProva(prova.getCodigo())).contains("excluída"));
            assertTrue(mensagem(() -> servico.salvarQuestoes(prova.getCodigo(), List.of(new PesoQuestao(1, 2.0))))
                    .contains("alterada"));
            assertEquals(1, servico.contarQuestoes(prova.getCodigo()));
            assertEquals("Aplicada", servico.estado(prova));
        }

        @Test
        @DisplayName("rejeita prova inexistente")
        void provaInexistente() {
            assertTrue(mensagem(() -> servico.salvarAlunos("PRV999", List.of("ALU001"))).contains("não existe"));
        }
    }

    // ---- dono da prova -----------------------------------------------------

    @Nested
    @DisplayName("Só o professor dono altera a prova (RN2)")
    class DonoDaProva {

        private Prova provaDeOutroProfessor() {
            Prova prova = provaPronta();
            servico.definirProfessorLogado(() -> OUTRO_PROFESSOR);
            return prova;
        }

        @Test
        void naoEdita() {
            Prova prova = provaDeOutroProfessor();
            prova.setTitulo("Alterado");
            assertTrue(mensagem(() -> servico.salvarProva(prova)).contains("dono"));
        }

        @Test
        void naoExclui() {
            Prova prova = provaDeOutroProfessor();
            assertTrue(mensagem(() -> servico.excluirProva(prova.getCodigo())).contains("dono"));
            assertEquals(1, provas.findAll().size());
        }

        @Test
        void naoAgenda() {
            Prova prova = provaDeOutroProfessor();
            LocalDate dia = proximoDiaUtil();
            assumeTrue(dia != null, "sem dia útil restante no semestre");
            assertTrue(mensagem(() -> servico.validarAgendamento(prova.getCodigo(), dia.atTime(10, 0)))
                    .contains("dono"));
        }

        @Test
        void naoCompoe() {
            Prova prova = provaDeOutroProfessor();
            assertTrue(mensagem(() -> servico.salvarQuestoes(prova.getCodigo(), List.of(new PesoQuestao(1, 1.0))))
                    .contains("dono"));
        }

        @Test
        void naoAplica() {
            Prova prova = provaDeOutroProfessor();
            assertTrue(mensagem(() -> servico.salvarAlunos(prova.getCodigo(), List.of("ALU001"))).contains("dono"));
            assertFalse(servico.aplicada(prova.getCodigo()));
        }

        @Test
        @DisplayName("sem professor logado nada pode ser alterado")
        void semLogin() {
            Prova prova = provaPronta();
            servico.definirProfessorLogado(null);
            assertTrue(mensagem(() -> servico.excluirProva(prova.getCodigo())).contains("dono"));
        }

        @Test
        @DisplayName("prova sem professor registrado não pertence a ninguém")
        void provaSemDono() {
            provas.save(new Prova("PRV050", "Órfã", "Física", (LocalDate) null, null, 1, null));
            assertTrue(mensagem(() -> servico.excluirProva("PRV050")).contains("dono"));
        }

        @Test
        @DisplayName("edição não transfere a prova para outro professor")
        void edicaoMantemDono() {
            Prova prova = novaProva();
            Prova editada = new Prova(prova.getCodigo(), "Novo título", "Matemática", (LocalDate) null, null, 2,
                    OUTRO_PROFESSOR);
            servico.salvarProva(editada);
            assertEquals(PROFESSOR, provas.findByKey(prova.getCodigo()).getRegistroProfessor());
            assertEquals("Novo título", provas.findByKey(prova.getCodigo()).getTitulo());
        }
    }

    // ---- reagendamento com entregas ---------------------------------------

    @Nested
    @DisplayName("Reagendar com entregas (PB08)")
    class ReagendarComEntregas {

        private ProvaAlunoStorageMemoria entregas;
        private ProvaServico servicoComEntregas;
        private Prova prova;
        private LocalDate dia;

        @BeforeEach
        void prepararComEntregas() {
            entregas = new ProvaAlunoStorageMemoria();
            servicoComEntregas = new ProvaServico(provas, new QuestaoServico(questoes), entregas, null);
            servicoComEntregas.definirProfessorLogado(() -> PROFESSOR);
            dia = proximoDiaUtil();
            assumeTrue(dia != null, "sem dia útil restante no semestre");
            multiplaEscolha(1, true);
            prova = servicoComEntregas.salvarProva(
                    new Prova(null, "Prova", "Matemática", dia.atTime(10, 0), dia.atTime(11, 0), null, 1, PROFESSOR));
            servicoComEntregas.salvarQuestoes(prova.getCodigo(), List.of(new PesoQuestao(1, 1.0)));
            servicoComEntregas.salvarAlunos(prova.getCodigo(), List.of("ALU001", "ALU002"));
        }

        private void entregar(String registro, String status) {
            entregas.registrarEntrega(new ProvaAluno(prova.getCodigo(), registro, Relogio.agora(), 8.0, status));
        }

        @Test
        @DisplayName("sem entregas a data pode ser alterada")
        void semEntregasPodeReagendar() {
            assertFalse(servicoComEntregas.temEntregas(prova.getCodigo()));
            assertDoesNotThrow(() -> servicoComEntregas.validarAgendamento(prova.getCodigo(), dia.atTime(14, 0)));
            prova.setDataHora(dia.atTime(14, 0));
            prova.setDataHoraFim(dia.atTime(15, 0));
            assertDoesNotThrow(() -> servicoComEntregas.salvarProva(prova));
            assertEquals(dia.atTime(14, 0), provas.findByKey(prova.getCodigo()).getDataHora());
        }

        @Test
        @DisplayName("aluno apenas vinculado (sem realizar) não bloqueia o reagendamento")
        void vinculoPendenteNaoBloqueia() {
            entregas.registrarEntrega(new ProvaAluno(prova.getCodigo(), "ALU001", null, null, "PENDENTE"));
            assertFalse(servicoComEntregas.temEntregas(prova.getCodigo()));
            assertDoesNotThrow(() -> servicoComEntregas.validarAgendamento(prova.getCodigo(), dia.atTime(14, 0)));
        }

        @Test
        @DisplayName("após uma entrega o diálogo de agendar é recusado")
        void entregaBloqueiaDialogo() {
            entregar("ALU001", "CORRIGIDA");
            assertTrue(servicoComEntregas.temEntregas(prova.getCodigo()));
            assertTrue(mensagem(() -> servicoComEntregas.validarAgendamento(prova.getCodigo(), dia.atTime(14, 0)))
                    .contains("reagendada"));
        }

        @Test
        @DisplayName("após uma entrega a edição não pode mudar data nem horários")
        void entregaBloqueiaEdicaoDoAgendamento() {
            entregar("ALU001", "REALIZADA");
            Prova editada = new Prova(prova.getCodigo(), "Prova", "Matemática", dia.atTime(10, 0), dia.atTime(12, 0),
                    null, 1, PROFESSOR);
            assertTrue(mensagem(() -> servicoComEntregas.salvarProva(editada)).contains("reagendada"));
            Prova outroDia = new Prova(prova.getCodigo(), "Prova", "Matemática", dia.atTime(10, 0), null, null, 1,
                    PROFESSOR);
            assertTrue(mensagem(() -> servicoComEntregas.salvarProva(outroDia)).contains("reagendada"));
            assertEquals(dia.atTime(11, 0), provas.findByKey(prova.getCodigo()).getDataHoraFim());
        }

        @Test
        @DisplayName("após uma entrega os demais dados continuam editáveis")
        void entregaPermiteEditarOutrosDados() {
            entregar("ALU001", "CORRIGIDA");
            Prova editada = new Prova(prova.getCodigo(), "Título novo", "Física", dia.atTime(10, 0), dia.atTime(11, 0),
                    "ADS", 2, PROFESSOR);
            assertDoesNotThrow(() -> servicoComEntregas.salvarProva(editada));
            Prova salva = provas.findByKey(prova.getCodigo());
            assertEquals("Título novo", salva.getTitulo());
            assertEquals(2, salva.getBimestre());
        }

        @Test
        @DisplayName("agendar sem data e horário é recusado")
        void agendarSemDataHora() {
            assertTrue(mensagem(() -> servicoComEntregas.validarAgendamento(prova.getCodigo(), null))
                    .contains("Informe a data"));
        }

        @Test
        @DisplayName("estado do aluno após entregar: Realizada e Corrigida")
        void estadoDoAlunoAposEntrega() {
            entregar("ALU001", "REALIZADA");
            entregar("ALU002", "CORRIGIDA");
            assertEquals("Realizada", servicoComEntregas.estadoParaAluno(prova, "ALU001"));
            assertEquals("Corrigida", servicoComEntregas.estadoParaAluno(prova, "ALU002"));
            assertEquals("Agendada", servicoComEntregas.estadoParaAluno(prova, "ALU003"));
        }
    }

    // ---- estados -----------------------------------------------------------

    @Nested
    @DisplayName("Estado da prova (PB05, PB06)")
    class Estados {

        @Test
        void estadosDoProfessor() {
            Prova prova = novaProva();
            assertEquals("Rascunho", servico.estado(prova));

            multiplaEscolha(1, true);
            compor(prova, 1);
            assertEquals("Pronta", servico.estado(prova));

            prova.setDataHora(Relogio.agora().plusDays(1));
            assertEquals("Agendada", servico.estado(prova));

            prova.setDataHora(Relogio.agora().minusDays(1));
            assertEquals("Encerrada", servico.estado(prova));

            servico.salvarAlunos(prova.getCodigo(), List.of("ALU001"));
            assertEquals("Aplicada", servico.estado(prova));
        }

        @Test
        void estadosDoAluno() {
            LocalDateTime agora = Relogio.agora();
            Prova prova = new Prova("PRV010", "Prova", "Matemática", (LocalDateTime) null, null, null, 1, PROFESSOR);
            assertEquals("Agendada", servico.estadoParaAluno(prova));

            prova.setDataHora(agora.plusHours(1));
            prova.setDataHoraFim(agora.plusHours(2));
            assertEquals("Agendada", servico.estadoParaAluno(prova));

            prova.setDataHora(agora.minusMinutes(30));
            prova.setDataHoraFim(agora.plusMinutes(30));
            assertEquals("Disponível", servico.estadoParaAluno(prova));

            prova.setDataHora(agora.minusHours(2));
            prova.setDataHoraFim(agora.minusHours(1));
            assertEquals("Encerrada", servico.estadoParaAluno(prova));

            // Sem horário de fim: disponível durante o dia agendado a partir do início
            prova.setDataHoraFim(null);
            prova.setDataHora(agora.minusMinutes(1));
            assertEquals("Disponível", servico.estadoParaAluno(prova));
            prova.setDataHora(agora.minusDays(1));
            assertEquals("Encerrada", servico.estadoParaAluno(prova));
        }
    }
}
