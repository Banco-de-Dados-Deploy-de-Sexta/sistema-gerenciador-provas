package storage;

import configuracao.ConexaoMySQL;
import modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioStorage {
    private static final String SQL_BUSCAR_POR_REGISTRO =
            "SELECT registro, nome, tipo_usuario FROM usuario WHERE registro = ?";
    private static final String SQL_LISTAR_ALUNOS =
            "SELECT registro, nome, tipo_usuario FROM usuario WHERE LOWER(tipo_usuario) = 'aluno' ORDER BY nome";

    private final ConexaoMySQL conexao;

    public UsuarioStorage(ConexaoMySQL conexao) {
        this.conexao = conexao;
    }

    public Usuario buscarPorRegistro(String registro) {
        try {
            Connection con = conexao.conectar();
            try (PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_REGISTRO)) {
                ps.setString(1, registro.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Usuario(
                                rs.getString("registro"),
                                rs.getString("nome"),
                                rs.getString("tipo_usuario")
                        );
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar usuário no banco de dados.", e);
        }
    }

    public List<Usuario> listarAlunos() {
        List<Usuario> alunos = new ArrayList<>();
        try {
            Connection con = conexao.conectar();
            try (PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ALUNOS);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    alunos.add(new Usuario(rs.getString("registro"), rs.getString("nome"), rs.getString("tipo_usuario")));
            }
            return alunos;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar os alunos no banco de dados.", e);
        }
    }
}