package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Lists;

import java.text.ParseException;
import java.util.ArrayList;

import java.util.List;

public class PollActivity extends AppCompatActivity {

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private boolean guiInitialized = false;
    private boolean anonymous;
    private List<Category> categories;
    private Lists lists;
    private User user;
    private String userKey;
    private Poll poll;

    private LinearLayout llOptionsPoll;
    private ImageView imgPollGenderPoll;
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

    private void initializeGui() {
        categories = Category.getCategories(this).get();

        final Bundle extras = getIntent().getExtras();
        userKey = extras.getString("userKey");
        user = (User) extras.getSerializable("user");
        anonymous = extras.getBoolean("anonymous");
        poll = (Poll) extras.getSerializable("poll");
        lists = (Lists) extras.getSerializable("lists");

        llOptionsPoll = (LinearLayout) findViewById(R.id.llOptionsPoll);
        imgPollGenderPoll = (ImageView) findViewById(R.id.imgPollGenderPoll);
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
        imgPollGenderPoll.setImageResource(
                poll.getGenders().equals("B") ?
                        R.drawable.gender_both :
                        (poll.getGenders().equals("E") ?
                                R.drawable.gender_male :
                                R.drawable.gender_female
                        )
        );

        txtCategoryPoll.setText(Entity.findMatches(categories, new Function<Category, Integer>() {
            @Override
            public Integer apply(Category in) {
                return in.getId();
            }
        }, poll.getCategory()).orElse(new Function<Category, String>() {
            @Override
            public String apply(Category in) {
                return in.getName();
            }
        }, ""));

        txtPollUserPoll.setText(Entity.findMatches(lists.USERS, new Function<User, Integer>() {
            @Override
            public Integer apply(User in) {
                return in.getId();
            }
        }, poll.getUser()).orElse(new Function<User, String>() {
            @Override
            public String apply(User in) {
                return in.getName();
            }
        }, ""));

        txtPollUserPoll.setVisibility(View.VISIBLE);
        pbPollUserPoll.setVisibility(View.GONE);

        try {
            txtPollPublishDatePoll.setText(User.DATE_FORMAT.format(Poll.DATE_FORMAT.parse(poll.getPdate())));
        } catch (ParseException ex) {
            txtPollPublishDatePoll.setText(poll.getPdate());
        }
        txtPollPublishDatePoll.setVisibility(View.VISIBLE);
        pbPollPublishDatePoll.setVisibility(View.GONE);

        final int minOptionId = lists.OPTIONS.get(0).getId();
        final int maxOptionId = lists.OPTIONS.get(lists.OPTIONS.size() - 1).getId();

        txtPollStatsPoll.setText(String.valueOf(Entity.findAllRangeMatches(lists.VOTES, new Function<Vote, Integer>() {
            @Override
            public Integer apply(Vote in) {
                return in.getO();
            }
        }, minOptionId, maxOptionId).size()));
        txtPollStatsPoll.setVisibility(View.VISIBLE);
        pbPollStatsPoll.setVisibility(View.GONE);

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
            for (Option o : lists.OPTIONS) {
                final CheckBox checkBox = new CheckBox(this);
                checkBox.setText(o.getTitle());
                checkBox.setTag(o.getId());
                llOptionsPoll.addView(checkBox);
                checkBoxes.add(checkBox);
            }
        } else {
            final RadioGroup radioGroup = new RadioGroup(this);
            for (Option o : lists.OPTIONS) {
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
