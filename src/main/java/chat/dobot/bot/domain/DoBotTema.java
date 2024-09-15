package chat.dobot.bot.domain;

public class DoBotTema {

    private String corFundoPagina;
    private String corTextoTitulo;
    private String corFundoChat;
    private String corTextoChat;
    private String corFundoMensagemUsuario;
    private String corFundoMensagemBot;

    public DoBotTema() {
    }

    public DoBotTema(String corFundoPagina, String corTextoTitulo, String corFundoChat, String corTextoChat, String corFundoMensagemUsuario, String corFundoMensagemBot) {
        this.corFundoPagina = corFundoPagina;
        this.corTextoTitulo = corTextoTitulo;
        this.corFundoChat = corFundoChat;
        this.corTextoChat = corTextoChat;
        this.corFundoMensagemUsuario = corFundoMensagemUsuario;
        this.corFundoMensagemBot = corFundoMensagemBot;
    }

    public String getCorFundoPagina() {
        return corFundoPagina;
    }

    public void setCorFundoPagina(String corFundoPagina) {
        this.corFundoPagina = corFundoPagina;
    }

    public String getCorTextoTitulo() {
        return corTextoTitulo;
    }

    public void setCorTextoTitulo(String corTextoTitulo) {
        this.corTextoTitulo = corTextoTitulo;
    }

    public String getCorFundoChat() {
        return corFundoChat;
    }

    public void setCorFundoChat(String corFundoChat) {
        this.corFundoChat = corFundoChat;
    }

    public String getCorFundoMensagemUsuario() {
        return corFundoMensagemUsuario;
    }

    public void setCorFundoMensagemUsuario(String corFundoMensagemUsuario) {
        this.corFundoMensagemUsuario = corFundoMensagemUsuario;
    }

    public String getCorFundoMensagemBot() {
        return corFundoMensagemBot;
    }

    public void setCorFundoMensagemBot(String corFundoMensagemBot) {
        this.corFundoMensagemBot = corFundoMensagemBot;
    }

    public String getCorTextoChat() {
        return corTextoChat;
    }

    public void setCorTextoChat(String corTextoChat) {
        this.corTextoChat = corTextoChat;
    }
}
