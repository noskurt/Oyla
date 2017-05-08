package com.ygznsl.noskurt.oyla.helper;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Nullable<T> implements Serializable {

    private List<ValueChangedEvent<T>> listeners = Collections.synchronizedList(new LinkedList<ValueChangedEvent<T>>());
    private T value;

    public Nullable() {
        this(null);
    }

    public Nullable(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        final T tmp = this.value;
        this.value = value;
        for (final ValueChangedEvent<T> listener : listeners){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listener.valueChanged(tmp, Nullable.this.value);
                }
            }).start();
        }
    }

    public boolean hasValue(){
        return value != null;
    }

    public void addValueChangedEvent(ValueChangedEvent<T> listener){
        listeners.add(listener);
    }

    public void removeValueChangedEvent(ValueChangedEvent<T> listener){
        listeners.remove(listener);
    }

    public interface ValueChangedEvent<T> {
        void valueChanged(T oldValue, T newValue);
    }

}
