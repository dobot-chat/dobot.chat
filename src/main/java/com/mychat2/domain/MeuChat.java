package com.mychat2.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class MeuChat {

    private String resposta;
    private static final List<Mensagem> mensagens = new ArrayList<>();

    public MeuChat() {
        this.resposta = "";
    }

    protected String getResposta() {
        return resposta;
    }

    protected void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public abstract void receberMensagem(String msg);

    protected abstract void processarMensagem(String msg);

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    protected void addMensagem(Mensagem mensagem) {
        mensagens.add(mensagem);
    }
}
