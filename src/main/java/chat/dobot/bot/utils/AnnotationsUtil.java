package chat.dobot.bot.utils;

import chat.dobot.bot.BotStateMethod;
import chat.dobot.bot.Contexto;
import chat.dobot.bot.DoBotException;
import chat.dobot.bot.annotations.DoBotChat;
import chat.dobot.bot.annotations.Entidade;
import chat.dobot.bot.annotations.EstadoChat;
import chat.dobot.bot.domain.DoBot;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationsUtil {

    public static List<Class<Record>> buscarEntidades() {
        List<Class<Record>> entidades = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            for (Class<?> classe : scanResult.getClassesWithAnnotation(Entidade.class).getRecords().loadClasses()) {
                @SuppressWarnings("unchecked")
                Class<Record> entidade = (Class<Record>) classe;
                entidades.add(entidade);
            }
        }

        return entidades;
    }


    //TODO : implementar retorno de lista de classes DoBotChat
    /**
     * Busca a classe anotada com @DoBotChat.
     * @throws DoBotException se nenhuma classe anotada com @DoBotChat for encontrada
     * @return a classe anotada com @DoBotChat
     */
    public static DoBot buscarClasseChatbot() {
        DoBot novoBot = null;
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            for (Class<?> classe : scanResult.getClassesWithAnnotation(DoBotChat.class).loadClasses()) {
                try {
                    Object instancia =  classe.getDeclaredConstructor().newInstance();
                    DoBotChat annotation = classe.getAnnotation(DoBotChat.class);
                    if (annotation != null) {
                        String id = annotation.id();
                        String nome = annotation.nome();
                        String descricao = annotation.descricao();
                        novoBot = new DoBot(id, nome, descricao);
                        Map<String, BotStateMethod> estados = mapearEstados(instancia);
                        novoBot.setEstados(estados);
                        return novoBot;
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new DoBotException("Erro ao instanciar a classe chatbot: " + classe.getName()+"\n"+e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new DoBotException("Erro ao instanciar a classe chatbot:\n"+e.getMessage(), e);
        }

        //TODO: criar coleção, adicionar bovoBot e se a coleção for vazia, lançar exceção
//        if(novoBot == null){
//            throw new DoBotException("Nenhuma classe anotada com @DoBotChat foi encontrada");
//        }
        return novoBot;
    }


    /**
     * Mapeia os estados do chatbot.
     * Os estados são mapeados a partir dos métodos anotados com @EstadoChat, na classe anotada com @DoBotChat.
     *
     * @param chatbotImpl objeto do usuario dev que contém a anotação @DoBotChat
     * @return um mapa com os estados do bot
     */
    public static Map<String, BotStateMethod> mapearEstados(Object chatbotImpl) {
        Map<String, BotStateMethod> estadosMap = new HashMap<>();

        for (Method metodo : chatbotImpl.getClass().getDeclaredMethods()) {
            if (metodo.isAnnotationPresent(EstadoChat.class)) {

                final String estado = getEstadoDoMetodo(metodo);
                if (estadosMap.containsKey(estado)) {
                    throw new DoBotException("Um mesmo estado não pode ser mapeado para mais de um método! Estado duplicado:" + estado);
                }

                conferirAssinaturaMetodo(metodo);

                // Adiciona os métodos que implementam os estados do bot ao mapa de estados
                estadosMap.put(estado, contexto -> {
                    try {
                        metodo.invoke(chatbotImpl, contexto);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Problema ao adicionar o método que implementa estado ao bot. Estado:"+estado,e);
                    }
                });
            }
        }
        if(estadosMap.isEmpty()){
            throw new DoBotException("Nenhum estado mapeado para " + chatbotImpl.getClass().getSimpleName() + "!");
        }
        if(!estadosMap.containsKey("main")){
            throw new DoBotException("Nenhum estado inicial definido!");
        }

        return estadosMap;
    }

    /**
     * Verifica se o método possui a assinatura correta.
     * O método deve ter um parâmetro do tipo Contexto.
     * @param metodo que implementa um estado do chatbot
     */
    private static void conferirAssinaturaMetodo(Method metodo) {
        String contextoName = Contexto.class.getSimpleName();
        if(metodo.getParameterCount() != 1){
            throw new DoBotException("Método "+metodo.getName()+" deve ter um parâmetro do tipo "+contextoName);
        }
        if(!metodo.getParameterTypes()[0].getSimpleName().equals(contextoName)){
            throw new DoBotException("Método "+metodo.getName()+" deve ter um parâmetro do tipo "+contextoName+"\nExemplo: public void "+metodo.getName()+"("+contextoName+" contexto)");
        }
    }

    /**
     * Recupera o nome do estado a partir da anotação do método
     * @param metodo que contém anotação @EstadoChat
     * @return o nome do estado
     */
    @NotNull
    private static String getEstadoDoMetodo(Method metodo) {
        EstadoChat estadoChat = metodo.getAnnotation(EstadoChat.class);

        final String estado;
        if(estadoChat.inicial()){
            estado = DoBot.ESTADO_INICIAL;
        }else {
            estado = estadoChat.estado().isBlank() ? metodo.getName().toLowerCase() : estadoChat.estado().toLowerCase();
        }

        return estado;
    }

}
