package com.ygznsl.noskurt.oyla.entity;

import android.os.CountDownTimer;
import com.google.firebase.database.DataSnapshot;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class EntityReceiver<T extends Entity> implements Serializable {

    private final List<T> list = Collections.synchronizedList(new LinkedList<T>());
    private CountDownTimer timer = null, timeoutTimer = null;
    private EntityReceiverListener listener;
    private final Class<T> type;
    private long timeout = 5000L;

    public EntityReceiver(Class<T> type) {
        this.type = type;
    }

    public EntityReceiver(Class<T> type, long timeout) {
        this(type);
        this.timeout = timeout;
    }

    public synchronized List<T> list() {
        return list;
    }

    public EntityReceiverListener getOnReceived() {
        return listener;
    }

    public synchronized long getTimeout() {
        return timeout;
    }

    public Class<T> getType() {
        return type;
    }

    public void setOnReceived(EntityReceiverListener listener) {
        this.listener = listener;
    }

    public synchronized void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void add(DataSnapshot data){
        try {
            list.add(data.getValue(type));
            if (timeoutTimer == null){
                timeoutTimer = new CountDownTimer(timeout, 500L) {
                    @Override
                    public void onTick(long millisUntilFinished) {}

                    @Override
                    public void onFinish() {
                        if (listener != null){
                            listener.onTimeout();
                        }
                    }
                };
                timeoutTimer.start();
            }
        } catch (Exception ex) {
            if (timer != null) timer.cancel();
            if (timeoutTimer != null) {
                timeoutTimer.cancel();
                timeoutTimer = null;
            }
            if (listener != null){
                listener.onExcepion(ex);
            }
        } finally {
            if (timer != null) timer.cancel();
            timer = new CountDownTimer(1500L, 500L) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    if (timeoutTimer != null){
                        timeoutTimer.cancel();
                        timeoutTimer = null;
                    }
                    if (listener != null){
                        listener.onReceived();
                    }
                }
            };
            timer.start();
        }
    }

}
