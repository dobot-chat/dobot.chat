package exemplos.gerenciadortarefas;

import com.mychat2.anotacoes.EstadoChat;
import com.mychat2.anotacoes.MyChat;
import com.mychat2.dominio.Contexto;

import java.util.LinkedList;
import java.util.List;

//@MyChat
public class GerenciadorDeTarefasChat {

    private final List<String> tarefas = new LinkedList<>();
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

    private static final String ESTADO_INICIAL = "inicial";
    private static final String ESTADO_ADICIONAR_TAREFA = "adicionarTarefa";
    private static final String ESTADO_REMOVER_TAREFA = "removerTarefa";


    @EstadoChat(estado = "inicial", inicial = true)
    public void processarMensagem(Contexto contexto) {
        String msg = contexto.getMensagemUsuario();
        System.out.println("Recebendo mensagem: " + msg);

        if (estadoAtual.equals(ESTADO_INICIAL)) {
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

    private void processarComando(String comando) {
        switch (comando) {
            case "0":
                estadoAtual = ESTADO_INICIAL;
                resposta = MENSAGEM_BOAS_VINDAS;
                break;
            case "1":
                estadoAtual = ESTADO_ADICIONAR_TAREFA;
                resposta = MENSAGEM_ADICIONAR_TAREFA;
                break;
            case "2":
                resposta = listarTarefas();
                break;
            case "3":
                if (tarefas.isEmpty()) {
                    resposta = MENSAGEM_SEM_TAREFAS_CADASTRADAS;
                } else {
                    estadoAtual = ESTADO_REMOVER_TAREFA;
                    resposta = listarTarefas() + "<br>" + MENSAGEM_REMOVER_TAREFA;
                }
                break;
            default:
                resposta = MENSAGEM_OPCAO_INVALIDA;
                break;
        }
    }

    public void executarOperacao(String msg) {
        switch (estadoAtual) {
            case ESTADO_ADICIONAR_TAREFA:
                adicionarTarefa(msg);
                break;
            case ESTADO_REMOVER_TAREFA:
                removerTarefa(msg);
                break;
        }
    }

    private void adicionarTarefa(String msg) {
        if (!msg.equals("0")) {
            tarefas.add(msg);
            resposta = MENSAGEM_ADICAO_TAREFA_SUCESSO;
        } else {
            resposta = MENSAGEM_OPERACAO_CANCELADA;
        }

        estadoAtual = ESTADO_INICIAL;
    }

    private String listarTarefas() {
        if (tarefas.isEmpty()) {
            return MENSAGEM_SEM_TAREFAS_CADASTRADAS;
        } else {
            StringBuilder listaDeTarefas = new StringBuilder();
            for (int i = 0; i < tarefas.size(); i++) {
                listaDeTarefas.append((i + 1)).append(" - ").append(tarefas.get(i)).append("<br>");
            }
            return listaDeTarefas.toString();
        }
    }

    private void removerTarefa(String msg) {
        try {
            int indice = Integer.parseInt(msg) - 1;
            if (indice >= 0 && indice < tarefas.size()) {
                tarefas.remove(indice);
                resposta = MENSAGEM_REMOCAO_TAREFA_SUCESSO;
            } else {
                resposta = msg.equals("0") ? MENSAGEM_OPERACAO_CANCELADA : MENSAGEM_TAREFA_NAO_ENCONTRADA;
            }
        } catch (NumberFormatException e) {
            resposta = MENSAGEM_OPCAO_INVALIDA;
        }

        estadoAtual = ESTADO_INICIAL;
    }
}
