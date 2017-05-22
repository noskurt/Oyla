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
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public final class City extends Entity implements Serializable {

    private int id;
    private String name;

    public static Nullable<List<City>> getCities(Context context){
        final List<City> cities = new LinkedList<>();
        try (InputStreamReader isr = new InputStreamReader(context.getAssets().open("city.json"), Charset.forName("utf-8"))){
            try (JsonReader reader = new JsonReader(isr)){
                final City[] array = new Gson().fromJson(reader, City[].class);
                cities.addAll(Arrays.asList(array));
                Collections.sort(cities, new Comparator<City>() {
                    @Override
                    public int compare(City c1, City c2) {
                        final Collator collator = Collator.getInstance(new Locale("tr", "TR"));
                        return collator.compare(c1.getName(), c2.getName());
                    }
                });
                return new Nullable<>(cities);
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
        final City city = (City) o;
        return id == city.id;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
