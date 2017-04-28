package com.ygznsl.noskurt.oyla.entity;

public interface EntityReceiverListener {
    void onReceived();
    void onTimeout();
    void onExcepion(Exception ex);
}
