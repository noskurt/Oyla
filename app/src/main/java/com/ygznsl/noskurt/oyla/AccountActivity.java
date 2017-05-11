package com.ygznsl.noskurt.oyla;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Lists;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class AccountActivity extends AppCompatActivity {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private final Locale locale = new Locale("tr", "TR");
    private final Lists myPolls = new Lists();
    private final Lists myVotes = new Lists();
    private boolean guiInitialized = false;
    private boolean anonymous;
    private FirebaseUser currentUser;
    private List<City> cities;
    private User user = null;
    private String userKey;
    private Lists lists;

    private TextView txtEmailAccount;
    private TextView txtUserNameAccount;
    private TextView txtBirthDateAccount;
    private Spinner spinnerCityAccount;
    private Button btnUpdateAccount;
    private Button btnMyPollsAccount;
    private Button btnMyVotesAccount;
    private Button btnChangePasswordAccount;
    private Button btnLogoutAccount;

    private AlertDialog dialogPasswordUpdate;
    private ProgressBar pbPasswordUpdate;
    private ScrollView llResetPassword;
    private EditText txtPasswordNewUpdate;
    private EditText txtPasswordNewAgainUpdate;

    private void calculateMyPolls(){
        final List<Poll> list = new LinkedList<>();
        for (Poll p : lists.POLLS){
            if (p.getUser() == user.getId()){
                list.add(p);
            }
        }

        myPolls.USERS = lists.USERS;
        myPolls.VOTES = lists.VOTES;
        myPolls.OPTIONS = lists.OPTIONS;
        myPolls.POLLS = list;

        btnMyPollsAccount.setEnabled(!myPolls.POLLS.isEmpty());
    }

    private void calculateMyVotes(){
        final Set<Integer> optionIds = new TreeSet<>();
        for (Vote v : lists.VOTES){
            if (v.getU() == user.getId()){
                optionIds.add(v.getO());
            }
        }

        final Set<Integer> pollIds = new TreeSet<>();
        for (Option o : lists.OPTIONS){
            if (optionIds.contains(o.getId())){
                pollIds.add(o.getPoll());
            }
        }

        final List<Poll> listPolls = new LinkedList<>();
        for (Poll p : lists.POLLS){
            if (pollIds.contains(p.getId())){
                listPolls.add(p);
            }
        }

        myVotes.USERS = lists.USERS;
        myVotes.VOTES = lists.VOTES;
        myVotes.OPTIONS = lists.OPTIONS;
        myVotes.POLLS = listPolls;

        btnMyVotesAccount.setEnabled(!myVotes.POLLS.isEmpty());
    }

    private void initializeGui(){
        currentUser = auth.getCurrentUser();
        if (currentUser == null) finish();

        final Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable("user");
        userKey = extras.getString("userKey");
        anonymous = extras.getBoolean("anonymous");
        lists = (Lists) extras.getSerializable("lists");

        txtEmailAccount = (TextView) findViewById(R.id.txtEmailAccount);
        txtUserNameAccount = (EditText) findViewById(R.id.txtUserNameAccount);
        txtBirthDateAccount = (TextView) findViewById(R.id.txtBirthDateAccount);
        spinnerCityAccount = (Spinner) findViewById(R.id.spinnerCityAccount);
        btnUpdateAccount = (Button) findViewById(R.id.btnUpdateAccount);
        btnMyPollsAccount = (Button) findViewById(R.id.btnMyPollsAccount);
        btnMyVotesAccount = (Button) findViewById(R.id.btnMyVotesAccount);
        btnChangePasswordAccount = (Button) findViewById(R.id.btnChangePasswordAccount);
        btnLogoutAccount = (Button) findViewById(R.id.btnLogoutAccount);

        new Thread(new Runnable() {
            @Override
            public void run() {
                calculateMyPolls();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                calculateMyVotes();
            }
        }).start();

        final View viewPasswordUpdate = LayoutInflater.from(this).inflate(R.layout.layout_password_update, null);
        pbPasswordUpdate = (ProgressBar) viewPasswordUpdate.findViewById(R.id.pbPasswordUpdate);
        llResetPassword = (ScrollView) viewPasswordUpdate.findViewById(R.id.llResetPassword);
        txtPasswordNewUpdate = (EditText) viewPasswordUpdate.findViewById(R.id.txtPasswordNewUpdate);
        txtPasswordNewAgainUpdate = (EditText) viewPasswordUpdate.findViewById(R.id.txtPasswordNewAgainUpdate);

        cities = City.getCities(this).get();
        spinnerCityAccount.setAdapter(new ArrayAdapter<>(AccountActivity.this, android.R.layout.simple_spinner_dropdown_item, cities));

        dialogPasswordUpdate = new AlertDialog.Builder(AccountActivity.this)
                .setView(viewPasswordUpdate)
                .setTitle(getString(R.string.text_btnChangePassword))
                .setPositiveButton("Değiştir", null)
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();

        dialogPasswordUpdate.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                final Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (!txtPasswordNewUpdate.getText().toString().equals(txtPasswordNewAgainUpdate.getText().toString())){
                                Toast.makeText(AccountActivity.this, "Şifreler uyuşmuyor.", Toast.LENGTH_LONG).show();
                                txtPasswordNewUpdate.requestFocus();
                                return;
                            }
                            pbPasswordUpdate.setVisibility(View.VISIBLE);
                            llResetPassword.setVisibility(View.GONE);
                            currentUser.updatePassword(txtPasswordNewUpdate.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pbPasswordUpdate.setVisibility(View.GONE);
                                            llResetPassword.setVisibility(View.VISIBLE);
                                            if (task.isSuccessful()){
                                                Toast.makeText(AccountActivity.this, "Şifreniz başarıyla değiştirildi!", Toast.LENGTH_LONG).show();
                                                dialogInterface.dismiss();
                                            } else {
                                                Toast.makeText(AccountActivity.this, "Şifreniz değiştirilemedi!\r\nŞifrenizi değiştirmek için lütfen tekrar giriş yapın.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } catch (Exception ex) {
                            Log.e("dialogPasswordUpdate", ex.getMessage());
                        }
                    }
                });
            }
        });

        txtBirthDateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance(locale);
                final DatePickerDialog dialog = new DatePickerDialog(
                        AccountActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                final Calendar calendar = Calendar.getInstance(locale);
                                calendar.set(year, monthOfYear, dayOfMonth);
                                txtBirthDateAccount.setText(User.DATE_FORMAT.format(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dialog.show();
            }
        });

        btnChangePasswordAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPasswordUpdate.show();
            }
        });

        btnLogoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    auth.signOut();
                    final Intent intent = new Intent(AccountActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(AccountActivity.this, "Çıkış yapılırken bir hata meydana geldi:\r\n" + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnUpdateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        btnMyPollsAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(AccountActivity.this, PollListActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("lists", myPolls);
                startActivity(intent);
            }
        });

        btnMyVotesAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent = new Intent(AccountActivity.this, PollListActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userKey", userKey);
                intent.putExtra("anonymous", anonymous);
                intent.putExtra("lists", myVotes);
                startActivity(intent);
            }
        });

        if (anonymous){
            btnMyVotesAccount.setEnabled(false);
            btnMyPollsAccount.setEnabled(false);
            btnUpdateAccount.setEnabled(false);
            btnChangePasswordAccount.setEnabled(false);
            txtBirthDateAccount.setEnabled(false);
            spinnerCityAccount.setEnabled(false);
        }

        txtEmailAccount.setText(anonymous ? "Anonim" : user.getEmail());
        txtUserNameAccount.setText(anonymous ? "Anonim" : user.getName());
        txtBirthDateAccount.setText(anonymous ? "Anonim" : user.getBdate());
        spinnerCityAccount.setSelection(anonymous ? 0 : Entity.findIndexMatches(cities, new Function<City, Integer>() {
            @Override
            public Integer apply(City in) {
                return in.getId();
            }
        }, user.getCity()));

        guiInitialized = true;
    }

    private void updateUserInfo(){
        if (user != null){
            final String name = txtUserNameAccount.getText().toString();
            final String bdate = txtBirthDateAccount.getText().toString();
            final int city = ((City) spinnerCityAccount.getSelectedItem()).getId();

            user.setName(name);
            user.setBdate(bdate);
            user.setCity(city);

            final HashMap<String, Object> map = new HashMap<>();
            map.put(userKey, user);
            db.child("user").updateChildren(map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null){
                        Toast.makeText(AccountActivity.this, "Tercihler başarıyla güncellendi.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AccountActivity.this, "Tercihler güncellenemedi:\r\n" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        if (!guiInitialized) initializeGui();
    }

}
