package com.ygznsl.noskurt.oyla.collection;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.State;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class StateCollection implements Serializable, Runnable, Iterable<State> {

    private final List<State> list = Collections.synchronizedList(new LinkedList<State>());
    private final DatabaseReference reference;

    public StateCollection(DatabaseReference reference) {
        this.reference = reference;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public List<State> list() {
        return list;
    }

    @Override
    public Iterator<State> iterator() {
        return list.iterator();
    }

    @Override
    public void run() {
        final CountDownLatch latch = new CountDownLatch(1);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    final State s = new State(data.getRef());
                    list.add(s);
                    Log.w("StateCollection", "State found: " + data);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Log.e("StateCollection", ex.getMessage());
        }
    }

}
