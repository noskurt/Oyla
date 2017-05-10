package com.ygznsl.noskurt.oyla;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean openingTaskExecuted = false;

    private ProgressBar pbSignIn;
    private EditText txtEmailSignIn;
    private EditText txtPasswordSignIn;
    private LinearLayout signInLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout pwLayout;

    private void openingTask() {
        final AsyncTask<FirebaseAuth, Integer, FirebaseUser> task = new AsyncTask<FirebaseAuth, Integer, FirebaseUser>() {
            private FirebaseUser user = null;

            @Override
            protected FirebaseUser doInBackground(FirebaseAuth... firebaseAuths) {
                user = auth.getCurrentUser();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    Log.e("task.doInBackground", ex.getMessage());
                }
                if (user != null) logIn();
                openingTaskExecuted = true;
                return user;
            }

            @Override
            protected void onPostExecute(FirebaseUser firebaseUser) {
                if (user == null) hideProgressBar();
            }
        };
        task.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!isOnline())
            Toast.makeText(SplashActivity.this, "İnternet erişimi gerekmektedir!", Toast.LENGTH_LONG).show();


        final Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        final Button btnRegister = (Button) findViewById(R.id.btnRegister);
        final TextView btnSignInAnonymously = (TextView) findViewById(R.id.btnSignInAnonymously);

        pbSignIn = (ProgressBar) findViewById(R.id.pbSignIn);
        signInLayout = (LinearLayout) findViewById(R.id.signInLayout);

        txtEmailSignIn = (EditText) findViewById(R.id.txtEmailSignIn);
        txtPasswordSignIn = (EditText) findViewById(R.id.txtPasswordSignIn);

        emailLayout = (TextInputLayout) findViewById(R.id.emailSignInLayout);
        pwLayout = (TextInputLayout) findViewById(R.id.passwordSignInLayout);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = txtEmailSignIn.getText().toString();
                final String password = txtPasswordSignIn.getText().toString();
                if (!validateEmail(email) || !validatePassword(password)) return;
                new AsyncTask<String, Integer, Boolean>() {
                    @Override
                    protected void onPreExecute() {
                        showProgressBar();
                    }

                    @Override
                    protected Boolean doInBackground(String... strings) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Log.e("signIn.doInBackground", ex.getMessage());
                        }
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    logIn();
                                } else {
                                    final Class c = task.getException().getClass();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (c.equals(FirebaseAuthInvalidUserException.class)) {
                                                Toast.makeText(SplashActivity.this, "Böyle bir kullanıcı mevcut değil.\r\nGiriş yapabilmek için lütfen kaydolun.", Toast.LENGTH_LONG).show();
                                                emailLayout.setError("Böyle bir kullanıcı mevcut değil");
                                                txtEmailSignIn.requestFocus();
                                            } else if (c.equals(FirebaseAuthInvalidCredentialsException.class)) {
                                                Toast.makeText(SplashActivity.this, "Hatalı şifre girdiniz.", Toast.LENGTH_LONG).show();
                                                pwLayout.setError("Hatalı şifre girdiniz.");
                                                txtPasswordSignIn.requestFocus();
                                            } else {
                                                Toast.makeText(SplashActivity.this, "Giriş başarısız oldu.", Toast.LENGTH_LONG).show();
                                                emailLayout.setErrorEnabled(false);
                                                pwLayout.setErrorEnabled(false);
                                            }
                                            hideProgressBar();
                                        }
                                    });
                                }
                            }
                        });
                        return true;
                    }
                }.execute();
            }
        });

        btnSignInAnonymously.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        } catch (InterruptedException ex) {
                            Log.e("signInAn.doInBackground", ex.getMessage());
                        }
                        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    if (task.getResult().getUser() != null) logIn();
                                } catch (Exception ex) {
                                    Toast.makeText(SplashActivity.this, "Giriş başarısız oldu.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        return true;
                    }
                }.execute();
            }
        });

        if (!openingTaskExecuted) openingTask();
    }

    private boolean validateEmail(String email) {
        boolean isValidEmail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValidEmail) {
            txtEmailSignIn.setError("Geçerli bir e-posta adresi giriniz!");
            txtEmailSignIn.requestFocus();
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
        pwLayout.setErrorEnabled(false);
        return true;
    }

    private void showProgressBar() {
        if (pbSignIn == null || signInLayout == null) return;
        pbSignIn.setVisibility(View.VISIBLE);
        signInLayout.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        if (pbSignIn == null || signInLayout == null) return;
        pbSignIn.setVisibility(View.GONE);
        signInLayout.setVisibility(View.VISIBLE);
    }

    private void logIn() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}