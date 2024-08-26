package com.mychat2.domain;

import com.mychat2.enums.Autor;
import com.mychat2.exception.ChatbotException;
import com.mychat2.util.BuscaAnotacoesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class MeuChat {

    private static final Logger logger = LoggerFactory.getLogger(MeuChat.class);

    private final List<Mensagem> mensagens = new LinkedList<>();
    private Map<String, Consumer<Contexto>> estados = new HashMap<>();
    private String msgUsuario;
    private String respostaBot;
    private String estado;
    private final Object chatbot;

    public MeuChat(Object chatbot) {
        this.chatbot = chatbot;
        mapearEstados();
    }

    public void receberMensagem(Contexto contexto) {
        msgUsuario = contexto.getMensagemUsuario();

        if (!estados.containsKey(contexto.getEstado())) {
            throw new ChatbotException("Estado '" + contexto.getEstado() + "' não encontrado.");
        }

        estados.get(contexto.getEstado()).accept(contexto);

        respostaBot = contexto.getResposta();
        estado = contexto.getEstado();

        adicionarMensagens(contexto);
    }

    private void adicionarMensagens(Contexto contexto) {
        if (contexto.getMensagemUsuario() != null) {
            mensagens.add(new Mensagem(Autor.USUARIO, contexto.getMensagemUsuario()));
        }
        if (contexto.getResposta() != null) {
            mensagens.add(new Mensagem(Autor.BOT, contexto.getResposta()));
        }
    }

    public void mapearEstados() {
        logger.info("Iniciando mapeamento dos estados para {}.", chatbot.getClass().getSimpleName());
        this.estados = BuscaAnotacoesUtil.mapearEstados(this.chatbot);

        if (this.estados.isEmpty()) {
            throw new ChatbotException("Nenhum estado mapeado para " + chatbot.getClass().getSimpleName());
        }

        logger.info("Mapeamento dos estados concluído com sucesso.");
        logger.info("Mapeamento de estados encontrados: {}", this.estados.keySet());
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    public String getEstado() {
        return estado;
    }

    public String getMsgUsuario() {
        return msgUsuario;
    }

    public String getRespostaBot() {
        return respostaBot;
    }

    public Object getChatbot() {
        return chatbot;
    }
}
