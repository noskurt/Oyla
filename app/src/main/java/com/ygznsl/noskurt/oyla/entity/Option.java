package com.ygznsl.noskurt.oyla.entity;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public final class Option extends Entity implements Serializable {

    private int id, poll;
    private String title;
    private final DatabaseReference reference;

    public Option(DatabaseReference reference) {
        this.reference = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Option.this.id = Integer.parseInt(dataSnapshot.child("id").getValue().toString());
                Option.this.poll = Integer.parseInt(dataSnapshot.child("poll").getValue().toString());
                Option.this.title = dataSnapshot.child("title").getValue().toString();
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
                    Option.this.id = id;
                }
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        reference.child("title").setValue(title).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Option.this.title = title;
                }
            }
        });
    }

    public int getPoll() {
        return poll;
    }

    public void setPoll(final int poll) {
        reference.child("poll").setValue(poll).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Option.this.poll = poll;
                }
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        return id == option.id;

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
        return "Option{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", poll=" + poll +
                '}';
    }

}
