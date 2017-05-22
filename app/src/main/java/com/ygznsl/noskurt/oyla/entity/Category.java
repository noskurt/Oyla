package com.ygznsl.noskurt.oyla.entity;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.ygznsl.noskurt.oyla.helper.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class Category extends Entity implements Serializable {

    private int id;
    private String name;

    public static Category all(){
        final Category c = new Category();
        c.setId(-1);
        c.setName("Hepsi");
        return c;
    }

    public static Nullable<List<Category>> getCategories(Context context){
        final List<Category> categories = new LinkedList<>();
        try (InputStreamReader isr = new InputStreamReader(context.getAssets().open("category.json"), Charset.forName("utf-8"))){
            try (JsonReader reader = new JsonReader(isr)){
                final Category[] array = new Gson().fromJson(reader, Category[].class);
                categories.addAll(Arrays.asList(array));
                return new Nullable<>(categories);
            }
        } catch (IOException ex) {
            Log.e("City.getCities", ex.getMessage());
            return new Nullable<>();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

}
