package storage;

import configuracao.ConexaoMySQL;
import interfaces.Storage;
import modelos.PesoQuestao;
import modelos.Prova;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProvaStorage implements Storage<String, Prova> {
    private static final String SQL_SALVAR = """
            INSERT INTO prova (codigo, titulo, materia, data_prova, curso, bimestre)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE titulo = VALUES(titulo), materia = VALUES(materia),
              data_prova = VALUES(data_prova), curso = VALUES(curso), bimestre = VALUES(bimestre)
            """;
    private static final String SQL_BUSCAR = "SELECT codigo, titulo, materia, data_prova, curso, bimestre FROM prova WHERE codigo = ?";
    private static final String SQL_LISTAR = "SELECT codigo, titulo, materia, data_prova, curso, bimestre FROM prova ORDER BY codigo";
    private static final String SQL_REMOVER = "DELETE FROM prova WHERE codigo = ?";
    private static final String SQL_LISTAR_QUESTOES = "SELECT id_questao, peso_questao FROM prova_questao WHERE codigo_prova = ? ORDER BY id_questao";

    private final ConexaoMySQL conexao;

    public ProvaStorage(ConexaoMySQL conexao) { this.conexao = conexao; }

    @Override
    public Prova get(String key) {
        try (Connection con = conexao.conectar(); PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? paraProva(rs) : null; }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar a prova " + key, e);
        }
    }

    @Override
    public List<Prova> getAll() {
        List<Prova> provas = new ArrayList<>();
        try (Connection con = conexao.conectar(); PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) provas.add(paraProva(rs));
            return provas;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar as provas", e);
        }
    }

    @Override
    public Prova set(String key, Prova value) {
        try (Connection con = conexao.conectar(); PreparedStatement ps = con.prepareStatement(SQL_SALVAR)) {
            ps.setString(1, key);
            ps.setString(2, value.getTitulo());
            ps.setString(3, value.getMateria());
            if (value.getData() == null) ps.setNull(4, java.sql.Types.DATE);
            else ps.setDate(4, Date.valueOf(value.getData()));
            ps.setString(5, value.getCurso());
            ps.setInt(6, value.getBimestre());
            ps.executeUpdate();
            value.setCodigo(key);
            return value;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar a prova " + key, e);
        }
    }

    @Override
    public void delete(String key) {
        try (Connection con = conexao.conectar()) {
            con.setAutoCommit(false);
            try (PreparedStatement vinculos = con.prepareStatement("DELETE FROM prova_questao WHERE codigo_prova = ?");
                 PreparedStatement prova = con.prepareStatement(SQL_REMOVER)) {
                vinculos.setString(1, key);
                vinculos.executeUpdate();
                prova.setString(1, key);
                prova.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover a prova " + key, e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection con = conexao.conectar()) {
            con.setAutoCommit(false);
            try (PreparedStatement vinculos = con.prepareStatement("DELETE FROM prova_questao");
                 PreparedStatement provas = con.prepareStatement("DELETE FROM prova")) {
                vinculos.executeUpdate();
                provas.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover todas as provas", e);
        }
    }

    public List<PesoQuestao> listarQuestoes(String codigo) {
        List<PesoQuestao> questoes = new ArrayList<>();
        try (Connection con = conexao.conectar(); PreparedStatement ps = con.prepareStatement(SQL_LISTAR_QUESTOES)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) questoes.add(new PesoQuestao(rs.getLong("id_questao"), rs.getDouble("peso_questao")));
            }
            return questoes;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar questões da prova " + codigo, e);
        }
    }

    public void salvarQuestoes(String codigo, List<PesoQuestao> questoes) {
        try (Connection con = conexao.conectar()) {
            con.setAutoCommit(false);
            try (PreparedStatement remover = con.prepareStatement("DELETE FROM prova_questao WHERE codigo_prova = ?");
                 PreparedStatement inserir = con.prepareStatement("INSERT INTO prova_questao (codigo_prova, id_questao, peso_questao) VALUES (?, ?, ?)")) {
                remover.setString(1, codigo);
                remover.executeUpdate();
                for (PesoQuestao questao : questoes) {
                    inserir.setString(1, codigo);
                    inserir.setLong(2, questao.getIdQuestao());
                    inserir.setDouble(3, questao.getPeso());
                    inserir.addBatch();
                }
                inserir.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar questões da prova " + codigo, e);
        }
    }

    private Prova paraProva(ResultSet rs) throws SQLException {
        Date data = rs.getDate("data_prova");
        return new Prova(rs.getString("codigo"), rs.getString("titulo"), rs.getString("materia"),
                data == null ? null : data.toLocalDate(), rs.getString("curso"), rs.getInt("bimestre"));
    }
}
