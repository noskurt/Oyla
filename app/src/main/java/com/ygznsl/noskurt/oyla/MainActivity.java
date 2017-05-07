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
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final List<Poll> polls = Collections.synchronizedList(new LinkedList<Poll>());
    private boolean pollsGot = false;
    private Poll randomPoll = null;
    private User user = null;
    private String userKey;

    private ProgressBar pbUserNameMain;
    private ProgressBar pbPollCountMain;
    private ProgressBar pbRandomPollMain;

    private TextView txtUserNameMain;
    private TextView txtPollCountMain;
    private LinearLayout llRandomPollMain;

    public MainActivity(){
        db.child("poll").keepSynced(true);
        getUserInfo();
        getPolls();
    }

    private void getUserInfo(){
        db.child("user").orderByChild("email").equalTo(auth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    user = ds.getValue(User.class);
                    userKey = ds.getKey();
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

    private void getPolls(){
        db.child("poll").keepSynced(true);
        db.child("poll").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    final Poll poll = ds.getValue(Poll.class);
                    if (!polls.contains(poll)) polls.add(poll);
                }
                Collections.sort(polls, new Comparator<Poll>() {
                    @Override
                    public int compare(Poll p1, Poll p2) {
                        return Integer.valueOf(p2.getId()).compareTo(p1.getId());
                    }
                });
                if (txtPollCountMain != null){
                    txtPollCountMain.setText(String.valueOf(polls.size()));
                    txtPollCountMain.setVisibility(View.VISIBLE);
                }
                if (pbPollCountMain != null){
                    pbPollCountMain.setVisibility(View.GONE);
                }
                pollsGot = true;
                selectRandomPoll();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private Poll selectRandomPoll(){
        randomPoll = polls.get(new Random().nextInt(polls.size()));
        Log.w("Random Poll", randomPoll.toString());
        if (llRandomPollMain != null){
            llRandomPollMain.setVisibility(View.VISIBLE);
        }
        if (pbRandomPollMain != null){
            pbRandomPollMain.setVisibility(View.GONE);
        }
        return randomPoll;
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

        final Button btnAccountMain = (Button) findViewById(R.id.btnAccountMain);

        btnAccountMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                startActivity(intent);
            }
        });

        if (user == null) getUserInfo();
        if (!pollsGot) getPolls();

        // TODO rastgele anket kısmı yapılacak

    }
}
