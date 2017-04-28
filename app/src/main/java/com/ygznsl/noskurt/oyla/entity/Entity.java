package com.ygznsl.noskurt.oyla.entity;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.List;

public abstract class Entity {
    public abstract int getId();
    public abstract void setId(int id);

    public static <T extends Entity> T findById(List<T> list, int id){
        for (T entity : list){
            if (entity.getId() == id){
                return entity;
            }
        }
        return null;
    }

    public static void bindEntity(DatabaseReference ref, final Option o){
        try {
            ref.child("id").addValueEventListener(new IntegerBinder(o, o.getClass().getField("id")));
            ref.child("poll").addValueEventListener(new StringBinder(o, o.getClass().getField("poll")));
            ref.child("title").addValueEventListener(new StringBinder(o, o.getClass().getField("title")));
        } catch (NoSuchFieldException ex) {
            Log.e("Entity.bindEntity", ex.getMessage());
        }
    }

    public static void bindEntity(DatabaseReference ref, final Poll p){
        try {
            ref.child("id").addValueEventListener(new IntegerBinder(p, p.getClass().getField("id")));
            ref.child("category").addValueEventListener(new IntegerBinder(p, p.getClass().getField("category")));
            ref.child("mult").addValueEventListener(new IntegerBinder(p, p.getClass().getField("mult")));
            ref.child("user").addValueEventListener(new IntegerBinder(p, p.getClass().getField("user")));
            ref.child("pdate").addValueEventListener(new StringBinder(p, p.getClass().getField("pdate")));
            ref.child("title").addValueEventListener(new StringBinder(p, p.getClass().getField("title")));
            ref.child("url").addValueEventListener(new StringBinder(p, p.getClass().getField("url")));
            ref.child("genders").addValueEventListener(new StringBinder(p, p.getClass().getField("genders")));
        } catch (NoSuchFieldException ex) {
            Log.e("Entity.bindEntity", ex.getMessage());
        }
    }

    public static void bindEntity(DatabaseReference ref, final User u){
        try {
            ref.child("id").addValueEventListener(new IntegerBinder(u, u.getClass().getField("id")));
            ref.child("city").addValueEventListener(new IntegerBinder(u, u.getClass().getField("city")));
            ref.child("bdate").addValueEventListener(new StringBinder(u, u.getClass().getField("bdate")));
            ref.child("email").addValueEventListener(new StringBinder(u, u.getClass().getField("email")));
            ref.child("gender").addValueEventListener(new StringBinder(u, u.getClass().getField("gender")));
            ref.child("name").addValueEventListener(new StringBinder(u, u.getClass().getField("name")));
        } catch (NoSuchFieldException ex) {
            Log.e("Entity.bindEntity", ex.getMessage());
        }
    }

    public static void bindVote(DatabaseReference ref, final Vote v){
        ref.child("u").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                v.setU(dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        ref.child("o").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                v.setO(dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        ref.child("vd").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                v.setVd(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private static class IntegerBinder implements ValueEventListener {

        private final Entity entity;
        private final Field field;

        public IntegerBinder(Entity entity, Field field) {
            this.entity = entity;
            this.field = field;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            try {
                field.setInt(entity, dataSnapshot.getValue(Integer.class));
            } catch (IllegalAccessException ex) {
                Log.e("IntegerBinder", ex.getMessage());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}

    }

    private static class StringBinder implements ValueEventListener {

        private final Entity entity;
        private final Field field;

        public StringBinder(Entity entity, Field field) {
            this.entity = entity;
            this.field = field;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            try {
                field.set(entity, dataSnapshot.getValue(String.class));
            } catch (IllegalAccessException ex) {
                Log.e("IntegerBinder", ex.getMessage());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}

    }

}
