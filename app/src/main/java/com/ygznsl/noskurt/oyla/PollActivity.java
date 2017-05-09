package com.ygznsl.noskurt.oyla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PollActivity extends AppCompatActivity {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final List<Option> options = Collections.synchronizedList(new LinkedList<Option>());
    private final List<Vote> votes = Collections.synchronizedList(new LinkedList<Vote>());
    private boolean guiInitialized = false;
    private List<Category> categories;
    private List<Poll> polls;
    private User user;
    private String userKey;
    private Poll poll;

    private LinearLayout llOptionsPoll;
    private TextView txtTitlePoll;
    private TextView txtCategoryPoll;
    private TextView txtInfoPoll;
    private TextView txtStatsPoll;
    private Button btnVotePoll;
    private Button btnStatsPoll;

    private void getVotes(){
        db.child("vote").orderByChild("o").startAt(options.get(0).getId()).endAt(options.get(options.size() - 1).getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            votes.add(ds.getValue(Vote.class));
                        }
                        if (txtStatsPoll != null){
                            txtStatsPoll.setText(String.format(new Locale("tr", "TR"), "%d kez oylandı.", votes.size()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void initializeGui() {
        options.clear();
        categories = Category.getCategories(this);

        final Bundle extras = getIntent().getExtras();
        final List<Option> wholeOptions = (List<Option>) extras.getSerializable("options");
        userKey = extras.getString("userKey");
        user = (User) extras.getSerializable("user");
        polls = (List<Poll>) extras.getSerializable("polls");
        poll = (Poll) extras.getSerializable("poll");

        for (Option o : wholeOptions) {
            if (o.getPoll() == poll.getId()) {
                options.add(o);
            }
        }

        Collections.sort(options, new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                return new Integer(o1.getId()).compareTo(o2.getId());
            }
        });

        getVotes();

        llOptionsPoll = (LinearLayout) findViewById(R.id.llOptionsPoll);
        txtTitlePoll = (TextView) findViewById(R.id.txtTitlePoll);
        txtCategoryPoll = (TextView) findViewById(R.id.txtCategoryPoll);
        txtInfoPoll = (TextView) findViewById(R.id.txtInfoPoll);
        txtStatsPoll = (TextView) findViewById(R.id.txtStatsPoll);
        btnVotePoll = (Button) findViewById(R.id.btnVotePoll);
        btnStatsPoll = (Button) findViewById(R.id.btnStatsPoll);

        txtTitlePoll.setText(poll.getTitle());

        for (Category c : categories) {
            if (c.getId() == poll.getCategory()) {
                txtCategoryPoll.setText(c.getName());
                break;
            }
        }

        db.child("user").orderByChild("id").equalTo(poll.getUser()).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            final User u = ds.getValue(User.class);
                            txtInfoPoll.setText(String.format("%s tarafından\n%s tarihinde oluşturuldu.", u.getName(), poll.getPdate()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        for (Option o : options) {
            if (poll.getMult() == 1) {
                // TODO çek bax
            } else {
                // TODO radyo
            }
        }

        btnVotePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO oyla
            }
        });

        btnStatsPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO istatistikler activty gönder
            }
        });


//
//        final StringBuilder str = new StringBuilder()
//                .append("userKey: ").append(userKey).append("\r\n")
//                .append("user: ").append(user).append("\r\n")
//                .append("poll: ").append(poll).append("\r\n")
//                .append("polls.size() = ").append(polls.size()).append("\r\n")
//                .append("options.size() = ").append(options.size()).append("\r\n");
//
//        Toast.makeText(this, str.toString(), Toast.LENGTH_LONG).show();

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        if (!guiInitialized) initializeGui();
    }

}
