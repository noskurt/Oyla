package com.ygznsl.noskurt.oyla.entity;

import java.util.List;

public abstract class Entity {
    public abstract int getId();
    public abstract void setId(int id);
    public static <T extends Entity> T findById(List<T> list, int id){
        for (T entity : list){
            if (entity.getId() == id){
                return entity;
            }
        }
        return null;
    }
}
