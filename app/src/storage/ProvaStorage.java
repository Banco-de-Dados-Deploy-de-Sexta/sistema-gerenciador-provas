package storage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import configuracao.ConexaoMySQL;
import interfaces.Storage;
import modelos.PesoQuestao;
import modelos.Prova;

public class ProvaStorage implements Storage<String, Prova> {
    private static final String SQL_SALVAR_MYSQL = """
            INSERT INTO prova (codigo, titulo, materia, data_prova, data_prova_fim, curso, bimestre, registro_professor)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE titulo = VALUES(titulo), materia = VALUES(materia),
              data_prova = VALUES(data_prova), data_prova_fim = VALUES(data_prova_fim), curso = VALUES(curso), bimestre = VALUES(bimestre),
              registro_professor = VALUES(registro_professor)
            """;

    private static final String SQL_SALVAR_POSTGRES = """
            INSERT INTO prova (codigo, titulo, materia, data_prova, data_prova_fim, curso, bimestre, registro_professor)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (codigo) DO UPDATE SET
              titulo = EXCLUDED.titulo,
              materia = EXCLUDED.materia,
              data_prova = EXCLUDED.data_prova,
              data_prova_fim = EXCLUDED.data_prova_fim,
              curso = EXCLUDED.curso,
              bimestre = EXCLUDED.bimestre,
              registro_professor = EXCLUDED.registro_professor
            """;

    private static final String SQL_BUSCAR = "SELECT codigo, titulo, materia, data_prova, data_prova_fim, curso, bimestre, registro_professor FROM prova WHERE codigo = ?";
    private static final String SQL_LISTAR = "SELECT codigo, titulo, materia, data_prova, data_prova_fim, curso, bimestre, registro_professor FROM prova ORDER BY codigo";
    private static final String SQL_REMOVER = "DELETE FROM prova WHERE codigo = ?";
    private static final String SQL_LISTAR_QUESTOES = "SELECT id_questao, peso_questao FROM prova_questao WHERE codigo_prova = ? ORDER BY id_questao";
    private static final String SQL_LISTAR_ALUNOS = "SELECT registro_aluno FROM prova_aluno WHERE codigo_prova = ? ORDER BY registro_aluno";
    private static final String SQL_LISTAR_POR_ALUNO = """
            SELECT p.codigo, p.titulo, p.materia, p.data_prova, p.data_prova_fim, p.curso, p.bimestre, p.registro_professor
            FROM prova p
            INNER JOIN prova_aluno pa ON p.codigo = pa.codigo_prova
            WHERE pa.registro_aluno = ?
            ORDER BY p.data_prova ASC, p.codigo ASC
            """;

    private final ConexaoMySQL conexao;

    public ProvaStorage(ConexaoMySQL conexao) {
        this.conexao = conexao;
    }

