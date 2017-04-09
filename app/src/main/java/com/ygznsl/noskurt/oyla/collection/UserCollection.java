package com.ygznsl.noskurt.oyla.collection;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.User;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class UserCollection implements Serializable, Runnable, Iterable<User> {

    private final List<User> list = Collections.synchronizedList(new LinkedList<User>());
    private final DatabaseReference reference;

    public UserCollection(DatabaseReference reference) {
        this.reference = reference;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public List<User> list() {
        return list;
    }

    @Override
    public Iterator<User> iterator() {
        return list.iterator();
    }

    @Override
    public void run() {
        final CountWatcher watcher = new CountWatcher();
        final CountDownLatch latch = new CountDownLatch(1);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int count = (int) dataSnapshot.getChildrenCount();
                Log.w("Count", "" + count);
                watcher.increment(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                final User user = new User(dataSnapshot.getRef());
                list.add(user);
                Log.w("User added", "" + dataSnapshot);
                watcher.decrement();
                if (watcher.get() == 0){
                    if (latch.getCount() != 0){
                        latch.countDown();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Already has a listener in User class
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final User u = CollectionUtil.findById(list, Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                if (u != null) list.remove(u);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Log.e("UserCollection", ex.getMessage());
        }
    }

    private class CountWatcher {

        private int count = 0;

        public void increment() { count++; }

        public void increment(int by) { count += by; }

        public void decrement() { count--; }

        public int get() {
            return count;
        }

        public void set(int count) {
            this.count = count;
        }

    }

}
