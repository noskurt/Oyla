package com.ygznsl.noskurt.oyla.entity;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Vote implements Serializable {

    private Date voteDate;
    private int user, option;
    private final DatabaseReference reference;
    private transient final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("tr", "TR"));

    public Vote(DatabaseReference reference) {
        this.reference = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vote.this.option = Integer.parseInt(dataSnapshot.child("oid").getValue().toString());
                Vote.this.user = Integer.parseInt(dataSnapshot.child("uid").getValue().toString());
                try {
                    Vote.this.voteDate = sdf.parse(dataSnapshot.child("votedate").getValue().toString());
                } catch (ParseException ex) {
                    Vote.this.voteDate = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public int getOption() {
        return option;
    }

    public void setOption(final int option) {
        reference.child("oid").setValue(option).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Vote.this.option = option;
                }
            }
        });
    }

    public int getUser() {
        return user;
    }

    public void setUser(final int user) {
        reference.child("uid").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Vote.this.user = user;
                }
            }
        });
    }

    public Date getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(final Date voteDate) {
        reference.child("votedate").setValue(sdf.format(voteDate)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Vote.this.voteDate = voteDate;
                }
            }
        });
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public SimpleDateFormat getDateFormat() {
        return sdf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vote vote = (Vote) o;

        if (user != vote.user) return false;
        if (option != vote.option) return false;
        return voteDate.equals(vote.voteDate);

    }

    @Override
    public int hashCode() {
        int result = voteDate.hashCode();
        result = 31 * result + user;
        result = 31 * result + option;
        return result;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "option=" + option +
                ", user=" + user +
                ", voteDate=" + sdf.format(voteDate) +
                '}';
    }

}
