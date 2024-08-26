package com.mychat2.util;

import com.mychat2.annotations.Chatbot;
import com.mychat2.annotations.ChatbotEstado;
import com.mychat2.annotations.Entidade;
import com.mychat2.domain.Contexto;
import com.mychat2.exception.ChatbotException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BuscaAnotacoesUtil {

    public static List<Class<?>> buscarEntidades() {
        List<Class<?>> entidades;

        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            entidades = new ArrayList<>(scanResult.getClassesWithAnnotation(Entidade.class).getRecords().loadClasses());
        }

        return entidades;
    }

    public static Object buscarClasseChatbot() throws Exception {
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            for (Class<?> clazz : scanResult.getClassesWithAnnotation(Chatbot.class).loadClasses()) {
                return clazz.getDeclaredConstructor().newInstance();
            }
        }

        return null;
    }

    public static Map<String, Consumer<Contexto>> mapearEstados(Object chatbotImpl) {
        Map<String, Consumer<Contexto>> estadosMap = new HashMap<>();
        Class<?> clazz = chatbotImpl.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ChatbotEstado.class)) {
                ChatbotEstado estado = method.getAnnotation(ChatbotEstado.class);
                validarMetodo(method, estado, estadosMap); // Passa o map e a annotation para a validação

                estadosMap.put(estado.value().toLowerCase(), obj -> {
                    try {
                        method.invoke(chatbotImpl, obj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        return estadosMap;
    }

    private static void validarMetodo(Method method, ChatbotEstado estado, Map<String, Consumer<Contexto>> estadosMap) {
        if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].getName().equals(Contexto.class.getName())) {
            throw new ChatbotException("O método '" + method.getName() + "' da classe " + method.getDeclaringClass().getName() + "está anotado com @ChatbotEstado e deve conter um único parâmetro, que precisa ser do tipo Contexto!");
        }

        if (estado.value().isBlank()) {
            throw new ChatbotException("O método '" + method.getName() + "' da classe " + method.getDeclaringClass().getName() + "está anotado com @ChatbotEstado, mas o valor da anotação não pode ser vazio!");
        }

        if (estadosMap.containsKey(estado.value().toLowerCase())) {
            throw new ChatbotException("O estado '" + estado.value() + "' não pode ser mapeado para mais de um método!");
        }
    }
}
