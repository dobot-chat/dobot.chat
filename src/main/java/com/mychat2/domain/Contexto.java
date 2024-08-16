package com.mychat2.domain;

public class Contexto {

    private final String mensagem;
    private String estado;
    private String resposta;

    public Contexto(String mensagem, String estado) {
        this.mensagem = mensagem;
        this.estado = estado;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getEstado() {
        return estado;
    }

    public void responder(String resposta) {
        this.resposta = resposta;
    }

    public void responder(String resposta, String estado) {
        this.resposta = resposta;
        this.estado = estado;
    }

    public String getResposta() {
        return resposta;
    }
}
