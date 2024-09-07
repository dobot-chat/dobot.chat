package exemplos.agendaconsultas;

import com.mychat2.anotacoes.Chatbot;
import com.mychat2.anotacoes.EstadoChat;
import com.mychat2.dominio.Contexto;
import org.yorm.exception.YormException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

@Chatbot
public class AgendaConsultasChat {

    private ConsultaBuilder consultaBuilder;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String MENSAGEM_MENU_INICIAL = "Olá, eu sou o Gerenciador de Consultas. Em que posso ajudar? <br>1 - Agendar consulta <br>2 - Listar consultas <br>3 - Cancelar consulta <br>4 - Buscar consultas por paciente";
    private static final String MENSAGEM_OPCAO_INVALIDA = "Opção inválida! <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_SEM_CONSULTAS_CADASTRADAS = "Não há consultas cadastradas. <br>Digite 0 se quiser ver as opções novamente.";
    private static final String MENSAGEM_CANCELAR_CONSULTA = "Qual consulta deseja cancelar? (Digite 0 para cancelar)";
    private static final String MENSAGEM_INFORMAR_PACIENTE = "Informe o nome do paciente (Digite 0 para cancelar)";
    private static final String MENSAGEM_INFORMAR_MEDICO = "Informe o médico que irá atender a consulta (Digite 0 para cancelar)";
    private static final String MENSAGEM_INFORMAR_DATA_HORA = "Informe a data e horário da consulta (Digite 0 para cancelar)";
    private static final String MENSAGEM_OPERACAO_CANCELADA = "Operação cancelada. <br>Digite 0 se quiser ver as opções novamente.";

    @EstadoChat(inicial = true)
    public void menuInicial(Contexto contexto) throws YormException {
        String msg = contexto.getMensagemUsuario();

        if (msg.matches("[0-4]")) {
            processarComando(contexto);
        } else {
            contexto.responder(MENSAGEM_OPCAO_INVALIDA);
        }

    }

    private void processarComando(Contexto contexto) throws YormException {
        switch (contexto.getMensagemUsuario()) {
            case "0":
                contexto.responder(MENSAGEM_MENU_INICIAL);
                break;
            case "1":
                contexto.responder(MENSAGEM_INFORMAR_PACIENTE, "informarPaciente");
                break;
            case "2":
                contexto.responder(listarConsultas(contexto));
                break;
            case "3":
                if (contexto.getServico(Consulta.class).buscarTodos().isEmpty()) {
                    contexto.responder(MENSAGEM_SEM_CONSULTAS_CADASTRADAS);
                } else {
                    contexto.responder(listarConsultas(contexto) + "<br>" + MENSAGEM_CANCELAR_CONSULTA, "cancelarConsulta");
                }
                break;
            case "4":
                contexto.responder(MENSAGEM_INFORMAR_PACIENTE, "buscarConsultasPorPaciente");
                break;
            default:
                contexto.responder(MENSAGEM_OPCAO_INVALIDA);
                break;
        }
    }

    private String listarConsultas(Contexto contexto) throws YormException {
        StringBuilder sb = new StringBuilder();
        List<Consulta> consultas = contexto.getServico(Consulta.class).buscarTodos();

        if (!consultas.isEmpty()) sb.append("Consultas agendadas: <br>");
        else sb.append(MENSAGEM_SEM_CONSULTAS_CADASTRADAS);

        for (Consulta consulta : consultas) {
            sb.append("(").append(consulta.Id()).append(") ").append("Paciente: ").append(consulta.paciente()).append(" - ");
            sb.append("Medico: ").append(consulta.medico()).append(" - ");
            sb.append("Data e Hora: ").append(formatter.format(consulta.dataHora())).append("<br>");
        }
        return sb.toString();
    }

