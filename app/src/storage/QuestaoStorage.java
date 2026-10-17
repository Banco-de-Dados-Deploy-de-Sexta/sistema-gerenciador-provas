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
        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_SALVAR)) {

            ps.setLong(1, key);
            ps.setString(2, value.getTitulo());
            ps.setString(3, value.getDescricao());
            ps.executeUpdate();

            return value;

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar a questão " + key, e);
        }
    }

    @Override
    public void delete(Long key) {
        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_REMOVER)) {

            ps.setLong(1, key);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover a questão " + key, e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_REMOVER_TODAS)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover todas as questões", e);
        }
    }

    // ---------------------------------------------------------------
    // Conversão
    // ---------------------------------------------------------------

    // A tabela questao não guarda alternativas, então elas voltam vazias aqui
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