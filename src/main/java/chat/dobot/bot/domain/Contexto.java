package chat.dobot.bot.domain;

import chat.dobot.bot.DoBotException;
import chat.dobot.bot.service.DoBotService;

import java.util.Map;

public class Contexto {

    private final String mensagemUsuario;
    private String estado;
    private String resposta;
    private final Map<String, DoBotService<Record>> servicos;

    public Contexto(String mensagemUsuario, String estado, Map<String, DoBotService<Record>> servicos) {
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

    public <T extends Record> DoBotService<T> getServico(Class<T> recordClass) {
        DoBotService<?> servico = servicos.get(recordClass.getSimpleName());

        if (servico == null) {
            throw new DoBotException("Serviço não encontrado para a classe " + recordClass.getSimpleName() + "!");
        }

        @SuppressWarnings("unchecked")
        DoBotService<T> servicoTipado = (DoBotService<T>) servico;

        return servicoTipado;
    }
}
