package tarefas;

import com.mychat2.anotacoes.EstadoChat;
import com.mychat2.dominio.Contexto;
import com.mychat2.servico.MyChatServico;
import org.yorm.exception.YormException;

//@Chatbot
public class GerenciadorDeTarefasChatYorm {

    private static final MyChatServico<Tarefa> MYCHAT_SERVICO = new MyChatServico<>(Tarefa.class);
    private String estadoAtual = "inicial";
    private String resposta = "";

    private static final String MENSAGEM_BOAS_VINDAS = "Olá, eu sou o Gerenciador de Tarefas, em que posso ajudar? <br>1 - Adicionar tarefa <br>2 - Listar tarefas <br>3 - Remover tarefa";
    private static final String MENSAGEM_ADICIONAR_TAREFA = "Qual tarefa deseja adicionar? (Digite 0 para cancelar)";
    private static final String MENSAGEM_REMOVER_TAREFA = "Qual tarefa deseja remover? (Digite 0 para cancelar)";
    private static final String MENSAGEM_SEM_TAREFAS_CADASTRADAS = "Não há tarefas cadastradas. <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_OPERACAO_CANCELADA = "Operação cancelada. <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_OPCAO_INVALIDA = "Opção inválida! <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_ADICAO_TAREFA_SUCESSO = "Tarefa adicionada com sucesso! <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_REMOCAO_TAREFA_SUCESSO = "Tarefa removida com sucesso! <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_TAREFA_NAO_ENCONTRADA = "Tarefa não encontrada! <br>Digite 0 se quiser ver as opções novamente.";

    @EstadoChat(estado = "inicial", inicial = true)
    public void processarMensagem(Contexto contexto) throws YormException {
        String msg = contexto.getMensagemUsuario();
        System.out.println("Recebendo mensagem: " + msg);

        if (estadoAtual.equals("inicial")) {
            if (msg.matches("[0-3]")) {
                processarComando(msg);
            } else {
                resposta = MENSAGEM_OPCAO_INVALIDA;
            }
        } else {
            executarOperacao(msg);
        }

        contexto.responder(resposta);
    }

    private void processarComando(String comando) throws YormException {
        switch (comando) {
            case "0":
                estadoAtual = "inicial";
                resposta = MENSAGEM_BOAS_VINDAS;
                break;
            case "1":
                estadoAtual = "adicionarTarefa";
                resposta = MENSAGEM_ADICIONAR_TAREFA;
                break;
            case "2":
                resposta = listarTarefas();
                break;
            case "3":
                if (MYCHAT_SERVICO.buscarTodos().isEmpty()) {
                    resposta = MENSAGEM_SEM_TAREFAS_CADASTRADAS;
                } else {
                    estadoAtual = "removerTarefa";
                    resposta = listarTarefas() + "<br>" + MENSAGEM_REMOVER_TAREFA;
                }
                break;
            default:
                resposta = MENSAGEM_OPCAO_INVALIDA;
                break;
        }
    }

    public void executarOperacao(String msg) throws YormException {
        switch (estadoAtual) {
            case "adicionarTarefa":
                adicionarTarefa(msg);
                break;
            case "removerTarefa":
                removerTarefa(msg);
                break;
        }
    }

    private void adicionarTarefa(String msg) throws YormException {
        if (!msg.equals("0")) {
            Tarefa tarefa = new Tarefa(0, msg);
            MYCHAT_SERVICO.salvar(tarefa);
            resposta = MENSAGEM_ADICAO_TAREFA_SUCESSO;
        } else {
            resposta = MENSAGEM_OPERACAO_CANCELADA;
        }

        estadoAtual = "inicial";
    }

    private String listarTarefas() throws YormException {
        if (MYCHAT_SERVICO.buscarTodos().isEmpty()) {
            return MENSAGEM_SEM_TAREFAS_CADASTRADAS;
        } else {
            StringBuilder listaDeTarefas = new StringBuilder();
            for (Tarefa trf : MYCHAT_SERVICO.buscarTodos()) {
                listaDeTarefas.append(trf.id()).append(" - ").append(trf.descricao()).append("<br>");
            }
            return listaDeTarefas.toString();
        }
    }

    private void removerTarefa(String msg) throws YormException {
        try {
            int idTarefa = Integer.parseInt(msg);

            Tarefa tarefaParaRemover = MYCHAT_SERVICO.buscarPorId(idTarefa);

            if (tarefaParaRemover != null) {
                MYCHAT_SERVICO.deletarPorId(idTarefa);
                resposta = MENSAGEM_REMOCAO_TAREFA_SUCESSO;
            } else {
                resposta = msg.equals("0") ? MENSAGEM_OPERACAO_CANCELADA : MENSAGEM_TAREFA_NAO_ENCONTRADA;
            }
        } catch (NumberFormatException e) {
            resposta = MENSAGEM_OPCAO_INVALIDA;
        }

        estadoAtual = "inicial";
    }
}
