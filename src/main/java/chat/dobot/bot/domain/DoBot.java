package chat.dobot.bot.domain;

import chat.dobot.bot.Autor;
import chat.dobot.bot.BotStateMethod;
import chat.dobot.bot.Contexto;
import chat.dobot.bot.DoBotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DoBot {

    private static final Logger logger = LoggerFactory.getLogger(DoBot.class);

    private final List<Mensagem> mensagens = new LinkedList<>();
    private Map<String, BotStateMethod> estados;
    private DoBotTema doBotTema;
    private String ultimaMensagemUsuario;
    private String ultimaMensagemBot;
    private String estadoAtual;

    private final String id;
    private final String nome;
    private final String descricao;


    public DoBot(String id, String nome, String descricao){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        estados = new HashMap<>();
    }

    /**
     * Encaminha a mensagem do usuário através do chat, seleciona o estado e executa o estado correspondente.
     * @param contexto contexto do chat
     */
    public void receberMensagem(Contexto contexto) {
        ultimaMensagemUsuario = contexto.getMensagemUsuario();

        if (!estados.containsKey(contexto.getEstado())) {
            throw new DoBotException("Estado '" + contexto.getEstado() + "' não encontrado!");
        }

        estados.get(contexto.getEstado()).execute(contexto);

        ultimaMensagemBot = contexto.getResposta() != null ? contexto.getResposta() : ultimaMensagemBot;

        this.setEstadoAtual(contexto.getEstado());

        adicionarMensagens(contexto);
    }

    public void adicionarMensagemInicial(String mensagemInicial) {
        if (mensagemInicial == null){
            throw new IllegalArgumentException("A mensagem inicial não pode ser nula!");
        }
        mensagens.add(new Mensagem(Autor.BOT, mensagemInicial));
    }

    private void adicionarMensagens(Contexto contexto) {
        if (contexto.getMensagemUsuario() != null) {
            mensagens.add(new Mensagem(Autor.USUARIO, contexto.getMensagemUsuario()));
        }
        if (contexto.getResposta() != null) {
            mensagens.add(new Mensagem(Autor.BOT, contexto.getResposta()));
        }
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    public String getEstadoAtual() {
        if(estadoAtual == null){
            throw new IllegalStateException("O estado atual não foi definido! : null");
        }
        return estadoAtual;
    }

    public String getUltimaMensagemUsuario() {
        return ultimaMensagemUsuario;
    }

    public String getUltimaMensagemBot() {
        return ultimaMensagemBot;
    }

    public DoBotTema getDoBotTema() {
        return doBotTema;
    }

    public void setDoBotTema(DoBotTema doBotTema) {
        this.doBotTema = doBotTema;
    }

    public void setEstados(Map<String, BotStateMethod> estados) {
        if(!estados.containsKey("main")) {
            throw new DoBotException("Nenhum estado inicial definido!");
        }
        this.estados = estados;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setEstadoAtual(String estado) {
        if(estado == null){
            throw new IllegalArgumentException("O estado atual não pode ser nulo!");
        }
        this.estadoAtual = estado.toLowerCase();
    }

}
