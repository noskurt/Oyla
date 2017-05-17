package com.ygznsl.noskurt.oyla;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Serializable, View.OnClickListener {

    private transient final FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean guiInitialized = false;
    private boolean pollsGot = false;
    private List<Category> categories;
    private Poll randomPoll = null;
    private User user = null;
    private String userKey;
    private boolean anonymous;

    private transient RelativeLayout llRandomPollMain;
    private transient ProgressBar pbUserNameMain;
    private transient ProgressBar pbPollCountMain;
    private transient ProgressBar pbRandomPollMain;
    private transient TextView txtUserNameMain;
    private transient TextView txtPollCountMain;
    private transient TextView txtPollTitleMain;
    private transient TextView txtPollPublishDateMain;
    private transient TextView txtPollCategoryMain;
    private transient ImageView imgPollGenderMain;

    private void getUsers(final OylaDatabase oyla) {
        Entity.getDatabase().getReference().child("user").keepSynced(true);
        Entity.getDatabase().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Nullable<FirebaseUser> currentUser = new Nullable<>(auth.getCurrentUser());
                final Function<FirebaseUser, String> mapper = new Function<FirebaseUser, String>() {
                    @Override
                    public String apply(FirebaseUser in) {
                        return new Nullable<>(in.getEmail()).orElse(new Function<String, String>() {
                            @Override
                            public String apply(String in) {
                                return in.toLowerCase(Locale.ENGLISH);
                            }
                        }, "");
                    }
                };
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            final User u = ds.getValue(User.class);
                            final String key = ds.getKey();
                            oyla.addUser(u);
                            if (!anonymous && u.getEmail().equals(currentUser.orElse(mapper, ""))) {
                                user = u;
                                userKey = key;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (txtUserNameMain != null) {
                                            txtUserNameMain.setText(user.getName());
                                            txtUserNameMain.setVisibility(View.VISIBLE);
                                        }
                                        if (pbUserNameMain != null) {
                                            pbUserNameMain.setVisibility(View.GONE);
                                        }
                                    }
                                });

                            }
                        }
                        oyla.sortUsersByIdAsc();
                        Entity.getDatabase().getReference().child("user").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                oyla.addUser(dataSnapshot.getValue(User.class));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                                final User user = dataSnapshot.getValue(User.class);
                                oyla.removeUser(user);
                                oyla.addUser(user);
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                oyla.removeUser(dataSnapshot.getValue(User.class));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }, "getUsers.onDataChange").start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getPolls(final OylaDatabase oyla) {
        Entity.getDatabase().getReference().child("poll").keepSynced(true);
        Entity.getDatabase().getReference().child("poll").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                oyla.getPolls().clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            final Poll poll = ds.getValue(Poll.class);
                            oyla.addPoll(poll);
                        }
                        oyla.sortPollsByIdDesc();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (txtPollCountMain != null) {
                                    txtPollCountMain.setText(String.valueOf(oyla.getPolls().size()));
                                    txtPollCountMain.setVisibility(View.VISIBLE);
                                }
                                if (pbPollCountMain != null) {
                                    pbPollCountMain.setVisibility(View.GONE);
                                }
                                pollsGot = true;
                                selectRandomPoll(oyla);
                            }
                        });

                        Entity.getDatabase().getReference().child("poll").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                oyla.addPoll(dataSnapshot.getValue(Poll.class));
                                if (txtPollCountMain != null) {
                                    txtPollCountMain.setText(String.valueOf(oyla.getPolls().size()));
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                                final Poll poll = dataSnapshot.getValue(Poll.class);
                                oyla.removePoll(poll);
                                oyla.addPoll(poll);
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                oyla.removePoll(dataSnapshot.getValue(Poll.class));
                                if (txtPollCountMain != null) {
                                    txtPollCountMain.setText(String.valueOf(oyla.getPolls().size()));
                                }
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }, "getPolls.onDataChange").start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getOptions(final OylaDatabase oyla) {
        Entity.getDatabase().getReference().child("option").keepSynced(true);
        Entity.getDatabase().getReference().child("option").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                oyla.addOption(dataSnapshot.getValue(Option.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                final Option option = dataSnapshot.getValue(Option.class);
                oyla.removeOption(option);
                oyla.addOption(option);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                oyla.removeOption(dataSnapshot.getValue(Option.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getVotes(final OylaDatabase oyla) {
        Entity.getDatabase().getReference().child("vote").keepSynced(true);
        Entity.getDatabase().getReference().child("vote").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                oyla.addVote(dataSnapshot.getValue(Vote.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                final Vote vote = dataSnapshot.getValue(Vote.class);
                oyla.removeVote(vote);
                oyla.addVote(vote);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                oyla.removeVote(dataSnapshot.getValue(Vote.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void selectRandomPoll(final OylaDatabase oyla) {
        randomPoll = oyla.randomPollForUser(user);

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

        txtPollCategoryMain.setText(Entity.findById(categories, randomPoll.getCategory()).orElse(new Function<Category, String>() {
            @Override
            public String apply(Category in) {
                return in.getName();
            }
        }, ""));

        pbRandomPollMain.setVisibility(View.GONE);
        llRandomPollMain.setVisibility(View.VISIBLE);
    }

    private void initializeGui(final OylaDatabase oyla) {
        anonymous = getIntent().getExtras().getBoolean("anonymous");
        categories = Category.getCategories(this).get();

        getUsers(oyla);
        getPolls(oyla);
        getOptions(oyla);
        getVotes(oyla);

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

        llRandomPollMain.setOnClickListener(this);

        btnAccountMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("anonymous", anonymous);
                startActivity(intent);
            }
        });

        btnBrowsePollsMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, PollListActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("scope", "all");
                startActivity(intent);
            }
        });

        btnCreatePollMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, CreatePollActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("anonymous", anonymous);
                startActivityForResult(intent, 4444);
            }
        });

        if (anonymous) {
            txtUserNameMain.setText(getString(R.string.text_anonymousUser));
            txtUserNameMain.setVisibility(View.VISIBLE);
            pbUserNameMain.setVisibility(View.GONE);
            btnCreatePollMain.setEnabled(false);
            btnAccountMain.setEnabled(false);
        }

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
        if (pollsGot && user != null) selectRandomPoll(oyla);
    }

    @Override
    public void onClick(View view) {
        final Intent intent = new Intent(this, PollActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("anonymous", anonymous);
        intent.putExtra("poll", randomPoll);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (pollsGot && user != null) selectRandomPoll(oyla);
        if (guiInitialized) txtPollCountMain.setText(String.valueOf(oyla.getPolls().size()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 4444 && resultCode == Activity.RESULT_OK) {
            final Poll poll = (Poll) data.getExtras().getSerializable("newPoll");
            if (poll != null) {
                final Intent intent = new Intent(this, PollActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("poll", poll);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Çıkış Yapıyorsunuz!")
                .setMessage("Çıkış yapmak istediğinize emin misiniz?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            auth.signOut();
                            final Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception ex) {
                            Toast.makeText(MainActivity.this, "Çıkış yapılırken bir hata meydana geldi:\r\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // İPTAL İŞLEMİ
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        builder.create().show();

        return super.onOptionsItemSelected(item);
    }

    public static Date tryParseDate(SimpleDateFormat sdf, String str, Date def) {
        try {
            return sdf.parse(str);
        } catch (ParseException ex) {
            return def;
        }
    }

}