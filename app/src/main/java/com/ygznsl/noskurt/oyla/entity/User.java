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

public final class User implements Serializable {

    private int id;
    private Gender gender;
    private City city;
    private Date birthDate;
    private String name, email, password;
    private final DatabaseReference reference;
    private transient final List<City> cities;
    private transient final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("tr", "TR"));

    public User(DatabaseReference reference, List<City> cities) {
        this.cities = cities;
        this.reference = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeId(Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                changeName(dataSnapshot.child("name").getValue().toString());
                changeEmail(dataSnapshot.child("email").getValue().toString());
                changePassword(dataSnapshot.child("hashedPassword").getValue().toString());
                changeGender(dataSnapshot.child("gender").getValue().toString().charAt(0));
                changeCity(Integer.parseInt(dataSnapshot.child("city").getValue().toString()));
                try {
                    changeBirthDate(sdf.parse(dataSnapshot.child("birthDate").getValue().toString()));
                } catch (ParseException ex) {
                    changeBirthDate(null);
                }
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

    private void changeEmail(String email){
        this.email = email;
    }

    private void changePassword(String password){
        this.password = password;
    }

    private void changeBirthDate(Date birthDate){
        this.birthDate = birthDate;
    }

    private void changeGender(Gender gender){
        this.gender = gender;
    }

    private void changeGender(char character){
        gender = Gender.of(character);
    }

    private void changeCity(City city){
        this.city = city;
    }

    private void changeCity(int cityId){
        for (City c : cities){
            if (c.getId() == cityId){
                city = c;
                return;
            }
        }
        city = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        changeId(id);
        reference.child("id").setValue(id);
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        changeGender(gender);
        reference.child("gender").setValue(gender.getCharacter());
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        changeCity(city);
        reference.child("city").setValue(city.getId());
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        changeBirthDate(birthDate);
        reference.child("birthDate").setValue(sdf.format(birthDate));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        changeName(name);
        reference.child("name").setValue(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        changeEmail(email);
        reference.child("email").setValue(email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        changePassword(password);
        reference.child("password").setValue(password);
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
