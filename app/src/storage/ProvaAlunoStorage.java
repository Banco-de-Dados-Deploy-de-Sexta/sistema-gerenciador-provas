package storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import configuracao.ConexaoMySQL;
import modelos.ProvaAluno;

public class ProvaAlunoStorage {

    private final ConexaoMySQL conexao;

    public ProvaAlunoStorage(ConexaoMySQL conexao) {
        this.conexao = conexao;
    }

    public ProvaAluno buscar(String codigoProva, String registroAluno) {
        String sql = "SELECT codigo_prova, registro_aluno, data_realizacao, nota_final, status FROM prova_aluno WHERE codigo_prova = ? AND registro_aluno = ?";
        try (PreparedStatement stmt = conexao.conectar().prepareStatement(sql)) {
            stmt.setString(1, codigoProva);
            stmt.setString(2, registroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return paraProvaAluno(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar realização da prova para o aluno.", e);
        }
    }

    public List<ProvaAluno> listarPorAluno(String registroAluno) {
        String sql = "SELECT codigo_prova, registro_aluno, data_realizacao, nota_final, status FROM prova_aluno WHERE registro_aluno = ?";
        List<ProvaAluno> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexao.conectar().prepareStatement(sql)) {
            stmt.setString(1, registroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(paraProvaAluno(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar provas do aluno.", e);
        }
    }

    // Quantidade de alunos que já entregaram a prova (regra da PB09)
    public int contarEntregas(String codigoProva) {
        String sql = """
            SELECT COUNT(*) FROM prova_aluno
            WHERE codigo_prova = ?
              AND (data_realizacao IS NOT NULL OR UPPER(status) IN ('REALIZADA', 'CORRIGIDA'))
        """;
        try (PreparedStatement stmt = conexao.conectar().prepareStatement(sql)) {
            stmt.setString(1, codigoProva);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao contar entregas da prova.", e);
        }
    }

    public void registrarEntrega(ProvaAluno provaAluno) {
        String sql = """
            UPDATE prova_aluno 
            SET data_realizacao = ?, nota_final = ?, status = ? 
            WHERE codigo_prova = ? AND registro_aluno = ?
        """;
        try (PreparedStatement stmt = conexao.conectar().prepareStatement(sql)) {
            stmt.setTimestamp(1, provaAluno.getDataRealizacao() != null 
                    ? Timestamp.valueOf(provaAluno.getDataRealizacao()) 
                    : Timestamp.valueOf(auxiliares.Relogio.agora()));
            if (provaAluno.getNotaFinal() != null) {
                stmt.setDouble(2, provaAluno.getNotaFinal());
            } else {
                stmt.setNull(2, java.sql.Types.DECIMAL);
            }
            stmt.setString(3, provaAluno.getStatus() != null ? provaAluno.getStatus() : "REALIZADA");
            stmt.setString(4, provaAluno.getCodigoProva());
            stmt.setString(5, provaAluno.getRegistroAluno());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao registrar a entrega da prova.", e);
        }
    }

    private ProvaAluno paraProvaAluno(ResultSet rs) throws SQLException {
        Timestamp dt = rs.getTimestamp("data_realizacao");
        Double nota = rs.getObject("nota_final") != null ? rs.getDouble("nota_final") : null;
        return new ProvaAluno(
            rs.getString("codigo_prova"),
            rs.getString("registro_aluno"),
            dt != null ? dt.toLocalDateTime() : null,
            nota,
            rs.getString("status")
        );
    }
}
