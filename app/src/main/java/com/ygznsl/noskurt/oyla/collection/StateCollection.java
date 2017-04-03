package com.ygznsl.noskurt.oyla.collection;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.State;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class StateCollection implements Serializable, Runnable, Iterable<State>  {

    private final List<State> list = new LinkedList<>();
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
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    final State s = new State(data.getRef());
                    list.add(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
