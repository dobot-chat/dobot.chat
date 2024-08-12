package tarefas;


import com.mychat2.annotations.Chatbot;
import com.mychat2.domain.Mensagem;
import com.mychat2.domain.MeuChat;
import com.mychat2.service.ChatbotService;
import org.yorm.exception.YormException;

import java.util.List;

@Chatbot
public class TarefasChatYorm extends MeuChat {

    private static final ChatbotService<Tarefa> chatbotService = new ChatbotService<>(Tarefa.class);
    private String estadoAtual = ESTADO_INICIAL;
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


    @Override
    protected String responderMensagem(String msg) throws YormException {
        System.out.println("Recebendo mensagem: " + msg);

        String msgTratada = msg == null ? "0" : msg;

        // Verifica o estado atual para decidir como processar a mensagem
        if (estadoAtual.equals(ESTADO_INICIAL)) {
            if (msgTratada.matches("[0-3]")) {
                processarComando(msgTratada);
            } else {
                resposta = MENSAGEM_OPCAO_INVALIDA;
            }
        } else {
            processarMensagem(msgTratada);
        }

        return resposta;
    }

    private void processarComando(String comando) throws YormException {
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
                if (chatbotService.getAll().isEmpty()) {
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

    public void processarMensagem(String msg) throws YormException {
        switch (estadoAtual) {
            case ESTADO_ADICIONAR_TAREFA:
                adicionarTarefa(msg);
                break;
            case ESTADO_REMOVER_TAREFA:
                removerTarefa(msg);
                break;
            case ESTADO_INICIAL:
                // Não deve chegar aqui, pois já lidamos com isso em processarComando
                break;
            default:
                resposta = MENSAGEM_OPCAO_INVALIDA;
                estadoAtual = ESTADO_INICIAL;
                break;
        }
    }

    private void adicionarTarefa(String msg) throws YormException {
        if (!msg.equals("0")) {
            Tarefa tarefa = new Tarefa(0, msg);
            chatbotService.saveObj(tarefa);
            resposta = MENSAGEM_ADICAO_TAREFA_SUCESSO;
        } else {
            resposta = MENSAGEM_OPERACAO_CANCELADA;
        }
        estadoAtual = ESTADO_INICIAL;
    }

    private String listarTarefas() throws YormException {
        if (chatbotService.getAll().isEmpty()) {
            return MENSAGEM_SEM_TAREFAS_CADASTRADAS;
        } else {
            StringBuilder listaDeTarefas = new StringBuilder();
            for (Tarefa trf : chatbotService.getAll()) {
                listaDeTarefas.append(trf.id()).append(" - ").append(trf.descricao()).append("<br>");
            }
            return listaDeTarefas.toString();
        }
    }

    private void removerTarefa(String msg) throws YormException {
        try {
            int indice = Integer.parseInt(msg);
            List<Tarefa> tarefas = chatbotService.getAll();
            if (indice > 0 && indice <= tarefas.size()) {
                chatbotService.deleteObj(indice);
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
