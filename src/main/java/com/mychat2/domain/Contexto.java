package com.mychat2.domain;

public class Contexto {

    private final String mensagemUsuario;
    private String estado;
    private String resposta;

    public Contexto(String mensagemUsuario, String estado) {
        this.mensagemUsuario = mensagemUsuario;
        this.estado = estado;
    }

    public String getMensagemUsuario() {
        return mensagemUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void mudarEstado(String estado) {
        this.estado = estado;
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
