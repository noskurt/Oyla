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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public final class Poll extends Entity implements Serializable {

    private int id, category, user;
    private String title, url;
    private Date publishDate;
    private boolean multiple;
    private char genderSpecified;
    private final DatabaseReference reference;
    private final List<Option> options = new LinkedList<>();
    private transient final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("tr", "TR"));

    public Poll(DatabaseReference reference) {
        this.reference = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Poll.this.id = Integer.parseInt(dataSnapshot.child("id").getValue().toString());
                Poll.this.title = dataSnapshot.child("title").getValue().toString();
                Poll.this.url = dataSnapshot.child("title").getValue().toString();
                Poll.this.multiple = dataSnapshot.child("multiple").getValue().toString().equals("1");
                Poll.this.genderSpecified = dataSnapshot.child("genderSpecified").getValue().toString().charAt(0);
                Poll.this.category = Integer.parseInt(dataSnapshot.child("category").getValue().toString());
                Poll.this.user = Integer.parseInt(dataSnapshot.child("user").getValue().toString());
                try {
                    Poll.this.publishDate = sdf.parse(dataSnapshot.child("publishDate").getValue().toString());
                } catch (ParseException ex) {
                    Poll.this.publishDate = null;
                }
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
                    Poll.this.id = id;
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
                    Poll.this.title = title;
                }
            }
        });
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        reference.child("url").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Poll.this.url = url;
                }
            }
        });
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(final Date publishDate) {
        reference.child("publishDate").setValue(sdf.format(publishDate)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Poll.this.publishDate = publishDate;
                }
            }
        });
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(final boolean multiple) {
        reference.child("multiple").setValue(multiple ? 1 : 0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Poll.this.multiple = multiple;
                }
            }
        });
    }

    public char getGenderSpecified() {
        return genderSpecified;
    }

    public void setGenderSpecified(final char genderSpecified) {
        reference.child("genderSpecified").setValue("" + genderSpecified).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Poll.this.genderSpecified = genderSpecified;
                }
            }
        });
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(final int category) {
        reference.child("category").setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Poll.this.category = category;
                }
            }
        });
    }

    public int getUser() {
        return user;
    }

    public void setUser(final int user) {
        reference.child("user").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Poll.this.user = user;
                }
            }
        });
    }

    public List<Option> getOptions() {
        return options;
    }

    public SimpleDateFormat getDateFormat() {
        return sdf;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poll poll = (Poll) o;

        return id == poll.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Poll{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", publishDate=" + sdf.format(publishDate) +
                ", multiple=" + multiple +
                ", genderSpecified=" + genderSpecified +
                ", category=" + category +
                ", user=" + user +
                '}';
    }

}
