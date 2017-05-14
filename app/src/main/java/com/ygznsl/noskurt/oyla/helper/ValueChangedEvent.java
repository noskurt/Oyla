package com.ygznsl.noskurt.oyla.helper;

import java.io.Serializable;

public interface ValueChangedEvent<T> extends Serializable {
    void valueChanged(T oldValue, T newValue);
}
