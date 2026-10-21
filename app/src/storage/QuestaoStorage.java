package storage;

import configuracao.ConexaoMySQL;
import interfaces.Storage;
import modelos.Alternativa;
import modelos.Questao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestaoStorage implements Storage<Long, Questao> {
    private static final String SQL_SALVAR_QUESTAO = """
            INSERT INTO questao (id_questao, titulo, descricao) VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE titulo = VALUES(titulo), descricao = VALUES(descricao)
            """;
    private static final String SQL_BUSCAR = "SELECT id_questao, titulo, descricao FROM questao WHERE id_questao = ?";
    private static final String SQL_LISTAR = "SELECT id_questao, titulo, descricao FROM questao ORDER BY id_questao";
    private static final String SQL_REMOVER_QUESTAO = "DELETE FROM questao WHERE id_questao = ?";
    private static final String SQL_REMOVER_ALTERNATIVAS = "DELETE FROM alternativa WHERE id_questao = ?";
    private static final String SQL_REMOVER_VINCULOS = "DELETE FROM prova_questao WHERE id_questao = ?";
    private static final String SQL_LISTAR_ALTERNATIVAS = """
            SELECT id_alternativa, texto, correta FROM alternativa
            WHERE id_questao = ? ORDER BY id_alternativa
            """;
    private static final String SQL_SALVAR_ALTERNATIVA = """
            INSERT INTO alternativa (id_alternativa, id_questao, texto, correta) VALUES (?, ?, ?, ?)
            """;

    private final ConexaoMySQL conexao;

    public QuestaoStorage(ConexaoMySQL conexao) { this.conexao = conexao; }

    @Override
    public Questao get(Long key) {
        try (Connection con = conexao.conectar(); PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setLong(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return paraQuestao(con, rs);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar a questão " + key, e);
        }
    }

    @Override
    public List<Questao> getAll() {
        List<Questao> questoes = new ArrayList<>();
        try (Connection con = conexao.conectar(); PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) questoes.add(paraQuestao(con, rs));
            return questoes;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar as questões", e);
        }
    }

    @Override
    public Questao set(Long key, Questao value) {
        try (Connection con = conexao.conectar()) {
            boolean autoCommitAnterior = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(SQL_SALVAR_QUESTAO)) {
                    ps.setLong(1, key);
                    ps.setString(2, value.getTitulo());
                    ps.setString(3, value.getDescricao());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(SQL_REMOVER_ALTERNATIVAS)) {
                    ps.setLong(1, key);
                    ps.executeUpdate();
                }
                salvarAlternativas(con, key, value.getAlternativas());
                con.commit();
                return value;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(autoCommitAnterior);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar a questão " + key, e);
        }
    }

    private void salvarAlternativas(Connection con, Long idQuestao, List<Alternativa> alternativas) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_SALVAR_ALTERNATIVA)) {
            int id = 1;
            for (Alternativa alternativa : alternativas) {
                String texto = alternativa.getTexto().trim();
                if (texto.isEmpty()) continue;
                ps.setInt(1, id++);
                ps.setLong(2, idQuestao);
                ps.setString(3, texto);
                ps.setBoolean(4, alternativa.isCorreta());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public void delete(Long key) {
        executarTransacaoRemocao(key);
    }

    private void executarTransacaoRemocao(Long key) {
        try (Connection con = conexao.conectar()) {
            con.setAutoCommit(false);
            try {
                for (String sql : List.of(SQL_REMOVER_VINCULOS, SQL_REMOVER_ALTERNATIVAS, SQL_REMOVER_QUESTAO)) {
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setLong(1, key);
                        ps.executeUpdate();
                    }
                }
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
            try (PreparedStatement vinculos = con.prepareStatement("DELETE FROM prova_questao");
                 PreparedStatement alternativas = con.prepareStatement("DELETE FROM alternativa");
                 PreparedStatement questoes = con.prepareStatement("DELETE FROM questao")) {
                vinculos.executeUpdate();
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

    private Questao paraQuestao(Connection con, ResultSet rs) throws SQLException {
        long id = rs.getLong("id_questao");
        List<Alternativa> alternativas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ALTERNATIVAS)) {
            ps.setLong(1, id);
            try (ResultSet alternativasRs = ps.executeQuery()) {
                while (alternativasRs.next()) {
                    alternativas.add(new Alternativa(
                            alternativasRs.getInt("id_alternativa"),
                            alternativasRs.getString("texto"),
                            alternativasRs.getBoolean("correta")
                    ));
                }
            }
        }
        return new Questao(id, rs.getString("titulo"), rs.getString("descricao"), alternativas);
    }
}