    @EstadoChat
    public void informarPaciente(Contexto contexto) {
        String nomePaciente = contexto.getMensagemUsuario();

        if (!nomePaciente.equals("0")) {
            consultaBuilder = new ConsultaBuilder();
            consultaBuilder.setPaciente(nomePaciente);
            contexto.responder(MENSAGEM_INFORMAR_MEDICO, "informarMedico");
        } else {
            contexto.responder(MENSAGEM_OPERACAO_CANCELADA, "menuInicial");
        }
    }

    @EstadoChat
    public void informarMedico(Contexto contexto) {
        String nomeMedico = contexto.getMensagemUsuario();

        if (!nomeMedico.equals("0")) {
            consultaBuilder.setMedico(nomeMedico);
            contexto.responder(MENSAGEM_INFORMAR_DATA_HORA, "informarDataHora");
        } else {
            contexto.responder(MENSAGEM_OPERACAO_CANCELADA, "menuInicial");
        }
    }

    @EstadoChat
    public void informarDataHora(Contexto contexto) {
        String msgDataHora = contexto.getMensagemUsuario();

        if (!msgDataHora.equals("0")) {
            try {
                LocalDateTime dataHora = LocalDateTime.parse(msgDataHora, formatter);

                consultaBuilder.setDataHora(dataHora);
                Consulta consulta = consultaBuilder.build();

                contexto.getServico(Consulta.class).salvar(consulta);
                contexto.responder("Consulta agendada com sucesso!", "menuInicial");
            } catch (DateTimeParseException e) {
                contexto.responder("Formato inválido! Por favor, informe a data e hora no formato dd/MM/yyyy HH:mm (Exemplo: 01/01/20224 10:00)", "informarDataHora");
            } catch (YormException e) {
                throw new RuntimeException(e);
            }
        } else {
            contexto.responder(MENSAGEM_OPERACAO_CANCELADA, "menuInicial");
        }
    }

    @EstadoChat
    public void cancelarConsulta(Contexto contexto) {
        String idConsulta = contexto.getMensagemUsuario();

        if (!idConsulta.equals("0")) {
            try {
                int id = Integer.parseInt(idConsulta);
                Consulta consulta = contexto.getServico(Consulta.class).buscarPorId(id);

                if (consulta != null) {
                    contexto.getServico(Consulta.class).deletarPorId(consulta.Id());
                    contexto.responder("Consulta cancelada com sucesso!", "menuInicial");
                } else {
                    contexto.responder("Consulta não encontrada! Digite 0 se quiser ver as opções novamente.", "menuInicial");
                }
            } catch (NumberFormatException e) {
                contexto.responder("Formato inválido! Digite 0 se quiser ver as opções novamente.", "menuInicial");
            } catch (YormException e) {
                throw new RuntimeException(e);
            }
        } else {
            contexto.responder(MENSAGEM_OPERACAO_CANCELADA, "menuInicial");
        }
    }

    @EstadoChat(estado = "buscarConsultasPorPaciente")
    public void buscarConsultasPorPaciente(Contexto contexto) throws YormException {
        String nomePaciente = contexto.getMensagemUsuario();

        if (nomePaciente.equals("0")) {
            contexto.responder(MENSAGEM_OPERACAO_CANCELADA, "menuInicial");
            return;
        }

        List<Consulta> consultas = contexto.getServico(Consulta.class).buscarTodos();
        StringBuilder sb = new StringBuilder();

        consultas = consultas.stream().filter(consulta -> consulta.paciente().contains(nomePaciente)).toList();

        consultas.forEach(consulta -> {
            sb.append("(").append(consulta.Id()).append(") ").append("Medico: ").append(consulta.medico()).append(" - ");
            sb.append("Data e Hora: ").append(formatter.format(consulta.dataHora())).append("<br>");
        });

        if (!consultas.isEmpty()) sb.insert(0, "Consultas agendadas para " + nomePaciente + ": <br>");
        else sb.append("Não há consultas agendadas para ").append(nomePaciente).append(". <br>");

        contexto.responder(sb.toString());
    }

    //@Chatbot
    public static class GerenciadorDeTarefasChat {

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
}
