package com.ygznsl.noskurt.oyla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;

import java.util.List;

public class PollActivity extends AppCompatActivity {

    private boolean guiInitialized = false;
    private List<Option> options;
    private List<Poll> polls;
    private User user;
    private String userKey;

    private void initializeGui(){
        final Bundle extras = getIntent().getExtras();
        userKey = extras.getString("userKey");
        user = (User) extras.getSerializable("user");
        polls = (List<Poll>) extras.getSerializable("polls");
        options = (List<Option>) extras.getSerializable("options");

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        if (!guiInitialized) initializeGui();
    }

}
