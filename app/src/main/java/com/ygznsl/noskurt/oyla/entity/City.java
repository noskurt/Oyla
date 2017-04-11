package com.ygznsl.noskurt.oyla.entity;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

public final class City extends Entity implements Serializable {

    private int id, state;
    private String name;
    private final List<State> states;
    private transient final DatabaseReference reference;

    public City(DatabaseReference reference, List<State> states) {
        this.states = states;
        this.reference = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                City.this.id = Integer.parseInt(dataSnapshot.child("id").getValue().toString());
                City.this.state = Integer.parseInt(dataSnapshot.child("state").getValue().toString());
                City.this.name = dataSnapshot.child("name").getValue().toString();
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
                    City.this.id = id;
                }
            }
        });
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        reference.child("state").setValue(state).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    City.this.state = state;
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
                    City.this.name = name;
                }
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        return id == city.id;

    }

    public DatabaseReference getReference() {
        return reference;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }

}
