package chat.dobot.bot.utils;

import chat.dobot.bot.BotStateMethod;
import chat.dobot.bot.Contexto;
import chat.dobot.bot.DoBotException;
import chat.dobot.bot.annotations.DoBot;
import chat.dobot.bot.annotations.Entidade;
import chat.dobot.bot.annotations.EstadoChat;
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

    public static Object buscarClasseChatbot() throws Exception {
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            for (Class<?> classe : scanResult.getClassesWithAnnotation(DoBot.class).loadClasses()) {
                return classe.getDeclaredConstructor().newInstance();
            }
        }

        return null;
    }


    /**
     * Mapeia os estados do chatbot.
     * @param chatbotImpl objeto do usuario dev que contém a anotação @DoBot
     * @return um mapa com os estados do bot
     */
    public static Map<String, BotStateMethod> mapearEstados(Object chatbotImpl) {
        Map<String, BotStateMethod> estadosMap = new HashMap<>();

        for (Method metodo : chatbotImpl.getClass().getDeclaredMethods()) {
            if (metodo.isAnnotationPresent(EstadoChat.class)) {
                EstadoChat estadoChat = metodo.getAnnotation(EstadoChat.class);
                String estado = estadoChat.estado().isBlank() ? metodo.getName() : estadoChat.estado();

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

        return estadosMap;
    }

    public static String obterEstadoInicial(Object chatbotImpl) {
        String estadoInicial = null;
        for (Method method : chatbotImpl.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EstadoChat.class)) {
                EstadoChat estadoChat = method.getAnnotation(EstadoChat.class);
                if (estadoChat.inicial()) {
                    if (estadoInicial != null) {
                        throw new DoBotException("Mais de um estado inicial definido!");
                    }
                    estadoInicial = estadoChat.estado().isEmpty() ? method.getName() : estadoChat.estado();
                }
            }
        }

        if (estadoInicial != null) {
            return estadoInicial.toLowerCase();
        }
        throw new DoBotException("Nenhum estado inicial definido!");
    }


}
