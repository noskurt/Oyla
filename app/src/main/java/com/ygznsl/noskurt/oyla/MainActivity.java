package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final Semaphore semMaxPollId = new Semaphore(1);
    private Poll randomPoll;
    private int maxPollId;
    private User user;

    private ProgressBar pbUserNameMain;
    private ProgressBar pbPollCountMain;
    private ProgressBar pbRandomPollMain;

    private TextView txtUserNameMain;
    private TextView txtPollCountMain;
    private LinearLayout llRandomPollMain;

    public MainActivity(){
        getUserInfo();
        getPollCount();
        getRandomPoll();
    }

    private void getUserInfo(){
        db.child("user").orderByChild("email").equalTo(auth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    user = ds.getValue(User.class);
                    if (txtUserNameMain != null){
                        txtUserNameMain.setText(user.getName());
                        txtUserNameMain.setVisibility(View.VISIBLE);
                    }
                    if (pbUserNameMain != null){
                        pbUserNameMain.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void getPollCount(){
        try {
            semMaxPollId.acquire();
            db.child("poll").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (txtPollCountMain != null){
                        txtPollCountMain.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        txtPollCountMain.setVisibility(View.VISIBLE);
                    }
                    if (pbPollCountMain != null){
                        pbPollCountMain.setVisibility(View.GONE);
                    }
                    final LinkedList<Poll> polls = new LinkedList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) polls.add(ds.getValue(Poll.class));
                    Collections.sort(polls, new Comparator<Poll>() {
                        @Override
                        public int compare(Poll p1, Poll p2) {
                            return Integer.valueOf(p2.getId()).compareTo(p1.getId());
                        }
                    });
                    maxPollId = polls.getFirst().getId();
                    semMaxPollId.release();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } catch (InterruptedException ex) {
            Log.e("getPollCount", ex.getMessage());
        }
    }

    private void getRandomPoll(){
        try {
            semMaxPollId.acquire();
            final int random = new Random().nextInt(maxPollId) + 1;
            db.child("poll").orderByChild("id").startAt(random).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        randomPoll = ds.getValue(Poll.class);
                        Log.w("Random poll", randomPoll.toString());
                        semMaxPollId.release();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (InterruptedException ex) {
            Log.e("getPollCount", ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbUserNameMain = (ProgressBar) findViewById(R.id.pbUserNameMain);
        pbPollCountMain = (ProgressBar) findViewById(R.id.pbPollCountMain);
        pbRandomPollMain = (ProgressBar) findViewById(R.id.pbRandomPollMain);

        txtUserNameMain = (TextView) findViewById(R.id.txtUserNameMain);
        txtPollCountMain = (TextView) findViewById(R.id.txtPollCountMain);
        llRandomPollMain = (LinearLayout) findViewById(R.id.llRandomPollMain);

        if (user == null) getUserInfo();
        if (maxPollId == 0) getPollCount();
        getRandomPoll();

        // TODO rastgele anket kısmı yapılacak

    }
}
