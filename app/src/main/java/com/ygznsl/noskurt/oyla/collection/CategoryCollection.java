package com.ygznsl.noskurt.oyla.collection;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Category;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class CategoryCollection implements Serializable, Runnable, Iterable<Category> {

    private final List<Category> list = new LinkedList<>();
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
        reference.addValueEventListener(new ValueEventListener() {
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
    }

}
