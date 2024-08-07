package tarefas;


import com.mychat2.annotations.Chatbot;
import com.mychat2.domain.Mensagem;
import com.mychat2.domain.MeuChat;

import java.util.ArrayList;
import java.util.List;

@Chatbot
public class TarefasChat extends MeuChat {

    private final List<String> tarefas = new ArrayList<>();
    private String estadoAtual = ESTADO_INICIAL;

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
    public void receberMensagem(String msg) {
        System.out.println("Recebendo mensagem: " + msg);

        String msgTratada = msg == null ? "0" : msg;

        // Verifica o estado atual para decidir como processar a mensagem
        if (estadoAtual.equals(ESTADO_INICIAL)) {
            if (msgTratada.matches("[0-3]")) {
                processarComando(msgTratada);
            } else {
                setResposta(MENSAGEM_OPCAO_INVALIDA);
            }
        } else {
            processarMensagem(msgTratada);
        }

        addMensagem(new Mensagem(msg, getResposta()));
    }

    private void processarComando(String comando) {
        switch (comando) {
            case "0":
                estadoAtual = ESTADO_INICIAL;
                setResposta(MENSAGEM_BOAS_VINDAS);
                break;
            case "1":
                estadoAtual = ESTADO_ADICIONAR_TAREFA;
                setResposta(MENSAGEM_ADICIONAR_TAREFA);
                break;
            case "2":
                setResposta(listarTarefas());
                break;
            case "3":
                if (tarefas.isEmpty()) {
                    setResposta(MENSAGEM_SEM_TAREFAS_CADASTRADAS);
                } else {
                    estadoAtual = ESTADO_REMOVER_TAREFA;
                    setResposta(listarTarefas() + "<br>" + MENSAGEM_REMOVER_TAREFA);
                }
                break;
            default:
                setResposta(MENSAGEM_OPCAO_INVALIDA);
                break;
        }
    }

    @Override
    public void processarMensagem(String msg) {
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
                setResposta(MENSAGEM_OPCAO_INVALIDA);
                estadoAtual = ESTADO_INICIAL;
                break;
        }
    }

    private void adicionarTarefa(String msg) {
        if (!msg.equals("0")) {
            tarefas.add(msg);
            setResposta(MENSAGEM_ADICAO_TAREFA_SUCESSO);
        } else {
            setResposta(MENSAGEM_OPERACAO_CANCELADA);
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
                setResposta(MENSAGEM_REMOCAO_TAREFA_SUCESSO);
            } else {
                setResposta(msg.equals("0") ? MENSAGEM_OPERACAO_CANCELADA : MENSAGEM_TAREFA_NAO_ENCONTRADA);
            }
        } catch (NumberFormatException e) {
            setResposta(MENSAGEM_OPCAO_INVALIDA);
        }
        estadoAtual = ESTADO_INICIAL;
    }
}
