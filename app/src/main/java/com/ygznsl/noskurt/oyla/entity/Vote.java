package com.ygznsl.noskurt.oyla.entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class Vote implements Serializable {

    private Option option;
    private User user;
    private Date voteDate;
    private final DatabaseReference reference;
    private transient final List<User> users;
    private transient final List<Option> options;
    private transient final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("tr", "TR"));

    public Vote(DatabaseReference reference, List<User> users, List<Option> options) {
        this.users = users;
        this.options = options;
        this.reference = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeOption(Integer.parseInt(dataSnapshot.child("oid").getValue().toString()));
                changeUser(Integer.parseInt(dataSnapshot.child("uid").getValue().toString()));
                try {
                    changeVoteDate(sdf.parse(dataSnapshot.child("votedate").getValue().toString()));
                } catch (ParseException ex) {
                    changeVoteDate(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void changeOption(Option option){
        this.option = option;
    }

    private void changeOption(int optionId){
        for (Option o : options){
            if (o.getId() == optionId){
                option = o;
                return;
            }
        }
        option = null;
    }

    private void changeUser(User user){
        this.user = user;
    }

    private void changeUser(int userId){
        for (User u : users){
            if (u.getId() == userId){
                user = u;
                return;
            }
        }
        user = null;
    }

    private void changeVoteDate(Date voteDate){
        this.voteDate = voteDate;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        changeOption(option);
        reference.child("oid").setValue(option.getId());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        changeUser(user);
        reference.child("uid").setValue(user.getId());
    }

    public Date getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(Date voteDate) {
        changeVoteDate(voteDate);
        reference.child("votedate").setValue(sdf.format(voteDate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vote vote = (Vote) o;

        if (!option.equals(vote.option)) return false;
        if (!user.equals(vote.user)) return false;
        return voteDate != null ? voteDate.equals(vote.voteDate) : vote.voteDate == null;

    }

    @Override
    public int hashCode() {
        int result = option.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + (voteDate != null ? voteDate.hashCode() : 0);
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
