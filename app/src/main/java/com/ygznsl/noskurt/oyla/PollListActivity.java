package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PollListActivity extends AppCompatActivity {

    private boolean guiInitialized = false;
    private boolean anonymous;
    private User user;

    private LinearLayout llMainLayoutPollList;
    private RecyclerView rvPollList;
    private PollViewAdapter adapter;
    private ProgressBar pbPollList;

    private void initializeGui(final OylaDatabase oyla){
        final Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable("user");
        anonymous = extras.getBoolean("anonymous");
        final Nullable<String> scope = new Nullable<>(extras.getString("scope"));

        rvPollList = (RecyclerView) findViewById(R.id.rvPollList);
        rvPollList.setLayoutManager(new LinearLayoutManager(this));
        pbPollList = (ProgressBar) findViewById(R.id.pbPollList);
        llMainLayoutPollList = (LinearLayout) findViewById(R.id.llMainLayoutPollList);

        final List<Poll> polls = new LinkedList<>(new HashSet<>(anonymous ? oyla.getPolls() :
                scope.orElse(new Function<String, List<Poll>>() {
                    @Override
                    public List<Poll> apply(String in) {
                        if (in.equals("polls")) return oyla.pollsUserCreated(user);
                        if (in.equals("votes")) return oyla.pollsUserVoted(user);
                        return oyla.getPolls();
                    }
                }, oyla.getPolls())
        ));

        adapter = new PollViewAdapter(this, polls, oyla);
        rvPollList.setAdapter(adapter);

        rvPollList.addOnItemTouchListener(new RecyclerItemClick(this, rvPollList, new RecyclerItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Intent intent = new Intent(PollListActivity.this, PollActivity.class);
                final Poll poll = polls.get(position);

                intent.putExtra("user", user);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("poll", poll);
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
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
    }

}