package com.ygznsl.noskurt.oyla;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
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
import com.ygznsl.noskurt.oyla.helper.Consumer;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;

public class SplashActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private boolean openingTaskExecuted = false;
    private boolean guiInitialized = false;

    private ProgressBar pbSignIn;
    private EditText txtEmailSignIn;
    private EditText txtPasswordSignIn;
    private LinearLayout signInLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout pwLayout;

    private void initializeGui() {
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
                            Thread.sleep(1000L);
                        } catch (InterruptedException ex) {
                            Log.e("signIn.doInBackground", ex.getMessage());
                        }
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                    final SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("anonymous", false);
                                    editor.apply();
                                    logIn(false);
                                } else {
                                    final Nullable<Exception> exception = new Nullable<>(task.getException());
                                    exception.operate(new Consumer<Exception>() {
                                        @Override
                                        public void accept(Exception in) {
                                            final Class c = in.getClass();
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
                                    });
                                    if (!exception.hasValue()){
                                        Toast.makeText(SplashActivity.this, "Giriş başarısız oldu.", Toast.LENGTH_LONG).show();
                                        emailLayout.setErrorEnabled(false);
                                        pwLayout.setErrorEnabled(false);
                                        hideProgressBar();
                                    }
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
                    protected Boolean doInBackground(String... strings) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException ex) {
                            Log.e("signInAn.doInBackground", ex.getMessage());
                        }
                        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                    final SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("anonymous", true);
                                    editor.apply();
                                    logIn(true);
                                } else {
                                    Toast.makeText(SplashActivity.this,
                                            ("Giriş başarısız oldu: \r\n" + new Nullable<>(task.getException()).orElse(new Function<Exception, String>() {
                                                @Override
                                                public String apply(Exception in) {
                                                    return in.getMessage();
                                                }
                                            }, "")).trim(),
                                            Toast.LENGTH_LONG).show();
                                }
                                hideProgressBar();
                            }
                        });
                        return true;
                    }
                }.execute();
            }
        });

        guiInitialized = true;
        if (!openingTaskExecuted) openingTask();
    }

    private void openingTask() {
        final boolean internetStatus = isOnline();
        if (!internetStatus) {
            final AlertDialog dialog = new AlertDialog.Builder(SplashActivity.this)
                    .setTitle("İnternet bağlantınızı kontrol edin")
                    .setMessage("Uygulamanın çalışabilmesi için internet bağlantısı gerekmektedir.\r\n" +
                            "Lütfen internet bağlantınızı kontrol edip uygulamayı tekrar çalıştırın.")
                    .setPositiveButton("Anladım", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            SplashActivity.this.finish();
                        }
                    })
                    .setNeutralButton("İnternet Ayarları", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .create();
            dialog.show();
        }
        final AsyncTask<FirebaseAuth, Integer, FirebaseUser> task = new AsyncTask<FirebaseAuth, Integer, FirebaseUser>() {
            private FirebaseUser user = null;

            @Override
            protected FirebaseUser doInBackground(FirebaseAuth... firebaseAuths) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    Log.e("task.doInBackground", ex.getMessage());
                }
                user = auth.getCurrentUser();
                if (user != null) {
                    final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    final boolean anonymous = sharedPref.getBoolean("anonymous", true);
                    logIn(anonymous);
                }
                openingTaskExecuted = true;
                return user;
            }

            @Override
            protected void onPostExecute(FirebaseUser firebaseUser) {
                if (user == null) hideProgressBar();
            }
        };
        if (internetStatus) task.execute();
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

    private void logIn(boolean anonymous) {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("anonymous", anonymous);
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (!guiInitialized) initializeGui();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}