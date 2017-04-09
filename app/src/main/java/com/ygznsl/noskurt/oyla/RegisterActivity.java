package com.ygznsl.noskurt.oyla;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputLayout pwLayout;
    private TextInputLayout pwAgainLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout surnameLayout;
    private TextInputLayout dateLayout;

    private EditText email;
    private EditText password;
    private EditText passwordAgain;

    private Button cancel;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailLayout = (TextInputLayout) findViewById(R.id.emailRegisterLayout);
        pwLayout = (TextInputLayout) findViewById(R.id.passwordRegisterLayout);
        pwAgainLayout = (TextInputLayout) findViewById(R.id.passwordAgainRegisterLayout);

        email = (EditText) findViewById(R.id.emailRegister);
        password = (EditText) findViewById(R.id.passwordRegister);
        passwordAgain = (EditText) findViewById(R.id.passwordAgainRegister);

        cancel = (Button) findViewById(R.id.cancelButton);
        register = (Button) findViewById(R.id.registerButtonRegister);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(email.getText().toString(), password.getText().toString());
            }
        });

    }

    private void register(final String email, final String pw) {
        if (!validateEmail(email) || !validatePassword(pw)) return;
        if (!password.getText().toString().equals(passwordAgain.getText().toString())) {
            pwLayout.setError("Şifreler uyuşmuyor!");
            pwAgainLayout.setError("Şifreler uyuşmuyor!");
            return;
        }
        final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "Kayıt", "Kayıt yapılıyor...", true);
        /*SplashActivity.auth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Kayıt YAPILAMADI!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Kayıt BAŞARILI!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });*/
    }

    private boolean validateEmail(String email) {
        email = email.trim();

        boolean isValidEmail = !TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (email.isEmpty() || !isValidEmail) {
            emailLayout.setError("Geçerli E-Mail adresi giriniz!");
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword(String pw) {
        if (pw.trim().isEmpty()) {
            pwLayout.setError("Geçerli şifre giriniz!");
            return false;
        } else if (pw.length() < 6) {
            pwLayout.setError("En az 6 karakter içermelidir!");
            return false;
        } else {
            pwLayout.setErrorEnabled(false);
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}