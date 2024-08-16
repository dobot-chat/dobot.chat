package com.mychat2.domain;

import org.yorm.exception.YormException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class MeuChat {

    private final List<Mensagem> mensagens = new ArrayList<>();
    private final Map<String, Consumer<Contexto>> estados = new HashMap<>();
    private String msgUsuario;
    private String respostaBot;
    private String estado;

    public final void receberMensagem(Contexto contexto) throws YormException {
        msgUsuario = contexto.getMensagem();

        if (estados.containsKey(contexto.getEstado())) {
            estados.get(contexto.getEstado()).accept(contexto);
        } else {
            processarMensagem(contexto);
        }

        respostaBot = contexto.getResposta();
        estado = contexto.getEstado();

        mensagens.add(new Mensagem(contexto.getMensagem(), contexto.getResposta()));
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
