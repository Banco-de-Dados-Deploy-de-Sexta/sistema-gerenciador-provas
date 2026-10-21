package storage;

import configuracao.ConexaoMySQL;
import interfaces.Storage;
import modelos.Questao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestaoStorage implements Storage<Long, Questao> {

    // ---------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------

    private static final String SQL_SALVAR = """
            INSERT INTO questao (id_questao, titulo, descricao)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                titulo    = VALUES(titulo),
                descricao = VALUES(descricao)
            """;

    private static final String SQL_BUSCAR_POR_ID = """
            SELECT id_questao, titulo, descricao
            FROM questao
            WHERE id_questao = ?
            """;

    private static final String SQL_LISTAR_TODOS = """
            SELECT id_questao, titulo, descricao
            FROM questao
            ORDER BY id_questao
            """;

    private static final String SQL_REMOVER = """
            DELETE FROM questao
            WHERE id_questao = ?
            """;

    private static final String SQL_REMOVER_TODAS = """
            DELETE FROM questao
            """;

    private static final String SQL_REMOVER_ALTERNATIVAS = """
            DELETE FROM alternativa
            WHERE id_questao = ?
            """;

    private static final String SQL_SALVAR_ALTERNATIVA = """
            INSERT INTO alternativa (id_alternativa, id_questao, texto, correta)
            VALUES (?, ?, ?, ?)
            """;

    // ---------------------------------------------------------------
    // Construtor
    // ---------------------------------------------------------------

    private final ConexaoMySQL conexao;

    public QuestaoStorage(ConexaoMySQL conexao) {
        this.conexao = conexao;
    }

    // ---------------------------------------------------------------
    // Leitura
    // ---------------------------------------------------------------

    @Override
    public Questao get(Long key) {
        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)
        ) {

            ps.setLong(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? paraQuestao(rs) : null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar a questão " + key, e);
        }
    }

    @Override
    public List<Questao> getAll() {
        List<Questao> questoes = new ArrayList<>();

        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                questoes.add(paraQuestao(rs));
            }

            return questoes;

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar as questões", e);
        }
    }

    // ---------------------------------------------------------------
    // Escrita
    // ---------------------------------------------------------------

    @Override
    public Questao set(Long key, Questao value) {
        try (Connection con = conexao.conectar()) {
            boolean autoCommitAnterior = con.getAutoCommit();
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(SQL_SALVAR)) {
                    ps.setLong(1, key);
                    ps.setString(2, value.getTitulo());
                    ps.setString(3, value.getDescricao());
                    ps.executeUpdate();
                }

                // Em uma atualização, substitui as alternativas antigas pelas novas.
                try (PreparedStatement ps = con.prepareStatement(SQL_REMOVER_ALTERNATIVAS)) {
                    ps.setLong(1, key);
                    ps.executeUpdate();
                }

                salvarAlternativas(con, key, value);
                con.commit();
                return value;
            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            } finally {
                con.setAutoCommit(autoCommitAnterior);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar a questão " + key, e);
        }
    }

    private void salvarAlternativas(Connection con, Long idQuestao, Questao questao) throws SQLException {
        String alternativas = questao.getAlternativas();
        if (alternativas == null || alternativas.isBlank()) {
            return;
        }

        String alternativaCorreta = questao.getAlternativa_correta();
        int idAlternativa = 1;

        try (PreparedStatement ps = con.prepareStatement(SQL_SALVAR_ALTERNATIVA)) {
            for (String alternativa : alternativas.split("\\|", -1)) {
                String texto = alternativa.trim();
                if (texto.isEmpty()) {
                    continue;
                }

                ps.setInt(1, idAlternativa++);
                ps.setLong(2, idQuestao);
                ps.setString(3, texto);
                ps.setBoolean(4, texto.equals(alternativaCorreta));
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    @Override
    public void delete(Long key) {
        try (Connection con = conexao.conectar()) {
            con.setAutoCommit(false);
            try (PreparedStatement alternativas = con.prepareStatement(SQL_REMOVER_ALTERNATIVAS);
                 PreparedStatement questao = con.prepareStatement(SQL_REMOVER)) {
                alternativas.setLong(1, key);
                alternativas.executeUpdate();
                questao.setLong(1, key);
                questao.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover a questão " + key, e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection con = conexao.conectar()) {
            con.setAutoCommit(false);
            try (PreparedStatement alternativas = con.prepareStatement("DELETE FROM alternativa");
                 PreparedStatement questoes = con.prepareStatement(SQL_REMOVER_TODAS)) {
                alternativas.executeUpdate();
                questoes.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover todas as questões", e);
        }
    }

    // ---------------------------------------------------------------
    // Conversão
    // ---------------------------------------------------------------

    // As alternativas ficam em uma tabela própria e são salvas junto da questão.
    private Questao paraQuestao(ResultSet rs) throws SQLException {
        return new Questao(
                rs.getLong("id_questao"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                "",
                ""
        );
    }
}
