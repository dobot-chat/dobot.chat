package com.mychat2.domain;

import org.yorm.exception.YormException;

import java.util.ArrayList;
import java.util.List;

public abstract class MeuChat {

    private static final List<Mensagem> mensagens = new ArrayList<>();

    public final void receberMensagem(String msg) throws YormException {
        String resposta = responderMensagem(msg);
        mensagens.add(new Mensagem(msg, resposta));
    }

    protected abstract String responderMensagem(String mensagemUsuario) throws YormException;

    public List<Mensagem> getMensagens() {
        return mensagens;
    }
}
