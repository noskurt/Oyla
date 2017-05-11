package com.ygznsl.noskurt.oyla.entity;

import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract class Entity {
    public abstract int getId();
    public abstract void setId(int id);

    public static <T extends Entity> Nullable<T> findById(List<T> list, int id){
        for (T entity : list){
            if (entity.getId() == id){
                return new Nullable<>(entity);
            }
        }
        return new Nullable<>();
    }

    public static <T, R> Nullable<T> findMatches(List<T> list, Function<T, R> mapper, R value){
        final List<T> ll = new LinkedList<>();
        for (T entity : list){
            if (mapper.apply(entity).equals(value)){
                return new Nullable<>(entity);
            }
        }
        return new Nullable<>();
    }

    public static <T, R> List<T> findAllMatches(List<T> list, Function<T, R> mapper, R value){
        final List<T> ll = new LinkedList<>();
        for (T entity : list){
            if (mapper.apply(entity).equals(value)){
                ll.add(entity);
            }
        }
        return ll;
    }

    public static <T, R extends Comparable<R>> List<T> findAllRangeMatches(List<T> list, Function<T, R> mapper, R idStart, R idEnd){
        final List<T> ll = new LinkedList<>();
        for (T entity : list){
            final R result = mapper.apply(entity);
            if (result.compareTo(idStart) >= 0 && result.compareTo(idEnd) <= 0){
                ll.add(entity);
            }
        }
        return ll;
    }

    public static <T, R> int findIndexMatches(List<T> list, Function<T, R> mapper, R value){
        final List<T> ll = new LinkedList<>();
        for (int i = 0; i < list.size(); i++){
            if (mapper.apply(list.get(i)).equals(value)){
                return i;
            }
        }
        return -1;
    }

}
