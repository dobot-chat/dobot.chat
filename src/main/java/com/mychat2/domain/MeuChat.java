package com.mychat2.domain;

import org.yorm.exception.YormException;

import java.util.ArrayList;
import java.util.List;

public abstract class MeuChat {

    protected String resposta;
    protected static final List<Mensagem> mensagens = new ArrayList<>();

    public MeuChat() {
        this.resposta = "";
    }

    public abstract void receberMensagem(String msg) throws YormException;

//    protected abstract void processarMensagem(String msg);

    public List<Mensagem> getMensagens() {
        return mensagens;
    }
}
