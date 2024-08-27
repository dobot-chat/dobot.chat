package com.mychat2.utils;

import com.mychat2.anotacoes.Chatbot;
import com.mychat2.anotacoes.EstadoChat;
import com.mychat2.anotacoes.Entidade;
import com.mychat2.dominio.Contexto;
import com.mychat2.exception.ChatbotExcecao;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AnotacoesUtil {

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

        for (Method metodo : clazz.getDeclaredMethods()) {
            if (metodo.isAnnotationPresent(EstadoChat.class)) {
                EstadoChat estado = metodo.getAnnotation(EstadoChat.class);
                validarMetodo(metodo, estado, estadosMap);

                estadosMap.put(estado.value().toLowerCase(), obj -> {
                    try {
                        metodo.invoke(chatbotImpl, obj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        return estadosMap;
    }

    private static void validarMetodo(Method method, EstadoChat estado, Map<String, Consumer<Contexto>> estadosMap) {
        if (method.getParameterCount() != 1 || !method.getParameterTypes()[0].getName().equals(Contexto.class.getName())) {
            throw new ChatbotExcecao("O método '" + method.getName() + "' da classe " + method.getDeclaringClass().getName() + " está anotado com " + EstadoChat.class.getName() + " e deve conter um único parâmetro, que precisa ser do tipo " + Contexto.class.getName() + "!");
        }

        if (estado.value().isBlank()) {
            throw new ChatbotExcecao("O método '" + method.getName() + "' da classe " + method.getDeclaringClass().getName() + " está anotado com " + EstadoChat.class.getName() + ", mas o valor da anotação não pode ser vazio!");
        }

        if (estadosMap.containsKey(estado.value().toLowerCase())) {
            throw new ChatbotExcecao("O estado '" + estado.value() + "' não pode ser mapeado para mais de um método!");
        }
    }
}
