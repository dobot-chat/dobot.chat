package com.mychat2.util;

import com.mychat2.annotations.Chatbot;
import com.mychat2.annotations.ChatbotEstado;
import com.mychat2.annotations.Entidade;
import com.mychat2.domain.Contexto;
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

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(ChatbotEstado.class)) {
                validarMetodo(method);
                ChatbotEstado estado = method.getAnnotation(ChatbotEstado.class);

                estadosMap.put(estado.value(), obj -> {
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

    private static void validarMetodo(Method method) {
        if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].getName().equals(Contexto.class.getName())) {
            throw new IllegalArgumentException(
                    String.format("O método %s da classe %s está anotado com @ChatbotEstado e deve conter um único parâmetro, que precisa ser do tipo Contexto!", method.getName(), method.getClass().getName())
            );
        }
    }
}
