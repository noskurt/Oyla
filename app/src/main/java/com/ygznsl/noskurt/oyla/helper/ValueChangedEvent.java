package com.ygznsl.noskurt.oyla.helper;

public interface ValueChangedEvent<T> {
    void valueChanged(T oldValue, T newValue);
}
