package com.ygznsl.noskurt.oyla.entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public final class Category implements Serializable {

    private int id;
    private String name;
    private final DatabaseReference reference;

    public Category(DatabaseReference reference) {
        this.reference = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeId(Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                changeName(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void changeId(int id){
        this.id = id;
    }

    private void changeName(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        changeId(id);
        reference.child("id").setValue(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        changeName(name);
        reference.child("name").setValue(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return id == category.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
