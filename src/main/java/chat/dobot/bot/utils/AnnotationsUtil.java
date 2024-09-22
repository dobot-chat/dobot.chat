package chat.dobot.bot.utils;

import chat.dobot.bot.BotStateMethod;
import chat.dobot.bot.DoBotException;
import chat.dobot.bot.annotations.DoBotChat;
import chat.dobot.bot.annotations.Entidade;
import chat.dobot.bot.annotations.EstadoChat;
import chat.dobot.bot.domain.DoBot;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

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
                    throw new DoBotException("Erro ao instanciar a classe chatbot: " + classe.getName(), e);
                }
            }
        } catch (Exception e) {
            throw new DoBotException("Erro ao buscar a classe chatbot", e);
        }

        //TODO: criar coleção, adicionar bovoBot e se a coleção for vazia, lançar exceção
//        if(novoBot == null){
//            throw new DoBotException("Nenhuma classe anotada com @DoBotChat foi encontrada");
//        }
        return novoBot;
    }


    /**
     * Mapeia os estados do chatbot.
     *
     * @param chatbotImpl objeto do usuario dev que contém a anotação @DoBotChat
     * @return um mapa com os estados do bot
     */
    public static Map<String, BotStateMethod> mapearEstados(Object chatbotImpl) {
        Map<String, BotStateMethod> estadosMap = new HashMap<>();

        for (Method metodo : chatbotImpl.getClass().getDeclaredMethods()) {
            if (metodo.isAnnotationPresent(EstadoChat.class)) {
                EstadoChat estadoChat = metodo.getAnnotation(EstadoChat.class);
                String estado;

                if(estadoChat.inicial()){
                    estado = "main";
                }else {
                    estado = estadoChat.estado().isBlank() ? metodo.getName() : estadoChat.estado();
                }
                if (estadosMap.containsKey(estado.toLowerCase())) {
                    throw new DoBotException("O estado '" + estado.toLowerCase() + "' não pode ser mapeado para mais de um método!");
                }

                estadosMap.put(estado.toLowerCase(), contexto -> {
                    try {
                        metodo.invoke(chatbotImpl, contexto);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
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

}
