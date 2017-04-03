package com.ygznsl.noskurt.oyla.entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

public final class Option implements Serializable {

    private int id;
    private String title;
    private Poll poll;
    private final DatabaseReference reference;
    private transient final List<Poll> polls;

    public Option(DatabaseReference reference, List<Poll> polls) {
        this.polls = polls;
        this.reference = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeId(Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                changeTitle(dataSnapshot.child("title").getValue().toString());
                changePoll(Integer.parseInt(dataSnapshot.child("poll").getValue().toString()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void changeId(int id){
        this.id = id;
    }

    private void changeTitle(String title){
        this.title = title;
    }

    private void changePoll(Poll poll){
        this.poll = poll;
    }

    private void changePoll(int pollId){
        for (Poll p : polls){
            if (p.getId() == pollId){
                poll = p;
                return;
            }
        }
        poll = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        changeId(id);
        reference.child("id").setValue(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        changeTitle(title);
        reference.child("title").setValue(title);
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        changePoll(poll);
        reference.child("poll").setValue(poll.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        return id == option.id;

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
