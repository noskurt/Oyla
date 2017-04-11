package com.ygznsl.noskurt.oyla.entity;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class State extends Entity implements Serializable {

    private int id;
    private String name;
    private final List<City> cities = new LinkedList<>();
    private transient final DatabaseReference reference;

    public State(DatabaseReference reference) {
        this.reference = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                State.this.id = Integer.parseInt(dataSnapshot.child("id").getValue().toString());
                State.this.name = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        reference.child("id").setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    State.this.id = id;
                }
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        reference.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    State.this.name = name;
                }
            }
        });
    }

    public List<City> getCities() {
        return cities;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        return id == state.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
