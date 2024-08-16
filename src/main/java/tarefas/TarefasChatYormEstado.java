package tarefas;

import com.mychat2.domain.Contexto;
import com.mychat2.domain.MeuChat;
import com.mychat2.service.ChatbotService;
import org.yorm.exception.YormException;

import java.util.List;

//@Chatbot
public class TarefasChatYormEstado extends MeuChat {

    private static final ChatbotService<Tarefa> chatbotService = new ChatbotService<>(Tarefa.class);

    private static final String MENSAGEM_BOAS_VINDAS = "Olá, eu sou o Gerenciador de Tarefas. Em que posso ajudar? <br>1 - Adicionar tarefa <br>2 - Listar tarefas <br>3 - Remover tarefa";
    private static final String MENSAGEM_ADICIONAR_TAREFA = "Qual tarefa deseja adicionar? (Digite 0 para cancelar)";
    private static final String MENSAGEM_REMOVER_TAREFA = "Qual tarefa deseja remover? (Digite 0 para cancelar)";
    private static final String MENSAGEM_SEM_TAREFAS_CADASTRADAS = "Não há tarefas cadastradas. <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_OPERACAO_CANCELADA = "Operação cancelada. <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_OPCAO_INVALIDA = "Opção inválida! <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_ADICAO_TAREFA_SUCESSO = "Tarefa adicionada com sucesso! <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_REMOCAO_TAREFA_SUCESSO = "Tarefa removida com sucesso! <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_TAREFA_NAO_ENCONTRADA = "Tarefa não encontrada! <br>Digite 0 se quiser ver as opções novamente.";

    public TarefasChatYormEstado() {
        configurarEstados();
    }

    private void configurarEstados() {
        addEstado("inicial", this::menuInicial);
        addEstado("adicionarTarefa", this::adicionarTarefa);
        addEstado("removerTarefa", this::removerTarefa);
    }

    private void menuInicial(Contexto contexto) {
        String msg = contexto.getMensagemUsuario();
        System.out.println("Recebendo mensagem: " + msg);

        if (msg.matches("[0-3]")) {
            processarComando(contexto);
        } else {
            contexto.responder(MENSAGEM_BOAS_VINDAS);
        }
    }

    private void processarComando(Contexto contexto) {
        try {
            switch (contexto.getMensagemUsuario()) {
                case "0":
                    contexto.responder(MENSAGEM_BOAS_VINDAS, "inicial");
                    break;
                case "1":
                    contexto.responder(MENSAGEM_ADICIONAR_TAREFA, "adicionarTarefa");
                    break;
                case "2":
                    contexto.responder(listarTarefas(), "inicial");
                    break;
                case "3":
                    if (chatbotService.getAll().isEmpty()) {
                        contexto.responder(MENSAGEM_SEM_TAREFAS_CADASTRADAS, "inicial");
                    } else {
                        contexto.responder(listarTarefas() + "<br>" + MENSAGEM_REMOVER_TAREFA, "removerTarefa");
                    }
                    break;
                default:
                    contexto.responder(MENSAGEM_OPCAO_INVALIDA, "inicial");
                    break;
            }
        } catch (YormException e) {
            throw new RuntimeException(e);
        }
    }

    private void adicionarTarefa(Contexto contexto) {
        try {
            String msg = contexto.getMensagemUsuario();
            if (!msg.equals("0")) {
                Tarefa tarefa = new Tarefa(0, msg);
                chatbotService.saveObj(tarefa);
                contexto.responder(MENSAGEM_ADICAO_TAREFA_SUCESSO, "inicial");
            } else {
                contexto.responder(MENSAGEM_OPERACAO_CANCELADA, "inicial");
            }
        } catch (YormException e) {
            throw new RuntimeException(e);
        }
    }

    private String listarTarefas() {
        try {
            List<Tarefa> tarefas = chatbotService.getAll();
            if (tarefas.isEmpty()) {
                return MENSAGEM_SEM_TAREFAS_CADASTRADAS;
            } else {
                StringBuilder listaDeTarefas = new StringBuilder();
                for (Tarefa tarefa : tarefas) {
                    listaDeTarefas.append(tarefa.id()).append(" - ").append(tarefa.descricao()).append("<br>");
                }
                return listaDeTarefas.toString();
            }
        } catch (YormException e) {
            throw new RuntimeException(e);
        }
    }

    private void removerTarefa(Contexto contexto) {
            try {
                String msg = contexto.getMensagemUsuario();
                int idTarefa = Integer.parseInt(msg);

                Tarefa tarefaParaRemover = chatbotService.getObjById(idTarefa);

                if (tarefaParaRemover != null) {
                    chatbotService.deleteObj(idTarefa);
                    contexto.responder(MENSAGEM_REMOCAO_TAREFA_SUCESSO, "inicial");
                }  else if (msg.equals("0")) {
                    contexto.responder(MENSAGEM_OPERACAO_CANCELADA, "inicial");
                } else {
                    contexto.responder(MENSAGEM_TAREFA_NAO_ENCONTRADA, "inicial");
                }
            } catch (NumberFormatException e) {
                contexto.responder(MENSAGEM_OPCAO_INVALIDA, "inicial");
            } catch (YormException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    protected void processarMensagem(Contexto contexto) throws YormException {
    }
}
