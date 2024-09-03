package com.mychat2.dominio;

import com.mychat2.excecao.MyChatExcecao;
import com.mychat2.servico.MyChatServico;

import java.util.Map;

public class Contexto {

    private final String mensagemUsuario;
    private String estado;
    private String resposta;
    private final Map<String, MyChatServico<Record>> servicos;

    public Contexto(String mensagemUsuario, String estado, Map<String, MyChatServico<Record>> servicos) {
        this.mensagemUsuario = mensagemUsuario;
        this.estado = estado;
        this.servicos = servicos;
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

    public <T extends Record> MyChatServico<T> getServico(Class<T> recordClass) {
        MyChatServico<?> servico = servicos.get(recordClass.getSimpleName());

        if (servico == null) {
            throw new MyChatExcecao("Serviço não encontrado para a classe " + recordClass.getSimpleName() + "!");
        }

        @SuppressWarnings("unchecked")
        MyChatServico<T> servicoTipado = (MyChatServico<T>) servico;

        return servicoTipado;
    }
}
