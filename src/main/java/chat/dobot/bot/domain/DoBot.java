package chat.dobot.bot.domain;

import chat.dobot.bot.Autor;
import chat.dobot.bot.Contexto;
import chat.dobot.bot.DoBotException;
import chat.dobot.bot.utils.AnnotationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DoBot {

    private static final Logger logger = LoggerFactory.getLogger(DoBot.class);

    private final List<Mensagem> mensagens = new LinkedList<>();
    private Map<String, Consumer<Contexto>> estados = new HashMap<>();
    private DoBotTema doBotTema;
    private String ultimaMensagemUsuario;
    private String ultimaMensagemBot;
    private String estadoAtual;
    private final Object chatbot;

    public DoBot(Object chatbot, String mensagemInicial, DoBotTema doBotTema) {
        this.chatbot = chatbot;
        mapearEstados();
        this.estadoAtual = AnnotationsUtil.obterEstadoInicial(chatbot);
        logger.info("Estado inicial: {}", this.estadoAtual);
        adicionarMensagemInicial(mensagemInicial);
        this.doBotTema = doBotTema;
    }

    public void receberMensagem(Contexto contexto) {
        ultimaMensagemUsuario = contexto.getMensagemUsuario();

        if (!estados.containsKey(contexto.getEstado().toLowerCase())) {
            throw new DoBotException("Estado '" + contexto.getEstado() + "' não encontrado!");
        }

        estados.get(contexto.getEstado().toLowerCase()).accept(contexto);

        ultimaMensagemBot = contexto.getResposta() != null ? contexto.getResposta() : ultimaMensagemBot;
        estadoAtual = contexto.getEstado();

        adicionarMensagens(contexto);
    }

    private void adicionarMensagemInicial(String mensagemInicial) {
        if (mensagemInicial != null){
            mensagens.add(new Mensagem(Autor.BOT, mensagemInicial));
        }
    }

    private void adicionarMensagens(Contexto contexto) {
        if (contexto.getMensagemUsuario() != null) {
            mensagens.add(new Mensagem(Autor.USUARIO, contexto.getMensagemUsuario()));
        }
        if (contexto.getResposta() != null) {
            mensagens.add(new Mensagem(Autor.BOT, contexto.getResposta()));
        }
    }

    private void mapearEstados() {
        logger.debug("Iniciando mapeamento dos estados para {}.", chatbot.getClass().getSimpleName());
        this.estados = AnnotationsUtil.mapearEstados(this.chatbot);

        if (this.estados.isEmpty()) {
            throw new DoBotException("Nenhum estado mapeado para " + chatbot.getClass().getSimpleName() + "!");
        }

        logger.debug("Mapeamento dos estados concluído com sucesso.");
        logger.info("Mapeamento de estados encontrados: {}", this.estados.keySet());
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
