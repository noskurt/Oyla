package com.ygznsl.noskurt.oyla;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.User;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity {

    private List<City> cities = Collections.synchronizedList(new LinkedList<City>());
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private final Locale locale = new Locale("tr", "TR");
    private boolean guiInitialized = false;
    private boolean gotCities = false;
    private User user = null;
    private String userKey;

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
    private View viewPasswordUpdate;
    private ProgressBar pbPasswordUpdate;
    private ScrollView llResetPassword;
    private TextInputLayout tilPasswordUpdate;
    private TextInputLayout tilPasswordNewUpdate;
    private TextInputLayout tilPasswordNewAgainUpdate;
    private EditText txtPasswordUpdate;
    private EditText txtPasswordNewUpdate;
    private EditText txtPasswordNewAgainUpdate;

    private void initializeGui(){
        currentUser = auth.getCurrentUser();
        if (currentUser == null) finish();

        txtEmailAccount = (TextView) findViewById(R.id.txtEmailAccount);
        txtUserNameAccount = (EditText) findViewById(R.id.txtUserNameAccount);
        txtBirthDateAccount = (TextView) findViewById(R.id.txtBirthDateAccount);
        spinnerCityAccount = (Spinner) findViewById(R.id.spinnerCityAccount);
        btnUpdateAccount = (Button) findViewById(R.id.btnUpdateAccount);
        btnMyPollsAccount = (Button) findViewById(R.id.btnMyPollsAccount);
        btnMyVotesAccount = (Button) findViewById(R.id.btnMyVotesAccount);
        btnChangePasswordAccount = (Button) findViewById(R.id.btnChangePasswordAccount);
        btnLogoutAccount = (Button) findViewById(R.id.btnLogoutAccount);

//        viewPasswordUpdate = LayoutInflater.from(this).inflate(R.layout.layout_password_update, null);
//        pbPasswordUpdate = (ProgressBar) viewPasswordUpdate.findViewById(R.id.pbPasswordUpdate);
//        llResetPassword = (ScrollView) viewPasswordUpdate.findViewById(R.id.llResetPassword);
//        tilPasswordUpdate = (TextInputLayout) viewPasswordUpdate.findViewById(R.id.tilPasswordUpdate);
//        tilPasswordNewUpdate = (TextInputLayout) viewPasswordUpdate.findViewById(R.id.tilPasswordNewUpdate);
//        tilPasswordNewAgainUpdate = (TextInputLayout) viewPasswordUpdate.findViewById(R.id.tilPasswordNewAgainUpdate);
//        txtPasswordUpdate = (EditText) viewPasswordUpdate.findViewById(R.id.txtPasswordUpdate);
//        txtPasswordNewUpdate = (EditText) viewPasswordUpdate.findViewById(R.id.txtPasswordNewUpdate);
//        txtPasswordNewAgainUpdate = (EditText) viewPasswordUpdate.findViewById(R.id.txtPasswordNewAgainUpdate);

        dialogPasswordUpdate = new AlertDialog.Builder(AccountActivity.this)
                .setView(viewPasswordUpdate)
                .setTitle(getString(R.string.text_btnChangePassword))
                .setPositiveButton("Değiştir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updatePassword();
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();

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
                // TODO
            }
        });

        btnMyVotesAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        if (!gotCities) getCities();

        user = (User) getIntent().getExtras().getSerializable("user");
        userKey = getIntent().getExtras().getString("userKey");

        if (user == null){
            db.child("user").orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        user = ds.getValue(User.class);
                        userKey = ds.getKey();
                        if (guiInitialized) putValues();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            if (guiInitialized) putValues();
        }

        guiInitialized = true;
    }

    private void getCities(){
        cities.clear();
        cities.addAll(City.getCities(this));
        spinnerCityAccount.setAdapter(new ArrayAdapter<>(AccountActivity.this, android.R.layout.simple_spinner_dropdown_item, cities));
        gotCities = true;
    }

    private void putValues(){
        //setTitle(String.format("%s - %s", getResources().getString(R.string.app_name), user.getName()));
        int index = 0;
        for (int i = 0; i < cities.size(); i++){
            if (cities.get(i).getId() == user.getCity()){
                index = i;
                break;
            }
        }
        txtEmailAccount.setText(user.getEmail());
        txtUserNameAccount.setText(user.getName());
        txtBirthDateAccount.setText(user.getBdate());
        spinnerCityAccount.setSelection(index);
    }

    private void updatePassword(){
        if (!txtPasswordNewUpdate.getText().toString().equals(txtPasswordNewAgainUpdate.getText().toString())){
            tilPasswordNewUpdate.setError("Şifreler uyuşmuyor.");
            tilPasswordNewAgainUpdate.setError("Şifreler uyuşmuyor.");
            txtPasswordNewUpdate.requestFocus();
            return;
        }
        tilPasswordNewUpdate.setErrorEnabled(false);
        tilPasswordNewAgainUpdate.setErrorEnabled(false);
        pbPasswordUpdate.setVisibility(View.VISIBLE);
        llResetPassword.setVisibility(View.GONE);
        currentUser.updatePassword(txtPasswordNewUpdate.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AccountActivity.this, "Şifreniz başarıyla değiştirildi!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AccountActivity.this, "Şifreniz değiştirilemedi!\r\nLütfen daha sonra tekrar deneyin:\r\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        txtPasswordNewUpdate.setText("");
                        txtPasswordNewAgainUpdate.setText("");
                        pbPasswordUpdate.setVisibility(View.GONE);
                        llResetPassword.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void updateUserInfo(){
        final String name = txtUserNameAccount.getText().toString();
        final String bdate = txtBirthDateAccount.getText().toString();
        final int city = ((City) spinnerCityAccount.getSelectedItem()).getId();

        final StringBuilder str = new StringBuilder()
                .append("Yeni isim: ").append(name).append("\r\n")
                .append("Yeni doğum tarihi: ").append(bdate).append("\r\n")
                .append("Yeni şehir: ").append(city).append("\r\n");

        Toast.makeText(this, str.toString().trim(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        if (!guiInitialized){
            initializeGui();
            putValues();
        }
    }

}
