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

/**
 * Classe que representa um chatbot implementado pelo usuário dev.
 * Esta classe implementa o Command, do padrão Front Controller
 */
public class DoBot {

    private static final Logger logger = LoggerFactory.getLogger(DoBot.class);

    private final List<Mensagem> mensagens = new LinkedList<>();
    private Map<String, BotStateMethod> estados;
    public static final String ESTADO_INICIAL = "main";
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
     * Método principal do <a href="https://en.wikipedia.org/wiki/Front_controller">Front Controller</a>.
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

    /**
     * Define a mensagem inicial ao chat.
     * Esta é a primeira mensagem exibida ao usuário.
     * @param mensagemInicial mensagem inicial
     */
    public void setMensagemInicial(String mensagemInicial) {
        if (mensagemInicial == null){
            throw new IllegalArgumentException("A mensagem inicial não pode ser nula!");
        }
        mensagens.add(new Mensagem(Autor.BOT, mensagemInicial));
    }

    /**
     * Adiciona as mensagens ao chat.
     * @param contexto
     */
    private void adicionarMensagens(Contexto contexto) {
        if (contexto.getMensagemUsuario() != null) {
            mensagens.add(new Mensagem(Autor.USUARIO, contexto.getMensagemUsuario()));
        }
        if (contexto.getResposta() != null) {
            mensagens.add(new Mensagem(Autor.BOT, contexto.getResposta()));
        }
    }

    /**
     * Retorna as mensagens do chat.
     * @return mensagens do chat
     */
    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    /**
     * Define o estado atual do chat.
     * @param estado estado atual
     */
    public void setEstadoAtual(String estado) {
        if(estado == null){
            throw new IllegalArgumentException("O estado atual não pode ser nulo!");
        }
        this.estadoAtual = estado.toLowerCase();
    }

    /**
     * Retorna o estado atual do chat.
     * @return estado atual
     */
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

    /**
     * Retorna o tema do chat.
     * @return tema do chat
     */
    public DoBotTema getDoBotTema() {
        return doBotTema;
    }

    /**
     * Define o tema do chat.
     * @param doBotTema tema do chat
     */
    public void setDoBotTema(DoBotTema doBotTema) {
        this.doBotTema = doBotTema;
    }

    /**
     * Define os estados do chat.
     */
    public void setEstados(Map<String, BotStateMethod> estados) {
        if(!estados.containsKey("main")) {
            throw new DoBotException("Nenhum estado inicial definido!");
        }
        this.estadoAtual = DoBot.ESTADO_INICIAL;
        this.estados = estados;
    }

    /**
     * Retorna o id do chat.
     * @return id do chat
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna o nome do chat.
     * @return nome do chat
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a descrição do chat.
     * @return descrição do chat
     */
    public String getDescricao() {
        return descricao;
    }


}
