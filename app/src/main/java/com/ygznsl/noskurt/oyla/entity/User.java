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

public final class User extends Entity implements Serializable {

    private int id, city;
    private Date birthDate;
    private String name, email, password, gender;
    private final DatabaseReference reference;
    private transient final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("tr", "TR"));

    public User(DatabaseReference reference) {
        this.reference = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User.this.id = Integer.parseInt(dataSnapshot.child("id").getValue().toString());
                User.this.city = Integer.parseInt(dataSnapshot.child("city").getValue().toString());
                User.this.name = dataSnapshot.child("name").getValue().toString();
                User.this.email = dataSnapshot.child("email").getValue().toString();
                User.this.password = dataSnapshot.child("hashedPassword").getValue().toString();
                User.this.gender = dataSnapshot.child("gender").getValue().toString();
                try {
                    User.this.birthDate = sdf.parse(dataSnapshot.child("birthDate").getValue().toString());
                } catch (ParseException ex) {
                    User.this.birthDate = null;
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
                    User.this.id = id;
                }
            }
        });
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        reference.child("gender").setValue(gender).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User.this.gender = gender;
                }
            }
        });
    }

    public int getCity() {
        return city;
    }

    public void setCity(final int city) {
        reference.child("city").setValue(city).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User.this.city = city;
                }
            }
        });
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final Date birthDate) {
        reference.child("birthDate").setValue(sdf.format(birthDate)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User.this.birthDate = birthDate;
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
                    User.this.name = name;
                }
            }
        });
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        reference.child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User.this.email = email;
                }
            }
        });
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String hashedPassword) {
        reference.child("hashedPassword").setValue(hashedPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User.this.password = hashedPassword;
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

        User user = (User) o;

        return id == user.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", gender=" + gender +
                ", city=" + city +
                ", birthDate=" + sdf.format(birthDate) +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
