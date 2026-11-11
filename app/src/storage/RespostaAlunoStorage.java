package storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import configuracao.ConexaoMySQL;
import modelos.RespostaAluno;

public class RespostaAlunoStorage {

    private final ConexaoMySQL conexao;

    public RespostaAlunoStorage(ConexaoMySQL conexao) {
        this.conexao = conexao;
    }

    public void salvarRespostas(List<RespostaAluno> respostas) {
        if (respostas == null || respostas.isEmpty()) return;

        String sql = """
            INSERT INTO resposta_aluno 
            (codigo_prova, registro_aluno, id_questao, id_alternativa_escolhida, resposta_texto, ordem_questao, acertou, nota_obtida) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            Connection con = conexao.conectar();
            boolean autoCommitAnterior = con.getAutoCommit();
            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                for (RespostaAluno r : respostas) {
                    stmt.setString(1, r.getCodigoProva());
                    stmt.setString(2, r.getRegistroAluno());
                    stmt.setInt(3, r.getIdQuestao());

                    if (r.getIdAlternativaEscolhida() != null) {
                        stmt.setInt(4, r.getIdAlternativaEscolhida());
                    } else {
                        stmt.setNull(4, Types.INTEGER);
                    }

                    stmt.setString(5, r.getRespostaTexto());
                    stmt.setInt(6, r.getOrdemQuestao() > 0 ? r.getOrdemQuestao() : 1);

                    if (r.getAcertou() != null) {
                        stmt.setBoolean(7, r.getAcertou());
                    } else {
                        stmt.setNull(7, Types.BOOLEAN);
                    }

                    if (r.getNotaObtida() != null) {
                        stmt.setDouble(8, r.getNotaObtida());
                    } else {
                        stmt.setNull(8, Types.DECIMAL);
                    }

                    stmt.addBatch();
                }
                stmt.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(autoCommitAnterior);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar respostas do aluno: " + e.getMessage());
            throw new IllegalStateException("Erro ao gravar respostas do aluno.", e);
        }
    }

    public List<RespostaAluno> listarPorAlunoEProva(String codigoProva, String registroAluno) {
        String sql = """
            SELECT id_resposta, codigo_prova, registro_aluno, id_questao, id_alternativa_escolhida, 
                   resposta_texto, ordem_questao, acertou, nota_obtida 
            FROM resposta_aluno 
            WHERE codigo_prova = ? AND registro_aluno = ? 
            ORDER BY ordem_questao ASC
        """;
        List<RespostaAluno> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexao.conectar().prepareStatement(sql)) {
            stmt.setString(1, codigoProva);
            stmt.setString(2, registroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RespostaAluno r = new RespostaAluno();
                    r.setId(rs.getInt("id_resposta"));
                    r.setCodigoProva(rs.getString("codigo_prova"));
                    r.setRegistroAluno(rs.getString("registro_aluno"));
                    r.setIdQuestao(rs.getInt("id_questao"));
                    r.setIdAlternativaEscolhida(rs.getObject("id_alternativa_escolhida") != null ? rs.getInt("id_alternativa_escolhida") : null);
                    r.setRespostaTexto(rs.getString("resposta_texto"));
                    r.setOrdemQuestao(rs.getInt("ordem_questao"));
                    r.setAcertou(rs.getObject("acertou") != null ? rs.getBoolean("acertou") : null);
                    r.setNotaObtida(rs.getObject("nota_obtida") != null ? rs.getDouble("nota_obtida") : null);
                    lista.add(r);
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar respostas do aluno.", e);
        }
    }
}