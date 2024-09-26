package chat.dobot.bot;

import chat.dobot.bot.service.DoBotService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Contexto {

    private final String mensagemUsuario;
    private String estado;
    private final List<String> respostas;
    private final Map<String, DoBotService<Record>> servicos;

    public Contexto(String mensagemUsuario, String estado, Map<String, DoBotService<Record>> servicos) {
        this.mensagemUsuario = mensagemUsuario;
        this.estado = estado.toLowerCase();
        this.servicos = servicos;
        respostas = new ArrayList<>(20);
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
        this.respostas.add(resposta);
    }

    public List<String> getRespostas() {
        return respostas;
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
