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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private final Locale locale = new Locale("tr", "TR");
    private boolean gotCities = false;
    private User user = null;
    private String userKey;

    private ScrollView mainLayoutAccount;
    private TextView txtEmailAccount;
    private TextView txtBirthDateAccount;
    private Spinner spinnerCityAccount;
    private Button btnUpdateAccount;
    private Button btnMyPollsAccount;
    private Button btnMyVotesAccount;
    private Button btnChangePasswordAccount;
    private Button btnLogoutAccount;
    private TextInputLayout tilPasswordNewAccount;
    private TextInputLayout tilPasswordNewAgainAccount;
    private EditText txtPasswordNewAccount;
    private EditText txtPasswordNewAgainAccount;
    private ProgressBar pbUpdatePasswordAccount;

    private void getCities(){
        cities.clear();
        cities.addAll(City.getCities(this));
        spinnerCityAccount.setAdapter(new ArrayAdapter<>(AccountActivity.this, android.R.layout.simple_spinner_dropdown_item, cities));
        gotCities = true;
    }

    private void putValues(){
        setTitle(String.format("%s - %s", getResources().getString(R.string.app_name), user.getName()));
        int index = 0;
        for (int i = 0; i < cities.size(); i++){
            if (cities.get(i).getId() == user.getCity()){
                index = i;
                break;
            }
        }
        txtEmailAccount.setText(user.getEmail());
        txtBirthDateAccount.setText(user.getBdate());
        spinnerCityAccount.setSelection(index);
    }

    private void updatePassword(){
        if (!txtPasswordNewAccount.getText().toString().equals(txtPasswordNewAgainAccount.getText().toString())){
            tilPasswordNewAccount.setError("Şifreler uyuşmuyor.");
            tilPasswordNewAgainAccount.setError("Şifreler uyuşmuyor.");
            txtPasswordNewAccount.requestFocus();
            return;
        }
        tilPasswordNewAccount.setErrorEnabled(false);
        tilPasswordNewAgainAccount.setErrorEnabled(false);
        pbUpdatePasswordAccount.setVisibility(View.VISIBLE);
        mainLayoutAccount.setVisibility(View.GONE);
        auth.getCurrentUser().updatePassword(txtPasswordNewAccount.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AccountActivity.this, "Şifreniz başarıyla değiştirildi!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AccountActivity.this, "Şifreniz değiştirilemedi!\r\nLütfen daha sonra tekrar deneyin:\r\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        txtPasswordNewAccount.setText("");
                        txtPasswordNewAgainAccount.setText("");
                        pbUpdatePasswordAccount.setVisibility(View.GONE);
                        mainLayoutAccount.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mainLayoutAccount = (ScrollView) findViewById(R.id.mainLayoutAccount);
        txtEmailAccount = (TextView) findViewById(R.id.txtEmailAccount);
        txtBirthDateAccount = (TextView) findViewById(R.id.txtBirthDateAccount);
        spinnerCityAccount = (Spinner) findViewById(R.id.spinnerCityAccount);
        btnUpdateAccount = (Button) findViewById(R.id.btnUpdateAccount);
        btnMyPollsAccount = (Button) findViewById(R.id.btnMyPollsAccount);
        btnMyVotesAccount = (Button) findViewById(R.id.btnMyVotesAccount);
        btnChangePasswordAccount = (Button) findViewById(R.id.btnChangePasswordAccount);
        btnLogoutAccount = (Button) findViewById(R.id.btnLogoutAccount);

        tilPasswordNewAccount = (TextInputLayout) findViewById(R.id.tilPasswordNewAccount);
        tilPasswordNewAgainAccount = (TextInputLayout) findViewById(R.id.tilPasswordNewAgainAccount);
        txtPasswordNewAccount = (EditText) findViewById(R.id.txtPasswordNewAccount);
        txtPasswordNewAgainAccount = (EditText) findViewById(R.id.txtPasswordNewAgainAccount);
        pbUpdatePasswordAccount = (ProgressBar) findViewById(R.id.pbUpdatePasswordAccount);

        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                final Calendar calendar = Calendar.getInstance(locale);
                calendar.set(year, monthOfYear, dayOfMonth);
                txtBirthDateAccount.setText(User.DATE_FORMAT.format(calendar.getTime()));
            }
        };

        txtBirthDateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance(locale);
                final DatePickerDialog dialog = new DatePickerDialog(
                        AccountActivity.this,
                        listener,
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
                updatePassword();
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

        if (!gotCities) getCities();

        user = (User) getIntent().getExtras().getSerializable("user");
        userKey = getIntent().getExtras().getString("userKey");

        if (user == null){
            db.child("user").orderByChild("email").equalTo(auth.getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        user = ds.getValue(User.class);
                        userKey = ds.getKey();
                        putValues();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            putValues();
        }
    }

}
