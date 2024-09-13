package chat.dobot.dominio;

import chat.dobot.excecao.DoBotExcecao;
import chat.dobot.servico.DoBotServico;

import java.util.Map;

public class Contexto {

    private final String mensagemUsuario;
    private String estado;
    private String resposta;
    private final Map<String, DoBotServico<Record>> servicos;

    public Contexto(String mensagemUsuario, String estado, Map<String, DoBotServico<Record>> servicos) {
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

    public <T extends Record> DoBotServico<T> getServico(Class<T> recordClass) {
        DoBotServico<?> servico = servicos.get(recordClass.getSimpleName());

        if (servico == null) {
            throw new DoBotExcecao("Serviço não encontrado para a classe " + recordClass.getSimpleName() + "!");
        }

        @SuppressWarnings("unchecked")
        DoBotServico<T> servicoTipado = (DoBotServico<T>) servico;

        return servicoTipado;
    }
}
