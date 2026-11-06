package auxiliares;

import modelos.Usuario;

public final class Sessao {
    private static Usuario usuarioLogado;
    private static String registroUsuarioLogado;

    private Sessao() { }

    public static void iniciar(Usuario usuario) { usuarioLogado = usuario; registroUsuarioLogado = usuario.getRegistro(); }
    public static void encerrar() { usuarioLogado = null; registroUsuarioLogado = null; }
    public static Usuario getUsuarioLogado() { return usuarioLogado; }
    public static String getRegistroUsuarioLogado() { return registroUsuarioLogado; }
}
