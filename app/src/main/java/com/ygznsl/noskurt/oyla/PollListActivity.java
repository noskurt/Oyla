package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.Lists;

import java.util.LinkedList;
import java.util.List;

public class PollListActivity extends AppCompatActivity {

    private boolean guiInitialized = false;
    private boolean anonymous;
    private Lists lists;
    private User user;
    private String userKey;

    private LinearLayout llMainLayoutPollList;
    private RecyclerView rvPollList;
    private PollViewAdapter adapter;
    private ProgressBar pbPollList;

    private void initializeGui(){
        final Bundle extras = getIntent().getExtras();
        userKey = extras.getString("userKey");
        user = (User) extras.getSerializable("user");
        anonymous = extras.getBoolean("anonymous");
        lists = (Lists) extras.getSerializable("lists");

        rvPollList = (RecyclerView) findViewById(R.id.rvPollList);
        rvPollList.setLayoutManager(new LinearLayoutManager(this));
        pbPollList = (ProgressBar) findViewById(R.id.pbPollList);
        llMainLayoutPollList = (LinearLayout) findViewById(R.id.llMainLayoutPollList);

        adapter = new PollViewAdapter(this, lists.POLLS, lists.OPTIONS);
        rvPollList.setAdapter(adapter);

        rvPollList.addOnItemTouchListener(new RecyclerItemClick(this, rvPollList, new RecyclerItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Intent intent = new Intent(PollListActivity.this, PollActivity.class);
                final Poll poll = lists.POLLS.get(position);

                final List<Option> list = new LinkedList<>();
                for (Option o : lists.OPTIONS){
                    if (o.getPoll() == poll.getId()){
                        list.add(o);
                    }
                }

                final Lists newLists = new Lists();
                newLists.USERS = lists.USERS;
                newLists.VOTES = lists.VOTES;
                newLists.POLLS = lists.POLLS;
                newLists.OPTIONS = list;

                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("poll", poll);
                intent.putExtra("lists", newLists);
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