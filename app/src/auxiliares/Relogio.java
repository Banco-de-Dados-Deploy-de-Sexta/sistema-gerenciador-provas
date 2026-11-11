package auxiliares;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Relógio confiável da aplicação.
 *
 * Em vez de usar o relógio do sistema operacional (que o aluno pode alterar
 * para burlar o horário da prova), o horário é obtido de uma fonte externa
 * e comparado com o banco de dados:
 *
 *   1. API pública de horário (timeapi.io)
 *   2. Cabeçalho "Date" de uma resposta HTTPS (fallback quando a API cai)
 *   3. Horário do servidor de banco de dados
 *   4. Relógio local (último recurso, marcado como não confiável)
 *
 * Depois de sincronizado, o horário avança usando System.nanoTime(), que é
 * monotônico e não muda quando o usuário altera a data/hora do Windows.
 * A sincronização é refeita periodicamente.
 */
public final class Relogio {

    private static final String FUSO_PADRAO = "America/Sao_Paulo";
    private static final String API_PADRAO = "https://timeapi.io/api/time/current/zone?timeZone=";
    private static final String URL_DATE_HEADER = "https://www.google.com/generate_204";
    private static final Duration VALIDADE = Duration.ofMinutes(5);
    private static final Duration INTERVALO_RETENTATIVA = Duration.ofSeconds(30);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private static final Pattern DATETIME_JSON = Pattern.compile("\"dateTime\"\s*:\s*\"([^\"]+)\"");

    private static final ZoneId FUSO;
    private static final String URL_API;
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();

    private static Instant referencia;        // instante obtido da fonte confiável
    private static long referenciaNanos;      // System.nanoTime() no momento da sincronização
    private static boolean confiavel = false;
    private static long ultimaTentativaNanos = Long.MIN_VALUE;
    private static boolean sincronizando = false;
    private static ConexaoFornecedor fonteBanco;

    @FunctionalInterface
    public interface ConexaoFornecedor {
        Connection conectar() throws Exception;
    }

    static {
        Map<String, String> env;
        try {
            env = Env.carregar(".env");
        } catch (RuntimeException e) {
            env = Map.of();
        }
        String fuso = env.getOrDefault("FUSO_HORARIO", FUSO_PADRAO);
        ZoneId zona;
        try {
            zona = ZoneId.of(fuso);
        } catch (RuntimeException e) {
            zona = ZoneId.of(FUSO_PADRAO);
        }
        FUSO = zona;
        URL_API = env.getOrDefault("TIME_API_URL", API_PADRAO + FUSO.getId());
    }

    private Relogio() { }

    /** Registra o banco como fonte de horário alternativa (usado quando a API está fora do ar). */
    public static void configurarFonteBanco(ConexaoFornecedor fornecedor) {
        fonteBanco = fornecedor;
    }

    public static ZoneId fuso() {
        return FUSO;
    }

    /** Indica se o horário atual veio de uma fonte externa (API/banco) e não do relógio local. */
    public static synchronized boolean isConfiavel() {
        instante();
        return confiavel;
    }

    public static LocalDateTime agora() {
        return instante().atZone(FUSO).toLocalDateTime();
    }

    public static LocalDate hoje() {
        return agora().toLocalDate();
    }

    public static LocalTime horaAtual() {
        return agora().toLocalTime();
    }

    /** Força uma nova sincronização com as fontes externas. */
    public static synchronized void sincronizar() {
        ultimaTentativaNanos = System.nanoTime();
        Instant externo = consultarApi();
        if (externo == null) externo = consultarCabecalhoDate();
        if (externo == null) externo = consultarBanco();

        if (externo != null) {
            referencia = externo;
            referenciaNanos = System.nanoTime();
            confiavel = true;
            return;
        }

        if (referencia != null) {
            // Mantém a última referência confiável avançando pelo nanoTime (monotônico).
            return;
        }

        System.err.println("[Relogio] AVISO: nenhuma fonte externa de horário disponível; usando o relógio local.");
        referencia = Instant.now();
        referenciaNanos = System.nanoTime();
        confiavel = false;
    }

    private static void sincronizarEmSegundoPlano() {
        if (sincronizando) return;
        sincronizando = true;
        ultimaTentativaNanos = System.nanoTime();
        Thread t = new Thread(() -> {
            try {
                sincronizar();
            } finally {
                synchronized (Relogio.class) {
                    sincronizando = false;
                }
            }
        }, "sincronizador-relogio");
        t.setDaemon(true);
        t.start();
    }

    public static synchronized Instant instante() {
        long agoraNanos = System.nanoTime();
        boolean vencido = referencia == null
                || Duration.ofNanos(agoraNanos - referenciaNanos).compareTo(VALIDADE) > 0
                || !confiavel;
        // Evita travar a interface tentando a rede a cada chamada quando as fontes estão fora do ar.
        boolean podeTentar = referencia == null
                || Duration.ofNanos(agoraNanos - ultimaTentativaNanos).compareTo(INTERVALO_RETENTATIVA) > 0;
        if (vencido && podeTentar) {
            if (referencia != null && confiavel) {
                // Já temos uma referência confiável avançando pelo nanoTime: renova em segundo plano.
                sincronizarEmSegundoPlano();
            } else {
                sincronizar();
            }
        }
        return referencia.plusNanos(System.nanoTime() - referenciaNanos);
    }

    // ---- Fontes ----------------------------------------------------------

    private static Instant consultarApi() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(URL_API)).timeout(TIMEOUT).GET().build();
            HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) return null;
            Matcher m = DATETIME_JSON.matcher(resp.body());
            if (!m.find()) return null;
            String valor = m.group(1);
            // Aceita tanto "2026-11-11T17:28:47.420" (hora do fuso) quanto valores com offset.
            try {
                return ZonedDateTime.parse(valor).toInstant();
            } catch (RuntimeException ignored) {
                return LocalDateTime.parse(valor).atZone(FUSO).toInstant();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Instant consultarCabecalhoDate() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(URL_DATE_HEADER))
                    .timeout(TIMEOUT)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> resp = HTTP.send(req, HttpResponse.BodyHandlers.discarding());
            String date = resp.headers().firstValue("Date").orElse(null);
            if (date == null) return null;
            return ZonedDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        } catch (Exception e) {
            return null;
        }
    }

    private static Instant consultarBanco() {
        if (fonteBanco == null) return null;
        try {
            Connection c = fonteBanco.conectar();
            String sql = c.getMetaData().getURL().startsWith("jdbc:postgresql:")
                    ? "SELECT EXTRACT(EPOCH FROM NOW())"
                    : "SELECT UNIX_TIMESTAMP()";
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    double epoch = rs.getDouble(1);
                    return Instant.ofEpochMilli((long) (epoch * 1000));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
