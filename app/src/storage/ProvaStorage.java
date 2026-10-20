package storage;

import configuracao.ConexaoMySQL;
import interfaces.Storage;
import modelos.Prova;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProvaStorage implements Storage<Long, Prova> {

    // ---------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------

    private static final String SQL_SALVAR = """
            INSERT INTO prova (codigo, titulo, materia, data_prova, curso, bimestre)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_BUSCAR_POR_ID = """
            SELECT codigo, titulo, materia, data_prova, curso, bimestre
            FROM prova
            WHERE codigo = ?
            """;

    private static final String SQL_LISTAR_TODOS = """
            SELECT codigo, titulo, materia, data_prova, curso, bimestre
            FROM prova
            ORDER BY codigo
            """;

    private static final String SQL_REMOVER = """
            DELETE FROM prova
            WHERE codigo = ?
            """;

    private static final String SQL_REMOVER_TODAS = """
            DELETE FROM prova
            """;

    // ---------------------------------------------------------------
    // Construtor
    // ---------------------------------------------------------------

    private final ConexaoMySQL conexao;

    public ProvaStorage(ConexaoMySQL conexao) {
        this.conexao = conexao;
    }

    // ---------------------------------------------------------------
    // Leitura
    // ---------------------------------------------------------------

    @Override
    public Prova get(Long key) {
        try (Connection con = conexao.conectar();
                PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setLong(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? paraProva(rs) : null;
            }
            
        } catch (SQLException e){
            throw new IllegalStateException("Erro ao buscar a prova " + key, e);
        }
    }

    @Override
    public List<Prova> getAll(){
        List<Prova> provas = new ArrayList<>();

        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                provas.add(paraProva(rs));
            }

            return provas;

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar as provas", e);
        }
    }

    // ---------------------------------------------------------------
    // Escrita
    // ---------------------------------------------------------------

    @Override
    public Prova set(Long key, Prova value) {
        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_SALVAR)) {

            ps.setLong(1, key);
            ps.setString(2, value.getTitulo());
            ps.setString(3, value.getMateria());
            ps.setDate(4, java.sql.Date.valueOf(value.getData()));
            ps.setString(5, value.getCurso());
            ps.setInt(6, value.getBimestre());

            ps.executeUpdate();

            return value;

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar a prova " + key, e);
        }
    }

    @Override
    public void delete(Long key) {
        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_REMOVER)) {

            ps.setLong(1, key);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover a prova " + key, e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection con = conexao.conectar();
             PreparedStatement ps = con.prepareStatement(SQL_REMOVER_TODAS)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover todas as provas", e);
        }
    }


    // ---------------------------------------------------------------
    // Conversão
    // ---------------------------------------------------------------


    private Prova paraProva(ResultSet rs) throws SQLException {
        Prova prova = new Prova(
                rs.getString("titulo"),
                rs.getString("materia"),
                rs.getDate("data_prova").toLocalDate(),
                rs.getString("curso"),
                rs.getInt("bimestre"));

        prova.setCodigo(rs.getLong("codigo"));
        return prova;
    }
}