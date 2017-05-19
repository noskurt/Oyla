package com.ygznsl.noskurt.oyla;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;
import com.ygznsl.noskurt.oyla.helper.Consumer;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;
import com.ygznsl.noskurt.oyla.helper.Predicate;

import java.text.Collator;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PollActivity extends AppCompatActivity {

    private final Locale locale = new Locale("tr", "TR");
    private boolean guiInitialized = false;
    private boolean anonymous;
    private List<Category> categories;
    private User user;
    private Poll poll;

    private LinearLayout llOptionsPoll;
    private ImageView imgPollGenderPoll;
    private TextView txtTitlePoll;
    private TextView txtCategoryPoll;
    private TextView txtPollUserPoll;
    private TextView txtPollPublishDatePoll;
    private TextView txtPollStatsPoll;
    private TextView txtUserAlreadyVotedPoll;
    private TextView txtUserCannotVotePoll;
    private ProgressBar pbPollUserPoll;
    private ProgressBar pbPollPublishDatePoll;
    private ProgressBar pbPollStatsPoll;
    private Button btnVotePoll;
    private Button btnStatsPoll;

    private void showStats(){
        final Intent intent = new Intent(this, PollAnalyticsActivity.class);
        intent.putExtra("poll", poll);
        intent.putExtra("anonymous", anonymous);
        startActivity(intent);
    }

    private void initializeGui(final OylaDatabase oyla) {
        categories = Category.getCategories(this).get();

        final Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable("user");
        anonymous = extras.getBoolean("anonymous");
        poll = (Poll) extras.getSerializable("poll");

//        setTitle(new Nullable<>(poll).orElse(new Function<Poll, String>() {
//            @Override
//            public String apply(Poll in) {
//                return "Oyla - " + in.getTitle();
//            }
//        }, "Oyla"));

//        setTitle(" Kategori: "+Entity.findMatches(categories, new Predicate<Category>() {
//            @Override
//            public boolean test(Category in) {
//                return in.getId() == poll.getCategory();
//            }
//        }).orElse(new Function<Category, String>() {
//            @Override
//            public String apply(Category in) {
//                return in.getName();
//            }
//        }, ""));

        final List<Option> options = oyla.optionsOfPoll(poll);
        Collections.sort(options, new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                final Collator collator = Collator.getInstance(locale);
                return collator.compare(o1.getTitle(), o2.getTitle());
            }
        });

        llOptionsPoll = (LinearLayout) findViewById(R.id.llOptionsPoll);
        imgPollGenderPoll = (ImageView) findViewById(R.id.imgPollGenderPoll);
        txtTitlePoll = (TextView) findViewById(R.id.txtTitlePoll);
        txtCategoryPoll = (TextView) findViewById(R.id.txtCategoryPoll);
        txtPollUserPoll = (TextView) findViewById(R.id.txtPollUserPoll);
        txtPollPublishDatePoll = (TextView) findViewById(R.id.txtPollPublishDatePoll);
        txtPollStatsPoll = (TextView) findViewById(R.id.txtPollStatsPoll);
        txtUserAlreadyVotedPoll = (TextView) findViewById(R.id.txtUserAlreadyVotedPoll);
        txtUserCannotVotePoll = (TextView) findViewById(R.id.txtUserCannotVotePoll);
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

        txtCategoryPoll.setText(Entity.findMatches(categories, new Predicate<Category>() {
            @Override
            public boolean test(Category in) {
                return in.getId() == poll.getCategory();
            }
        }).orElse(new Function<Category, String>() {
            @Override
            public String apply(Category in) {
                return in.getName();
            }
        }, ""));

        txtPollUserPoll.setText(Entity.findMatches(oyla.getUsers(), new Predicate<User>() {
            @Override
            public boolean test(User in) {
                return in.getId() == poll.getUser();
            }
        }).orElse(new Function<User, String>() {
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
        } finally {
            txtPollPublishDatePoll.setVisibility(View.VISIBLE);
            pbPollPublishDatePoll.setVisibility(View.GONE);
        }

        txtPollStatsPoll.setText(String.valueOf(oyla.votesOfPoll(poll).size()));
        txtPollStatsPoll.setVisibility(View.VISIBLE);
        pbPollStatsPoll.setVisibility(View.GONE);

        final List<CheckBox> checkBoxes = new LinkedList<>();
        final List<RadioButton> radioButtons = new LinkedList<>();

        llOptionsPoll.removeAllViews();

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
                    final List<Integer> optionIds = Entity.findAllMatches(checkBoxes, new Predicate<CheckBox>() {
                        @Override
                        public boolean test(CheckBox in) {
                            return in.isChecked();
                        }
                    }, new Function<CheckBox, Integer>() {
                        @Override
                        public Integer apply(CheckBox in) {
                            return (Integer) in.getTag();
                        }
                    });
                    if (!optionIds.isEmpty()) btnVotePoll.setEnabled(false);
                    for (int oid : optionIds){
                        final Vote vote = new Vote();
                        vote.setO(oid);
                        vote.setU(user.getId());
                        vote.setVd(Vote.DATE_FORMAT.format(Calendar.getInstance(locale).getTime()));

                        final DatabaseReference pushed = Entity.getDatabase().getReference().child("vote").push();
                        pushed.setValue(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    oyla.addVote(vote);
                                }
                            }
                        });
                    }
                    btnVotePoll.setEnabled(true);
                    showStats();
                } else {
                    Entity.findMatches(radioButtons, new Predicate<RadioButton>() {
                        @Override
                        public boolean test(RadioButton in) {
                            return in.isChecked();
                        }
                    }).operate(new Consumer<RadioButton>() {
                        @Override
                        public void accept(RadioButton in) {
                            final Vote vote = new Vote();
                            vote.setU(user.getId());
                            vote.setO((Integer) in.getTag());
                            vote.setVd(Vote.DATE_FORMAT.format(Calendar.getInstance(locale).getTime()));

                            final DatabaseReference pushed = Entity.getDatabase().getReference().child("vote").push();
                            pushed.setValue(vote).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        oyla.addVote(vote);
                                        showStats();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        btnStatsPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStats();
            }
        });

        if (!anonymous && oyla.hasUserVotedPoll(user, poll)){
            final List<Integer> votedOptionIds = Entity.map(oyla.optionsUserVoted(user, poll), new Function<Option, Integer>() {
                @Override
                public Integer apply(Option in) {
                    return in.getId();
                }
            });

            if (poll.getMult() == 1){
                for (CheckBox cb : checkBoxes){
                    cb.setEnabled(false);
                    if (votedOptionIds.contains(cb.getTag())){
                        cb.setChecked(true);
                    }
                }
            } else {
                for (RadioButton rb : radioButtons){
                    rb.setEnabled(false);
                    if (votedOptionIds.contains(rb.getTag())){
                        rb.setChecked(true);
                    }
                }
            }

            txtUserAlreadyVotedPoll.setVisibility(View.VISIBLE);
            btnVotePoll.setEnabled(false);
        } else {
            txtUserAlreadyVotedPoll.setVisibility(View.GONE);
        }

        if (!anonymous && !poll.getGenders().equals("B")){
            if (!poll.getGenders().equals(user.getGender())){
                btnVotePoll.setEnabled(false);
                txtUserCannotVotePoll.setText(String.format(locale,
                        getString(R.string.text_userCannotVote), poll.getGenders().equals("K") ? "kadınlara" : "erkeklere"));
                txtUserCannotVotePoll.setVisibility(View.VISIBLE);
            }
        }

        if (anonymous) {
            txtUserCannotVotePoll.setText(getString(R.string.text_anonymousUserCannotVote));
            txtUserCannotVotePoll.setVisibility(View.VISIBLE);
            btnVotePoll.setEnabled(false);
        }

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        setTitle(" Oyla");
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        initializeGui(oyla);
    }

}
