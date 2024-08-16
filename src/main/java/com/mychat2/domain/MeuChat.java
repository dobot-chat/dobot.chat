package com.mychat2.domain;

import com.mychat2.enums.Autor;
import org.yorm.exception.YormException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class MeuChat {

    private final List<Mensagem> mensagens = new LinkedList<>();
    private final Map<String, Consumer<Contexto>> estados = new HashMap<>();
    private String msgUsuario;
    private String respostaBot;
    private String estado;

    public final void receberMensagem(Contexto contexto) throws YormException {
        msgUsuario = contexto.getMensagemUsuario();

        if (estados.containsKey(contexto.getEstado())) {
            estados.get(contexto.getEstado()).accept(contexto);
        } else {
            processarMensagem(contexto);
        }

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

    protected abstract void processarMensagem(Contexto contexto) throws YormException;

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    protected void addEstado(String key, Consumer<Contexto> action) {
        estados.put(key, action);
    }

    public String getEstadoAtual() {
        return estado;
    }

    public String getMsgUsuario() {
        return msgUsuario;
    }

    public String getRespostaBot() {
        return respostaBot;
    }
}
