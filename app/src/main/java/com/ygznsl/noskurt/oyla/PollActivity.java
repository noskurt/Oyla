package com.ygznsl.noskurt.oyla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PollActivity extends AppCompatActivity {

    private boolean guiInitialized = false;
    private final List<Option> options = Collections.synchronizedList(new LinkedList<Option>());
    private List<Poll> polls;
    private User user;
    private String userKey;
    private Poll poll;

    private void initializeGui(){
        options.clear();

        final Bundle extras = getIntent().getExtras();
        final List<Option> wholeOptions = (List<Option>) extras.getSerializable("options");
        userKey = extras.getString("userKey");
        user = (User) extras.getSerializable("user");
        polls = (List<Poll>) extras.getSerializable("polls");
        poll = (Poll) extras.getSerializable("poll");

        for (Option o : wholeOptions){
            if (o.getPoll() == poll.getId()){
                options.add(o);
            }
        }

        final StringBuilder str = new StringBuilder()
                .append("userKey: ").append(userKey).append("\r\n")
                .append("user: ").append(user).append("\r\n")
                .append("poll: ").append(poll).append("\r\n")
                .append("polls.size() = ").append(polls.size()).append("\r\n")
                .append("options.size() = ").append(options.size()).append("\r\n");

        Toast.makeText(this, str.toString(), Toast.LENGTH_LONG).show();

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        if (!guiInitialized) initializeGui();
    }

}
