package configuracao;

import auxiliares.Env;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class ConexaoMySQL {

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoMySQL() {
        Map<String, String> env = Env.carregar(".env");

        this.url = env.get("DB_URL");
        this.usuario = env.get("DB_USER");
        this.senha = env.get("DB_PASSWORD");

        if (url == null || usuario == null || senha == null) {
            throw new IllegalStateException(
                    "Defina DB_URL, DB_USER e DB_PASSWORD no arquivo .env");
        }
    }

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}