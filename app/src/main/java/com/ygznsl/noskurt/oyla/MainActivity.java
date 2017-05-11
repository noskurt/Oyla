package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Lists;
import com.ygznsl.noskurt.oyla.helper.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final List<Option> options = Collections.synchronizedList(new LinkedList<Option>());
    private final List<User> users = Collections.synchronizedList(new LinkedList<User>());
    private final List<Poll> polls = Collections.synchronizedList(new LinkedList<Poll>());
    private final List<Vote> votes = Collections.synchronizedList(new LinkedList<Vote>());
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean guiInitialized = false;
    private boolean pollsGot = false;
    private List<Category> categories;
    private Poll randomPoll = null;
    private User user = null;
    private String userKey;
    private boolean anonymous;

    private RelativeLayout llRandomPollMain;
    private ProgressBar pbUserNameMain;
    private ProgressBar pbPollCountMain;
    private ProgressBar pbRandomPollMain;
    private TextView txtUserNameMain;
    private TextView txtPollCountMain;
    private TextView txtPollTitleMain;
    private TextView txtPollPublishDateMain;
    private TextView txtPollCategoryMain;
    private ImageView imgPollGenderMain;

    public MainActivity(){
        getPolls();
        getUsers();
        getOptions();
        getVotes();
    }

    private void getPolls(){
        db.child("poll").keepSynced(true);
        db.child("poll").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            final Poll poll = ds.getValue(Poll.class);
                            if (!polls.contains(poll)){
                                polls.add(poll);
                            }
                        }
                        Collections.sort(polls, new Comparator<Poll>() {
                            @Override
                            public int compare(Poll p1, Poll p2) {
                                return Integer.valueOf(p2.getId()).compareTo(p1.getId());
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (txtPollCountMain != null){
                                    txtPollCountMain.setText(String.valueOf(polls.size()));
                                    txtPollCountMain.setVisibility(View.VISIBLE);
                                }
                                if (pbPollCountMain != null){
                                    pbPollCountMain.setVisibility(GONE);
                                }
                                pollsGot = true;
                                selectRandomPoll();
                            }
                        });

                    }
                }, "getPolls.onDataChange").start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        db.child("poll").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Poll poll = dataSnapshot.getValue(Poll.class);
                polls.remove(poll);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("poll.cancelled", databaseError.getMessage());
            }
        });
    }

    private void getUsers(){
        db.child("user").keepSynced(true);
        db.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Nullable<FirebaseUser> currentUser = new Nullable<>(auth.getCurrentUser());
                final Function<FirebaseUser, String> mapper = new Function<FirebaseUser, String>() {
                    @Override
                    public String apply(FirebaseUser in) {
                        return in.getEmail();
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            final User u = ds.getValue(User.class);
                            final String key = ds.getKey();
                            if (!users.contains(u)){
                                users.add(u);
                            }
                            if (u.getEmail().equals(currentUser.orElse(mapper, ""))){
                                user = u;
                                userKey = key;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (txtUserNameMain != null){
                                            txtUserNameMain.setText(user.getName());
                                            txtUserNameMain.setVisibility(View.VISIBLE);
                                        }
                                        if (pbUserNameMain != null){
                                            pbUserNameMain.setVisibility(GONE);
                                        }
                                    }
                                });

                            }
                        }
                        Collections.sort(users, new Comparator<User>() {
                            @Override
                            public int compare(User u1, User u2) {
                                return Integer.valueOf(u1.getId()).compareTo(u2.getId());
                            }
                        });
                    }
                }, "getUsers.onDataChange").start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        db.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                users.remove(user);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("user.cancelled", databaseError.getMessage());
            }
        });
    }

    private void getOptions(){
        db.child("option").keepSynced(true);
        db.child("option").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Option option = dataSnapshot.getValue(Option.class);
                options.add(option);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Option option = dataSnapshot.getValue(Option.class);
                options.remove(option);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("option.cancelled", databaseError.getMessage());
            }
        });
    }

    private void getVotes(){
        db.child("vote").keepSynced(true);
        db.child("vote").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Vote vote = dataSnapshot.getValue(Vote.class);
                votes.add(vote);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Vote vote = dataSnapshot.getValue(Vote.class);
                votes.remove(vote);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("vote.cancelled", databaseError.getMessage());
            }
        });
    }

    private void selectRandomPoll() {
        do {
            randomPoll = polls.get(new Random().nextInt(polls.size()));
        } while (!randomPoll.getGenders().equals("B"));

        txtPollTitleMain.setText(randomPoll.getTitle());
        txtPollPublishDateMain.setText(User.DATE_FORMAT.format(tryParseDate(Poll.DATE_FORMAT, randomPoll.getPdate(), new Date())));
        imgPollGenderMain.setImageResource(
                randomPoll.getGenders().equals("B") ?
                        R.drawable.gender_both :
                        (randomPoll.getGenders().equals("E") ?
                                R.drawable.gender_male :
                                R.drawable.gender_female
                        )
        );

        for (Category c : categories){
            if (c.getId() == randomPoll.getCategory()){
                txtPollCategoryMain.setText(c.getName());
                break;
            }
        }

        pbRandomPollMain.setVisibility(GONE);
        llRandomPollMain.setVisibility(View.VISIBLE);
    }

    private void initializeGui() {
        anonymous = getIntent().getExtras().getBoolean("anonymous");
        categories = Category.getCategories(this).get();

        pbUserNameMain = (ProgressBar) findViewById(R.id.pbUserNameMain);
        pbPollCountMain = (ProgressBar) findViewById(R.id.pbPollCountMain);
        pbRandomPollMain = (ProgressBar) findViewById(R.id.pbRandomPollMain);

        llRandomPollMain = (RelativeLayout) findViewById(R.id.llRandomPollMain);
        txtUserNameMain = (TextView) findViewById(R.id.txtUserNameMain);
        txtPollCountMain = (TextView) findViewById(R.id.txtPollCountMain);
        txtPollTitleMain = (TextView) findViewById(R.id.txtPollTitleMain);
        txtPollPublishDateMain = (TextView) findViewById(R.id.txtPollPublishDateMain);
        txtPollCategoryMain = (TextView) findViewById(R.id.txtPollCategoryMain);
        imgPollGenderMain = (ImageView) findViewById(R.id.imgPollGenderMain);

        final Button btnBrowsePollsMain = (Button) findViewById(R.id.btnBrowsePollsMain);
        final Button btnCreatePollMain = (Button) findViewById(R.id.btnCreatePollMain);
        final Button btnAccountMain = (Button) findViewById(R.id.btnAccountMain);

        if (anonymous) btnCreatePollMain.setEnabled(false);

        llRandomPollMain.setOnClickListener(this);

        btnAccountMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Lists lists = new Lists();
                lists.OPTIONS = options;
                lists.POLLS = polls;
                lists.USERS = users;
                lists.VOTES = votes;

                final Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("lists", lists);
                startActivity(intent);
            }
        });

        btnBrowsePollsMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Lists lists = new Lists();
                lists.OPTIONS = options;
                lists.POLLS = polls;
                lists.USERS = users;
                lists.VOTES = votes;

                final Intent intent = new Intent(MainActivity.this, PollListActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("lists", lists);
                startActivity(intent);
            }
        });

        btnCreatePollMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO CreatePollActivity düzenle
                final Lists lists = new Lists();
                lists.OPTIONS = options;
                lists.POLLS = polls;
                lists.USERS = users;
                lists.VOTES = votes;

                final Intent intent = new Intent(MainActivity.this, CreatePollActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("lists", lists);
                startActivity(intent);
            }
        });

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!guiInitialized) initializeGui();
        if (pollsGot && user != null) selectRandomPoll();
    }

    public static Date tryParseDate(SimpleDateFormat sdf, String str, Date def){
        try {
            return sdf.parse(str);
        } catch (ParseException ex) {
            return def;
        }
    }

    @Override
    public void onClick(View view) {
        final List<Option> list = new LinkedList<>();
        for (Option o : options){
            if (o.getPoll() == randomPoll.getId()){
                list.add(o);
            }
        }

        final Lists lists = new Lists();
        lists.POLLS = polls;
        lists.USERS = users;
        lists.VOTES = votes;
        lists.OPTIONS = list;

        final Intent intent = new Intent(this, PollActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("userKey", userKey);
        intent.putExtra("anonymous", anonymous);
        intent.putExtra("poll", randomPoll);
        intent.putExtra("lists", lists);
        startActivity(intent);
    }

}