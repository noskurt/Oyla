package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.Nullable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final List<Option> options = Collections.synchronizedList(new LinkedList<Option>());
    private final List<Poll> polls = Collections.synchronizedList(new LinkedList<Poll>());
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean guiInitialized = false;
    private boolean optionsGot = false;
    private boolean pollsGot = false;
    private User user = null;
    private String userKey;

    private LinearLayout llRandomPollMain;
    private ProgressBar pbUserNameMain;
    private ProgressBar pbPollCountMain;
    private ProgressBar pbRandomPollMain;
    private TextView txtUserNameMain;
    private TextView txtPollCountMain;

    public MainActivity(){
        db.child("poll").keepSynced(true);
        getUserInfo();
        getPolls();
        getOptions();
    }

    private void getUserInfo(){
        db.child("user").orderByChild("email").equalTo(auth.getCurrentUser().getEmail()).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void getOptions(){
        db.child("option").keepSynced(true);
        db.child("option").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    options.add(ds.getValue(Option.class));
                }
                optionsGot = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void selectRandomPoll() {
        Poll randomPoll;
        do {
            randomPoll = polls.get(new Random().nextInt(polls.size()));
        } while (!randomPoll.getGenders().equals("B"));

        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.card_view_poll_list, null);
        final TextView txtPollTitlePollView = (TextView) view.findViewById(R.id.txtPollTitlePollView);
        final TextView txtPollPublishDatePollView = (TextView) view.findViewById(R.id.txtPollPublishDatePollView);
        final TextView txtPollUserPollView = (TextView) view.findViewById(R.id.txtPollUserPollView);
        final TextView txtPollOptionsPollView = (TextView) view.findViewById(R.id.txtPollOptionsPollView);
        final ImageView imgPollGenderPollView = (ImageView) view.findViewById(R.id.imgPollGenderPollView);

        final Poll p = randomPoll;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, PollActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("polls", (Serializable) polls);
                intent.putExtra("poll", p);
                if (optionsGot) intent.putExtra("options", (Serializable) options);
                startActivity(intent);
            }
        });

        db.child("user").orderByChild("id").equalTo(randomPoll.getUser())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            txtPollUserPollView.setText(ds.getValue(User.class).getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

        txtPollTitlePollView.setText(randomPoll.getTitle());
        txtPollPublishDatePollView.setText(User.DATE_FORMAT.format(tryParseDate(Poll.DATE_FORMAT, randomPoll.getPdate(), new Date())));
        imgPollGenderPollView.setImageResource(
                randomPoll.getGenders().equals("B") ?
                        R.drawable.gender_both :
                        (randomPoll.getGenders().equals("E") ?
                                R.drawable.gender_male :
                                R.drawable.gender_female
                        )
        );

        if (optionsGot){
            final StringBuilder str = new StringBuilder();
            for (Option o : options){
                if (o.getPoll() == randomPoll.getId()){
                    str.append("\"").append(o.getTitle()).append("\", ");
                }
            }
            final String options = "[" + str.toString().trim().substring(0, str.toString().trim().length() - 1) + "]";
            txtPollOptionsPollView.setText(options.length() <= 50 ? options : options.substring(0, 48) + "...");
        } else {
            db.child("option").orderByChild("poll").equalTo(randomPoll.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final StringBuilder str = new StringBuilder();
                            for (DataSnapshot ds : dataSnapshot.getChildren()){
                                str.append("\"").append(ds.getValue(Option.class).getTitle()).append("\", ");
                            }
                            final String options = "[" + str.toString().trim().substring(0, str.toString().trim().length() - 1) + "]";
                            txtPollOptionsPollView.setText(options.length() <= 50 ? options : options.substring(0, 48) + "...");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
        }

        llRandomPollMain.removeAllViews();
        llRandomPollMain.addView(view, 0);
    }

    private void initializeGui() {
        pbUserNameMain = (ProgressBar) findViewById(R.id.pbUserNameMain);
        pbPollCountMain = (ProgressBar) findViewById(R.id.pbPollCountMain);
        pbRandomPollMain = (ProgressBar) findViewById(R.id.pbRandomPollMain);

        txtUserNameMain = (TextView) findViewById(R.id.txtUserNameMain);
        txtPollCountMain = (TextView) findViewById(R.id.txtPollCountMain);
        llRandomPollMain = (LinearLayout) findViewById(R.id.llRandomPollMain);

        final Button btnBrowsePollsMain = (Button) findViewById(R.id.btnBrowsePollsMain);
        final Button btnCreatePollMain = (Button) findViewById(R.id.btnCreatePollMain);
        final Button btnAccountMain = (Button) findViewById(R.id.btnAccountMain);

        btnAccountMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("polls", (Serializable) polls);
                if (optionsGot) intent.putExtra("options", (Serializable) options);
                startActivity(intent);
            }
        });

        btnBrowsePollsMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, PollListActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("polls", (Serializable) polls);
                if (optionsGot) intent.putExtra("options", (Serializable) options);
                startActivity(intent);
            }
        });

        btnCreatePollMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*final Intent intent = new Intent(MainActivity.this, CreatePollActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("polls", (Serializable) polls);
                if (optionsGot) intent.putExtra("options", (Serializable) options);
                startActivity(intent);*/
            }
        });

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (!guiInitialized) initializeGui();

            if (llRandomPollMain != null){
                llRandomPollMain.setVisibility(View.GONE);
            }
            if (pbRandomPollMain != null){
                pbRandomPollMain.setVisibility(View.VISIBLE);
            }

            if (pollsGot && user != null) selectRandomPoll();
        } catch (Exception ex) {
            Log.e("onCreate", ex.getMessage());
        } finally {
            if (llRandomPollMain != null){
                llRandomPollMain.setVisibility(View.VISIBLE);
            }
            if (pbRandomPollMain != null){
                pbRandomPollMain.setVisibility(View.GONE);
            }
        }
    }

    public static Date tryParseDate(SimpleDateFormat sdf, String str, Date def){
        try {
            return sdf.parse(str);
        } catch (ParseException ex) {
            return def;
        }
    }

}
