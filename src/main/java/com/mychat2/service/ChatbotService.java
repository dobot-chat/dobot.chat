package com.mychat2.service;

import com.mychat2.config.YormConfig;
import org.yorm.Yorm;
import org.yorm.exception.YormException;

import java.util.List;

public class ChatbotService<T extends Record> {

    private final Yorm yorm;
    private final Class<T> obj;

    public ChatbotService(Class<T> obj) {
        this.yorm = YormConfig.getYorm();
        this.obj = obj;
    }

    public void saveObj(T obj) throws YormException {
        yorm.save(obj);
    }

    public void insertObjList(List<T> obj) throws YormException {
        yorm.insert(obj);
    }

    public List<T> getAll() throws YormException {
        return yorm.find(obj);
    }

    public T getObjById(int id) throws YormException {
        return yorm.find(obj, id);
    }

    public void deleteObj(int id) throws YormException {
        T objeto = getObjById(id);
        if (objeto != null) {
            yorm.delete(objeto.getClass(), id);
        }
    }
}
