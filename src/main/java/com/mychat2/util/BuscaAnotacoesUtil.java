package com.mychat2.util;

import com.mychat2.annotations.Chatbot;
import com.mychat2.annotations.Entidade;
import com.mychat2.domain.MeuChat;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class BuscaAnotacoesUtil {

    public static List<Class<?>> buscarEntidades() {
        List<Class<?>> entidades;

        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            entidades = new ArrayList<>(scanResult.getClassesWithAnnotation(Entidade.class).getRecords().loadClasses());
        }

        return entidades;
    }

    public static MeuChat buscarClasseChatbot() throws Exception {
        try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
            for (Class<?> clazz : scanResult.getClassesWithAnnotation(Chatbot.class.getName()).loadClasses()) {
                if (MeuChat.class.isAssignableFrom(clazz)) {
                    return (MeuChat) clazz.getDeclaredConstructor().newInstance();
                }
            }
        }

        return null;
    }
}
