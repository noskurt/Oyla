package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PollActivity extends AppCompatActivity {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final List<Vote> votes = Collections.synchronizedList(new LinkedList<Vote>());
    private boolean guiInitialized = false;
    private List<Category> categories;
    private List<Option> options;
    private List<Poll> polls;
    private User user;
    private String userKey;
    private Poll poll;

    private LinearLayout llOptionsPoll;
    private TextView txtTitlePoll;
    private TextView txtCategoryPoll;
    private TextView txtPollUserPoll;
    private TextView txtPollPublishDatePoll;
    private TextView txtPollStatsPoll;
    private ProgressBar pbPollUserPoll;
    private ProgressBar pbPollPublishDatePoll;
    private ProgressBar pbPollStatsPoll;
    private Button btnVotePoll;
    private Button btnStatsPoll;

    private void getVotes() {
        final int minId = options.get(0).getId();
        final int maxId = options.get(options.size() - 1).getId();
        Log.w("getVotes", String.valueOf(minId));
        Log.w("getVotes", String.valueOf(maxId));

        db.child("vote").keepSynced(false);

        /*db.child("vote").orderByChild("o").startAt(minId).endAt(maxId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        try {
                            Log.w("onChildAdded", "dataSnapshot alındı: " + dataSnapshot.toString());
                            final Vote v = new Vote();
                            v.setO(dataSnapshot.child("o").getValue(Integer.class));
                            v.setU(dataSnapshot.child("u").getValue(Integer.class));
                            v.setVd(dataSnapshot.child("vd").getValue(String.class));
                            votes.add(v);
                            Log.w("onChildAdded", "Oy alındı: " + v.toString());
                        } catch (Exception ex) {
                            Log.e("onChildAdded", ex.getMessage());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });*/

        /*db.child("vote").orderByChild("o").startAt(minId).endAt(maxId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.w("onDataChange", "Oylar alınıyor...");
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            try {
                                final Vote v = new Vote();
                                v.setO(ds.child("o").getValue(Integer.class));
                                v.setU(ds.child("u").getValue(Integer.class));
                                v.setVd(ds.child("vd").getValue(String.class));
                                votes.add(v);
                                Log.w("onDataChange", "Oy alındı: " + v.toString());
                            } catch (Exception ex) {
                                Log.e("onDataChange", ex.getMessage());
                            }
                        }
                        Log.w("onDataChange", "Oylar alındı.");
                        if (txtPollStatsPoll != null){
                            txtPollStatsPoll.setText(String.valueOf(votes.size()));
                            txtPollStatsPoll.setVisibility(View.VISIBLE);
                        }
                        if (pbPollStatsPoll != null){
                            pbPollStatsPoll.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });*/
    }

    private void initializeGui() {
        categories = Category.getCategories(this);

        final Bundle extras = getIntent().getExtras();
        userKey = extras.getString("userKey");
        user = (User) extras.getSerializable("user");
        polls = (List<Poll>) extras.getSerializable("polls");
        options = (List<Option>) extras.getSerializable("options");
        poll = (Poll) extras.getSerializable("poll");

        Collections.sort(options, new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }
        });

        getVotes();

        llOptionsPoll = (LinearLayout) findViewById(R.id.llOptionsPoll);
        txtTitlePoll = (TextView) findViewById(R.id.txtTitlePoll);
        txtCategoryPoll = (TextView) findViewById(R.id.txtCategoryPoll);
        txtPollUserPoll = (TextView) findViewById(R.id.txtPollUserPoll);
        txtPollPublishDatePoll = (TextView) findViewById(R.id.txtPollPublishDatePoll);
        txtPollStatsPoll = (TextView) findViewById(R.id.txtPollStatsPoll);
        pbPollUserPoll = (ProgressBar) findViewById(R.id.pbPollUserPoll);
        pbPollPublishDatePoll = (ProgressBar) findViewById(R.id.pbPollPublishDatePoll);
        pbPollStatsPoll = (ProgressBar) findViewById(R.id.pbPollStatsPoll);
        btnVotePoll = (Button) findViewById(R.id.btnVotePoll);
        btnStatsPoll = (Button) findViewById(R.id.btnStatsPoll);

        txtTitlePoll.setText(poll.getTitle());
        txtTitlePoll.setCompoundDrawables(
                poll.getGenders().equals("B") ?
                        getDrawable(R.drawable.gender_both) :
                        (
                                poll.getGenders().equals("E") ?
                                        getDrawable(R.drawable.gender_male) :
                                        getDrawable(R.drawable.gender_female)
                        ),
                null, null, null
        );

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
                            txtPollUserPoll.setText(u.getName());
                            txtPollUserPoll.setVisibility(View.VISIBLE);
                            pbPollUserPoll.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

        try {
            txtPollPublishDatePoll.setText(User.DATE_FORMAT.format(Poll.DATE_FORMAT.parse(poll.getPdate())));
        } catch (ParseException ex) {
            txtPollPublishDatePoll.setText(poll.getPdate());
        }
        txtPollPublishDatePoll.setVisibility(View.VISIBLE);
        pbPollPublishDatePoll.setVisibility(View.GONE);

        /**
         * Bu çekbakslarla radio butonları tutmak için
         * liste oluşturmak durumunda kaldım. Radio grup
         * bir işe yaramıyor içinden hangini seçtiğimi
         * tespit edemiyorum mecbur liste yaptım. Altta
         * da döngüye sokup kimi seçtiğmi tespit ediyorum.
         * Tespit ederken TAG kullandım içine Firebaseden
         * gelen ID yi koydum hadi eyv :)
         */

        final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        final ArrayList<RadioButton> radioButtons = new ArrayList<>();

        if (poll.getMult() == 1) {
            for (Option o : options) {
                final CheckBox checkBox = new CheckBox(this);
                checkBox.setText(o.getTitle());
                checkBox.setTag(o.getId());
                llOptionsPoll.addView(checkBox);
                checkBoxes.add(checkBox);
            }
        } else {
            final RadioGroup radioGroup = new RadioGroup(this);
            for (Option o : options) {
                final RadioButton radioButton = new RadioButton(this);
                radioButton.setText(o.getTitle());
                radioButton.setTag(o.getId());
                radioGroup.addView(radioButton);
                radioButtons.add(radioButton);
            }
            llOptionsPoll.addView(radioGroup);
        }

        btnVotePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poll.getMult() == 1) {
                    for (CheckBox c : checkBoxes) {
                        Toast.makeText(PollActivity.this, c.isChecked() + " " + c.getTag(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    for (RadioButton r : radioButtons) {
                        Toast.makeText(PollActivity.this, r.isChecked() + " " + r.getTag(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnStatsPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PollActivity.this, PollAnalyticsActivity.class);
                startActivity(intent);
                // TODO istatistikler activty gönder
            }
        });

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        if (!guiInitialized) initializeGui();
    }

}
