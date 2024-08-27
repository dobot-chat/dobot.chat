package com.mychat2.servico;

import com.mychat2.config.YormConfig;
import org.yorm.Yorm;
import org.yorm.exception.YormException;

import java.util.List;

public class ChatbotServico<T extends Record> {

    private final Yorm yorm;
    private final Class<T> obj;

    public ChatbotServico(Class<T> obj) {
        this.yorm = YormConfig.getYorm();
        this.obj = obj;
    }

    public void salvar(T obj) throws YormException {
        yorm.save(obj);
    }

    public void salvarLista(List<T> obj) throws YormException {
        yorm.insert(obj);
    }

    public List<T> buscarTodos() throws YormException {
        return yorm.find(obj);
    }

    public T buscarPorId(int id) throws YormException {
        return yorm.find(obj, id);
    }

    public void deletarPorId(int id) throws YormException {
        T objeto = buscarPorId(id);
        if (objeto != null) {
            yorm.delete(objeto.getClass(), id);
        }
    }
}
