package com.ygznsl.noskurt.oyla.collection;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.State;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class CityCollection implements Serializable, Runnable, Iterable<City> {

    private final List<City> list = Collections.synchronizedList(new LinkedList<City>());
    private final DatabaseReference reference;
    private final List<State> states;

    public CityCollection(DatabaseReference reference, List<State> states) {
        this.reference = reference;
        this.states = states;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public List<City> list() {
        return list;
    }

    @Override
    public Iterator<City> iterator() {
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
                    final City c = new City(data.getRef(), states);
                    list.add(c);
                    Log.w("CityCollection", "City found: " + data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Log.e("CityCollection", ex.getMessage());
        }
    }

}
