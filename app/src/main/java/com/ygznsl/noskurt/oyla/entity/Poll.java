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

public final class Poll implements Serializable {

    private int id;
    private String title, url;
    private Date publishDate;
    private boolean multiple;
    private Gender genderSpecified;
    private Category category;
    private User user;
    private final DatabaseReference reference;
    private transient final List<User> users;
    private transient final List<Category> categories;
    private transient final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("tr", "TR"));

    public Poll(DatabaseReference reference, List<Category> categories, List<User> users) {
        this.categories = categories;
        this.users = users;
        this.reference = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeId(Integer.parseInt(dataSnapshot.child("id").getValue().toString()));
                changeTitle(dataSnapshot.child("title").getValue().toString());
                changeUrl(dataSnapshot.child("url").getValue().toString());
                changeMultiple(dataSnapshot.child("multiple").getValue().toString().equals("1"));
                changeGenderSpecified(dataSnapshot.child("genderSpecified").getValue().toString().charAt(0));
                changeCategory(Integer.parseInt(dataSnapshot.child("category").getValue().toString()));
                changeUser(Integer.parseInt(dataSnapshot.child("user").getValue().toString()));
                try {
                    changePublishDate(sdf.parse(dataSnapshot.child("publishDate").getValue().toString()));
                } catch (ParseException ex) {
                    changePublishDate(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void changeId(int id){
        this.id = id;
    }

    private void changeUrl(String url){
        this.url = url;
    }

    private void changeTitle(String title){
        this.title = title;
    }

    private void changePublishDate(Date publishDate){
        this.publishDate = publishDate;
    }

    private void changeMultiple(boolean multiple){
        this.multiple = multiple;
    }

    private void changeGenderSpecified(Gender genderSpecified){
        this.genderSpecified = genderSpecified;
    }

    private void changeGenderSpecified(char character){
        genderSpecified = Gender.of(character);
    }

    private void changeCategory(Category category){
        this.category = category;
    }

    private void changeCategory(int categoryId){
        for (Category c : categories){
            if (c.getId() == categoryId){
                category = c;
                return;
            }
        }
        category = null;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        changeUrl(url);
        reference.child("url").setValue(url);
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        changePublishDate(publishDate);
        reference.child("publishDate").setValue(sdf.format(publishDate));
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        changeMultiple(multiple);
        reference.child("multiple").setValue(multiple ? 1 : 0);
    }

    public Gender getGenderSpecified() {
        return genderSpecified;
    }

    public void setGenderSpecified(Gender genderSpecified) {
        changeGenderSpecified(genderSpecified);
        reference.child("genderSpecified").setValue(genderSpecified.getCharacter());
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        changeCategory(category);
        reference.child("category").setValue(category.getId());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        changeUser(user);
        reference.child("user").setValue(user.getId());
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
