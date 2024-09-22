package chat.dobot.bot.domain;

import chat.dobot.bot.Autor;
import chat.dobot.bot.BotStateMethod;
import chat.dobot.bot.Contexto;
import chat.dobot.bot.DoBotException;
import chat.dobot.bot.utils.AnnotationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DoBot {

    private static final Logger logger = LoggerFactory.getLogger(DoBot.class);

    private final List<Mensagem> mensagens = new LinkedList<>();
    private final Map<String, BotStateMethod> estados;
    private DoBotTema doBotTema;
    private String ultimaMensagemUsuario;
    private String ultimaMensagemBot;
    private String estadoAtual;
    private final Object chatbot;

    public DoBot(Object chatbot, Map<String, BotStateMethod> estados, String mensagemInicial, DoBotTema doBotTema) {
        this.chatbot = chatbot;
        this.estados = estados;
        this.estadoAtual = AnnotationsUtil.obterEstadoInicial(chatbot);
        logger.debug("Estado inicial: {}", this.estadoAtual);
        adicionarMensagemInicial(mensagemInicial);
        this.doBotTema = doBotTema;
    }

    /**
     * Encaminha a mensagem do usuário através do chat, seleciona o estado e executa o estado correspondente.
     * @param contexto contexto do chat
     */
    public void receberMensagem(Contexto contexto) {
        ultimaMensagemUsuario = contexto.getMensagemUsuario();

        if (!estados.containsKey(contexto.getEstado())) {
            throw new DoBotException("Estado '" + contexto.getEstado() + "' não encontrado!");
        }

        estados.get(contexto.getEstado()).execute(contexto);

        ultimaMensagemBot = contexto.getResposta() != null ? contexto.getResposta() : ultimaMensagemBot;
        estadoAtual = contexto.getEstado();

        adicionarMensagens(contexto);
    }

    private void adicionarMensagemInicial(String mensagemInicial) {
        if (mensagemInicial == null){
            throw new IllegalArgumentException("A mensagem inicial não pode ser nula!");
        }
        mensagens.add(new Mensagem(Autor.BOT, mensagemInicial));
    }

    private void adicionarMensagens(Contexto contexto) {
        if (contexto.getMensagemUsuario() != null) {
            mensagens.add(new Mensagem(Autor.USUARIO, contexto.getMensagemUsuario()));
        }
        if (contexto.getResposta() != null) {
            mensagens.add(new Mensagem(Autor.BOT, contexto.getResposta()));
        }
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    public String getEstadoAtual() {
        return estadoAtual;
    }

    public String getUltimaMensagemUsuario() {
        return ultimaMensagemUsuario;
    }

    public String getUltimaMensagemBot() {
        return ultimaMensagemBot;
    }

    public Object getChatbot() {
        return chatbot;
    }

    public DoBotTema getDoBotTema() {
        return doBotTema;
    }

    public void setDoBotTema(DoBotTema doBotTema) {
        this.doBotTema = doBotTema;
    }
}
