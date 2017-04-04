package com.ygznsl.noskurt.oyla;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class SplashActivity extends AppCompatActivity {

    private LinearLayout loginLayout;
    private ProgressBar progressBar;

    private Button signIn;
    private Button register;
    private Button contGuest;

    private EditText email;
    private EditText password;
    private CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loginLayout = (LinearLayout) findViewById(R.id.signInLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        signIn = (Button) findViewById(R.id.signInButton);
        register = (Button) findViewById(R.id.registerButton);
        contGuest = (Button) findViewById(R.id.continueButton);

        email = (EditText) findViewById(R.id.emailSignIn);
        password = (EditText) findViewById(R.id.passwordSignIn);
        remember = (CheckBox) findViewById(R.id.rememberMeBox);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SplashActivity.this,"Giriş",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SplashActivity.this,"Misafir Girişi",Toast.LENGTH_SHORT).show();
            }
        });


        new SharedTask().execute((Void) null);

    }

    class SharedTask extends AsyncTask <Void, Void ,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loginLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            progressBar.startAnimation(animation);
            loginLayout.startAnimation(animation);

            progressBar.setVisibility(View.INVISIBLE);
            loginLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
