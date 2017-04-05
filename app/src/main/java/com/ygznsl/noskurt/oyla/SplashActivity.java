package com.ygznsl.noskurt.oyla;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private Button signIn;
    private Button register;
    private Button contGuest;

    private EditText email;
    private EditText password;

    public static FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) auth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        signIn = (Button) findViewById(R.id.signInButton);
        register = (Button) findViewById(R.id.registerButton);
        contGuest = (Button) findViewById(R.id.continueButton);

        email = (EditText) findViewById(R.id.emailSignIn);
        password = (EditText) findViewById(R.id.passwordSignIn);

        auth = FirebaseAuth.getInstance();

        checkSigned();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(), password.getText().toString());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        contGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SplashActivity.this, "Misafir Girişi", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void signIn(String email, String pw) {
        final ProgressDialog progressDialog = ProgressDialog.show(SplashActivity.this, "Giriş", "Giriş yapılıyor...", true);
        auth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(SplashActivity.this, "Giriş Hatalı!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkSigned() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(SplashActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SplashActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

}