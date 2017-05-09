package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;

import java.io.Serializable;
import java.util.List;

public class PollListActivity extends AppCompatActivity {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private boolean optionsGot = false;
    private boolean guiInitialized = false;
    private List<Category> categories;
    private List<Option> options;
    private List<Poll> polls;
    private User user;
    private String userKey;

    private LinearLayout llMainLayoutPollList;
    private RecyclerView rvPollList;
    private PollViewAdapter adapter;
    private ProgressBar pbPollList;

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

    private void initializeGui(){
        categories = Category.getCategories(this);

        final Bundle extras = getIntent().getExtras();
        userKey = extras.getString("userKey");
        user = (User) extras.getSerializable("user");
        polls = (List<Poll>) extras.getSerializable("polls");
        options = (List<Option>) extras.getSerializable("options");

        if (options == null) getOptions();
        else optionsGot = true;

        rvPollList = (RecyclerView) findViewById(R.id.rvPollList);
        rvPollList.setLayoutManager(new LinearLayoutManager(this));
        pbPollList = (ProgressBar) findViewById(R.id.pbPollList);
        llMainLayoutPollList = (LinearLayout) findViewById(R.id.llMainLayoutPollList);

        adapter = new PollViewAdapter(this, polls, options);
        rvPollList.setAdapter(adapter);

        rvPollList.addOnItemTouchListener(new RecyclerItemClick(this, rvPollList, new RecyclerItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Intent intent = new Intent(PollListActivity.this, PollActivity.class);
                final Poll poll = polls.get(position);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("polls", (Serializable) polls);
                intent.putExtra("poll", poll);
                if (optionsGot) intent.putExtra("options", (Serializable) options);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {}
        }));

        pbPollList.setVisibility(View.GONE);
        llMainLayoutPollList.setVisibility(View.VISIBLE);

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        if (!guiInitialized) initializeGui();
    }

}