    @Override
    public Prova get(String key) {
        try {
            Connection con = conexao.conectar();
            try (PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? paraProva(rs) : null;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar a prova " + key, e);
        }
    }

    @Override
    public List<Prova> getAll() {
        List<Prova> provas = new ArrayList<>();
        try {
            Connection con = conexao.conectar();
            try (PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    provas.add(paraProva(rs));
                return provas;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar as provas", e);
        }
    }

    @Override
    public Prova set(String key, Prova value) {
        String sqlSalvar = conexao.isPostgreSQL() ? SQL_SALVAR_POSTGRES : SQL_SALVAR_MYSQL;
        try {
            Connection con = conexao.conectar();
            try (PreparedStatement ps = con.prepareStatement(sqlSalvar)) {
                ps.setString(1, key);
                ps.setString(2, value.getTitulo());
                ps.setString(3, value.getMateria());
                if (value.getDataHora() == null) ps.setNull(4, java.sql.Types.TIMESTAMP);
                else ps.setTimestamp(4, Timestamp.valueOf(value.getDataHora()));
                if (value.getDataHoraFim() == null) ps.setNull(5, java.sql.Types.TIMESTAMP);
                else ps.setTimestamp(5, Timestamp.valueOf(value.getDataHoraFim()));
                ps.setString(6, value.getCurso());
                ps.setInt(7, value.getBimestre());
                ps.setString(8, value.getRegistroProfessor());
                ps.executeUpdate();
                value.setCodigo(key);
                return value;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar a prova " + key, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Connection con = conexao.conectar();
            boolean autoCommitAnterior = con.getAutoCommit();
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
            } finally {
                con.setAutoCommit(autoCommitAnterior);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover a prova " + key, e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            Connection con = conexao.conectar();
            boolean autoCommitAnterior = con.getAutoCommit();
            con.setAutoCommit(false);
            try (PreparedStatement vinculos = con.prepareStatement("DELETE FROM prova_questao");
                    PreparedStatement provas = con.prepareStatement("DELETE FROM prova")) {
                vinculos.executeUpdate();
                provas.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(autoCommitAnterior);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao remover todas as provas", e);
        }
    }

    public List<PesoQuestao> listarQuestoes(String codigo) {
        List<PesoQuestao> questoes = new ArrayList<>();
        try {
            Connection con = conexao.conectar();
            try (PreparedStatement ps = con.prepareStatement(SQL_LISTAR_QUESTOES)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        questoes.add(new PesoQuestao(rs.getLong("id_questao"), rs.getDouble("peso_questao")));
                }
                return questoes;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar questões da prova " + codigo, e);
        }
    }

    public void salvarQuestoes(String codigo, List<PesoQuestao> questoes) {
        try {
            Connection con = conexao.conectar();
            boolean autoCommitAnterior = con.getAutoCommit();
            con.setAutoCommit(false);
            try (PreparedStatement remover = con.prepareStatement("DELETE FROM prova_questao WHERE codigo_prova = ?");
                    PreparedStatement inserir = con.prepareStatement(
                            "INSERT INTO prova_questao (codigo_prova, id_questao, peso_questao) VALUES (?, ?, ?)")) {
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
            } finally {
                con.setAutoCommit(autoCommitAnterior);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar questões da prova " + codigo, e);
        }
    }

    public List<String> listarAlunos(String codigo) {
        List<String> alunos = new ArrayList<>();
        try {
            Connection con = conexao.conectar();
            try (PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ALUNOS)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) alunos.add(rs.getString("registro_aluno"));
                }
                return alunos;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar alunos da prova " + codigo, e);
        }
    }

    public List<Prova> listarPorAluno(String registroAluno) {
        List<Prova> provas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.conectar().prepareStatement(SQL_LISTAR_POR_ALUNO)) {
            stmt.setString(1, registroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    provas.add(paraProva(rs));
                }
                return provas;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar provas do aluno " + registroAluno, e);
        }
    }

    // Substitui os alunos vinculados à prova pela lista informada, na mesma transação
    public void salvarAlunos(String codigo, List<String> registros) {
        try {
            Connection con = conexao.conectar();
            boolean autoCommitAnterior = con.getAutoCommit();
            con.setAutoCommit(false);
            try (PreparedStatement remover = con.prepareStatement("DELETE FROM prova_aluno WHERE codigo_prova = ?");
                 PreparedStatement inserir = con.prepareStatement("INSERT INTO prova_aluno (codigo_prova, registro_aluno) VALUES (?, ?)")) {
                remover.setString(1, codigo);
                remover.executeUpdate();
                for (String registro : registros) {
                    inserir.setString(1, codigo);
                    inserir.setString(2, registro);
                    inserir.addBatch();
                }
                inserir.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(autoCommitAnterior);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar alunos da prova " + codigo, e);
        }
    }

    private Prova paraProva(ResultSet rs) throws SQLException {
        Timestamp dataHora = rs.getTimestamp("data_prova");
        Timestamp dataHoraFim = rs.getTimestamp("data_prova_fim");
        return new Prova(
                rs.getString("codigo"),
                rs.getString("titulo"),
                rs.getString("materia"),
                dataHora == null ? null : dataHora.toLocalDateTime(),
                dataHoraFim == null ? null : dataHoraFim.toLocalDateTime(),
                rs.getString("curso"),
                rs.getInt("bimestre"),
                rs.getString("registro_professor")
        );
    }

}
