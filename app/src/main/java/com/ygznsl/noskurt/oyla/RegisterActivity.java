package com.ygznsl.noskurt.oyla;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final List<String> userNames = Collections.synchronizedList(new LinkedList<String>());
    private final Locale locale = new Locale("tr", "TR");
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
    private List<City> cities;
    private boolean guiInitialized = false;
    private int userMaxId = 0;

    private ProgressBar pbRegister;
    private LinearLayout registerLayout;
    private RadioGroup radioGrpRegister;
    private Spinner spinnerCityRegister;

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

    private void initializeGui(){
        Entity.getDatabase().getReference().child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final int id = dataSnapshot.child("id").getValue(Integer.class);
                if (id > userMaxId) userMaxId = id;
                userNames.add(dataSnapshot.child("name").getValue(String.class));
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

        cities = City.getCities(this).get();

        pbRegister = (ProgressBar) findViewById(R.id.pbRegister);
        registerLayout = (LinearLayout) findViewById(R.id.registerLayout);
        radioGrpRegister = (RadioGroup) findViewById(R.id.radioGrpRegister);
        spinnerCityRegister = (Spinner) findViewById(R.id.spinnerCityRegister);

        spinnerCityRegister.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cities));

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
                final Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
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

        guiInitialized = true;
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
        if (username.isEmpty()){
            nameLayout.setError("Geçerli bir kullanıcı adı girin.");
            nameLayout.requestFocus();
            return;
        }
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
        final City city = (City) spinnerCityRegister.getSelectedItem();
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
            protected Boolean doInBackground(String... strings) {
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        final User user = new User();
                                        user.setId(++userMaxId);
                                        user.setEmail(email);
                                        user.setName(username);
                                        user.setBdate(birthDateStr);
                                        user.setCity(city.getId());
                                        user.setGender(radioBtnId == R.id.btnRadioMaleRegister ? "E" : "K");

                                        final DatabaseReference pushed = Entity.getDatabase().getReference().child("user").push();
                                        pushed.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
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
                                    } else {
                                        Toast.makeText(RegisterActivity.this,
                                                ("Kullanıcı oluşturulurken hata meydana geldi:\r\n" +
                                                        new Nullable<>(task.getException()).orElse(new Function<Exception, String>() {
                                                            @Override
                                                            public String apply(Exception in) {
                                                                return in.getMessage();
                                                            }
                                                        }, "")).trim(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    return true;
                } catch (Exception ex) {
                    Log.e("register.doInBackground", ex.getMessage());
                    hideProgressBar();
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
        intent.putExtra("anonymous", false);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MyApplication.setIconBar(this);
        setTitle(" Kayıt Ol");
        if (!guiInitialized) initializeGui();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

}