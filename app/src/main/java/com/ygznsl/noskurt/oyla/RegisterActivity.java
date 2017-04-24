package com.ygznsl.noskurt.oyla;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.ygznsl.noskurt.oyla.entity.City;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    // Yağız

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final List<City> cities = Collections.synchronizedList(new LinkedList<City>());
    private final List<String> userNames = Collections.synchronizedList(new LinkedList<String>());
    private final Locale locale = new Locale("tr", "TR");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
    private boolean gotCities = false;

    private ProgressBar pbRegister;
    private LinearLayout registerLayout;
    private RadioGroup radioGrpRegister;
    private Spinner spinnerCityRegister;

    // Yağız

    private TextInputLayout emailLayout;
    private TextInputLayout pwLayout;
    private TextInputLayout pwAgainLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout dateLayout;

    private EditText txtEmailRegister;
    private EditText txtPasswordRegister;
    private EditText txtPasswordAgainRegister;
    private EditText txtBirthDateRegister;
    private EditText txtNameRegister;

    public RegisterActivity(){
        db.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userNames.add(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("constructor.firebase", databaseError.getMessage());
            }
        });
    }

    private boolean getCities() throws ExecutionException, InterruptedException {
        return new AsyncTask<InputStream, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                showProgressBar();
            }

            @Override
            protected void onPostExecute(Boolean s) {
                hideProgressBar();
                spinnerCityRegister.setAdapter(new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, cities));
            }

            @Override
            protected Boolean doInBackground(InputStream... ınputStreams) {
                try (InputStreamReader isr = new InputStreamReader(getAssets().open("state.json"), Charset.forName("utf-8"))){
                    try (JsonReader reader = new JsonReader(isr)){
                        final City[] array = new Gson().fromJson(reader, City[].class);
                        cities.clear();
                        cities.addAll(Arrays.asList(array));
                        return (gotCities = true);
                    }
                } catch (IOException ex) {
                    Log.e("getCities.task", ex.getMessage());
                    return false;
                }
            }
        }.execute().get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pbRegister = (ProgressBar) findViewById(R.id.pbRegister);
        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);
        radioGrpRegister = (RadioGroup) findViewById(R.id.radioGrpRegister);
        spinnerCityRegister = (Spinner) findViewById(R.id.spinnerCityRegister);

        emailLayout = (TextInputLayout) findViewById(R.id.emailRegisterLayout);
        pwLayout = (TextInputLayout) findViewById(R.id.passwordRegisterLayout);
        pwAgainLayout = (TextInputLayout) findViewById(R.id.passwordAgainRegisterLayout);
        nameLayout = (TextInputLayout) findViewById(R.id.nameRegisterLayout);
        dateLayout = (TextInputLayout) findViewById(R.id.dateRegisterLayout);

        txtEmailRegister = (EditText) findViewById(R.id.txtEmailRegister);
        txtPasswordRegister = (EditText) findViewById(R.id.txtPasswordRegister);
        txtPasswordAgainRegister = (EditText) findViewById(R.id.txtPasswordAgainRegister);
        txtBirthDateRegister = (EditText) findViewById(R.id.txtBirthDateRegister);
        txtNameRegister = (EditText) findViewById(R.id.txtNameRegister);

        final Button btnCancelRegister = (Button) findViewById(R.id.btnCancelRegister);
        final Button btnRegisterRegister = (Button) findViewById(R.id.btnRegisterRegister);

        btnCancelRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegisterRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                final Calendar calendar = Calendar.getInstance(locale);
                calendar.set(year, monthOfYear, dayOfMonth);
                txtBirthDateRegister.setText(sdf.format(calendar.getTime()));
            }
        };

        txtBirthDateRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance(locale);
                final DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        listener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dialog.show();
            }
        });

        if (!gotCities) {
            try {
                if (!getCities()){
                    getCities();
                }
            } catch (InterruptedException | ExecutionException ex) {
                Log.e("onCreate", ex.getMessage());
            }
        }
    }

    private void showProgressBar(){
        if (pbRegister == null || registerLayout == null) return;
        pbRegister.setVisibility(View.VISIBLE);
        registerLayout.setVisibility(View.GONE);
    }

    private void hideProgressBar(){
        if (pbRegister == null || registerLayout == null) return;
        pbRegister.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
    }

    private void register() {
        final String email = txtEmailRegister.getText().toString();
        final String password = txtPasswordRegister.getText().toString();
        if (!validateEmail(email) || !validatePassword(password)) return;
        if (!password.equals(txtPasswordAgainRegister.getText().toString())) {
            pwLayout.setError("Şifreler uyuşmuyor!");
            pwAgainLayout.setError("Şifreler uyuşmuyor!");
            pwLayout.requestFocus();
            return;
        }
        pwLayout.setErrorEnabled(false);
        pwAgainLayout.setErrorEnabled(false);
        final String username = txtNameRegister.getText().toString();
        if (userNames.contains(username)){
            nameLayout.setError("Bu ada sahip bir kullanıcı zaten bulunuyor.");
            nameLayout.requestFocus();
            return;
        }
        nameLayout.setErrorEnabled(false);
        final String birthDateStr = txtBirthDateRegister.getText().toString();
        if (birthDateStr.isEmpty()){
            dateLayout.setError("Doğum tarihinizi girmelisiniz.");
            dateLayout.requestFocus();
            return;
        }
        dateLayout.setErrorEnabled(false);
        final int radioBtnId = radioGrpRegister.getCheckedRadioButtonId();
        if (radioBtnId == -1){
            Toast.makeText(this, "Cinsiyet kısmını boş bırakamazsınız.", Toast.LENGTH_SHORT).show();
            return;
        }
        final Object city = spinnerCityRegister.getSelectedItem();
        if (city == null){
            Toast.makeText(this, "Yaşadığınız şehri seçmelisiniz.", Toast.LENGTH_SHORT).show();
            spinnerCityRegister.requestFocus();
            return;
        }

        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                showProgressBar();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                hideProgressBar();
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                try {
                    Thread.sleep(1000);
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.getResult().getUser() != null){
                                        final DatabaseReference pushed = db.child("user").push();

                                        pushed.child("birthDate").setValue(birthDateStr);
                                        pushed.child("email").setValue(email);
                                        pushed.child("name").setValue(username);
                                        pushed.child("gender").setValue(
                                                radioBtnId == R.id.btnRadioMaleRegister ? "E" : "K"
                                        );
                                        pushed.child("city").setValue(((City)city).getId());

                                        auth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        logIn();
                                                    }
                                                });
                                    }
                                }
                            });
                    return true;
                } catch (Exception ex) {
                    Log.e("register.doInBackground", ex.getMessage());
                    return false;
                }
            }
        }.execute();
    }

    private boolean validateEmail(String email) {
        boolean isValidEmail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValidEmail) {
            emailLayout.setError("Geçerli bir e-posta adresi giriniz!");
            emailLayout.requestFocus();
            return false;
        }
        emailLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.trim().isEmpty()) {
            pwLayout.setError("Geçerli bir şifre giriniz!");
            return false;
        }
        if (password.length() < 6) {
            pwLayout.setError("Şifreniz en az 6 karakter içermelidir!");
            return false;
        }
        pwLayout.setErrorEnabled(false);
        return true;
    }

    private void logIn(){
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}