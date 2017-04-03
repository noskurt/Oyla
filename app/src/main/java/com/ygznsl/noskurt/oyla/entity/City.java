package com.ygznsl.noskurt.oyla.entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

public final class City implements Serializable {

    private int id;
    private String name;
    private State state;
    private final DatabaseReference reference;
    private transient final List<State> states;

    public City(DatabaseReference reference, List<State> states) {
        this.states = states;
        this.reference = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeId(Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                changeName(dataSnapshot.child("name").getValue().toString());
                changeState(Integer.parseInt(dataSnapshot.child("state").getValue().toString()));
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

    private void changeState(State state){
        this.state = state;
    }

    private void changeState(int stateId){
        for (State s : states){
            if (s.getId() == stateId){
                state = s;
                return;
            }
        }
        state = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        reference.child("id").setValue(id);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        changeState(state);
        reference.child("state").setValue(state.getId());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        reference.child("name").setValue(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        return id == city.id;

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
