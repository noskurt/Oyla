package com.ygznsl.noskurt.oyla.helper;

import java.io.Serializable;

public final class Nullable<T> implements Serializable {

    private T value;
    private ValueChangedEvent<T> listener;

    public Nullable(){
        this(null);
    }

    public Nullable(T value) {
        this(value, null);
    }

    public Nullable(T value, ValueChangedEvent<T> listener) {
        this.value = value;
        this.listener = listener;
    }

    public T get(){
        return value;
    }

    public void set(T value) {
        final T tmp = this.value;
        this.value = value;
        if (!tmp.equals(this.value) && listener != null){
            listener.valueChanged(tmp, this.value);
        }
    }

    public boolean hasValue(){
        return value != null;
    }

    public ValueChangedEvent<T> getOnValueChanged() {
        return listener;
    }

    public void setOnValueChanged(ValueChangedEvent<T> listener) {
        this.listener = listener;
    }

    public T getOrDefault(T defaultValue){
        return hasValue() ? get() : defaultValue;
    }

    public <R> R orElse(Function<T, R> mapper, R defaultValue){
        return hasValue() ? mapper.apply(value) : defaultValue;
    }

}
