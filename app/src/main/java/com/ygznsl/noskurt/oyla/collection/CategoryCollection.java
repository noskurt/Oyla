package com.ygznsl.noskurt.oyla.collection;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Category;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class CategoryCollection implements Serializable, Runnable, Iterable<Category> {

    private final List<Category> list = Collections.synchronizedList(new LinkedList<Category>());
    private final DatabaseReference reference;

    public CategoryCollection(DatabaseReference reference) {
        this.reference = reference;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public List<Category> list() {
        return list;
    }

    @Override
    public Iterator<Category> iterator() {
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
                    final Category cat = new Category(data.getRef());
                    list.add(cat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Log.e("CategoryCollection", ex.getMessage());
        }
    }

}
