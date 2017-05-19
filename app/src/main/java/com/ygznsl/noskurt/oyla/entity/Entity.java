package com.ygznsl.noskurt.oyla.entity;

import com.google.firebase.database.FirebaseDatabase;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.Predicate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Entity {
    private static FirebaseDatabase db;

    public static FirebaseDatabase getDatabase() {
        if (db == null) {
            db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);
        }
        return db;
    }

    public static <T extends Entity> Nullable<T> findById(List<T> list, int id){
        for (T entity : list)
            if (entity.getId() == id) return new Nullable<>(entity);
        return new Nullable<>();
    }

    public static <T> Nullable<T> findMatches(List<T> list, Predicate<T> predicate){
        for (T entity : list)
            if (predicate.test(entity)) return new Nullable<>(entity);
        return new Nullable<>();
    }

    public static <T> List<T> findAllMatches(List<T> list, Predicate<T> predicate){
        final List<T> ll = new LinkedList<>();
        for (T entity : list){
            if (predicate.test(entity)){
                ll.add(entity);
            }
        }
        return ll;
    }

    public static <T, R> List<R> findAllMatches(List<T> list, Predicate<T> predicate, Function<T, R> mapper){
        final List<R> ll = new LinkedList<>();
        for (T entity : list){
            if (predicate.test(entity)){
                ll.add(mapper.apply(entity));
            }
        }
        return ll;
    }

    public static <T> int findIndexMatches(List<T> list, Predicate<T> predicate){
        for (int i = 0; i < list.size(); i++)
            if (predicate.test(list.get(i))) return i;
        return -1;
    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper){
        final List<R> ll = new LinkedList<>();
        for (T entity : list)
            ll.add(mapper.apply(entity));
        return ll;
    }

    public static int maxId(List<Integer> list){
        return Collections.max(list);
    }

    public abstract int getId();

    public abstract void setId(int id);

}
