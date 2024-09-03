package com.mychat2.dominio;

import com.mychat2.enums.Autor;
import com.mychat2.excecao.MyChatExcecao;
import com.mychat2.utils.AnotacoesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class MyChat {

    private static final Logger logger = LoggerFactory.getLogger(MyChat.class);

    private final List<Mensagem> mensagens = new LinkedList<>();
    private Map<String, Consumer<Contexto>> estados = new HashMap<>();
    private String ultimaMensagemUsuario;
    private String ultimaMensagemChatbot;
    private String estadoAtual;
    private final Object chatbot;

    public MyChat(Object chatbot, String mensagemInicial) {
        this.chatbot = chatbot;
        mapearEstados();
        this.estadoAtual = AnotacoesUtil.obterEstadoInicial(chatbot);
        adicionarMensagemInicial(mensagemInicial);
        logger.info("Estado inicial: {}", this.estadoAtual);
    }

    public void receberMensagem(Contexto contexto) {
        ultimaMensagemUsuario = contexto.getMensagemUsuario();

        if (!estados.containsKey(contexto.getEstado().toLowerCase())) {
            throw new MyChatExcecao("Estado '" + contexto.getEstado() + "' não encontrado!");
        }

        estados.get(contexto.getEstado().toLowerCase()).accept(contexto);

        ultimaMensagemChatbot = contexto.getResposta() != null ? contexto.getResposta() : ultimaMensagemChatbot;
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
        logger.info("Iniciando mapeamento dos estados para {}.", chatbot.getClass().getSimpleName());
        this.estados = AnotacoesUtil.mapearEstados(this.chatbot);

        if (this.estados.isEmpty()) {
            throw new MyChatExcecao("Nenhum estado mapeado para " + chatbot.getClass().getSimpleName() + "!");
        }

        logger.info("Mapeamento dos estados concluído com sucesso.");
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

    public String getUltimaMensagemChatbot() {
        return ultimaMensagemChatbot;
    }

    public Object getChatbot() {
        return chatbot;
    }
}
