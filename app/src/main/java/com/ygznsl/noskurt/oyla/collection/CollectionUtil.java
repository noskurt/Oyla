package com.ygznsl.noskurt.oyla.collection;

import com.ygznsl.noskurt.oyla.entity.Entity;

import java.util.List;

public final class CollectionUtil {

    public static <T extends Entity> T findById(List<T> list, int id){
        for (T entity : list){
            if (entity.getId() == id){
                return entity;
            }
        }
        return null;
    }

}
