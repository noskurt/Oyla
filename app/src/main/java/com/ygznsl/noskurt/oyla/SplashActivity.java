package com.ygznsl.noskurt.oyla;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.ygznsl.noskurt.oyla.collection.UserCollection;

public class SplashActivity extends AppCompatActivity {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private UserCollection users;

    private Button btnSignIn;
    private Button btnRegister;
    private ProgressBar pbSignIn;
    private TextView btnSignInAnonymously;
    private EditText txtEmailSignIn;
    private EditText txtPasswordSignIn;
    private LinearLayout signInLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout pwLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db.setPersistenceEnabled(true);
        db.getReference().keepSynced(true);
        db.getReference("user").keepSynced(true);

        pbSignIn = (ProgressBar) findViewById(R.id.pbSignIn);
        signInLayout = (LinearLayout) findViewById(R.id.signInLayout);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSignInAnonymously = (TextView) findViewById(R.id.btnSignInAnonymously);

        txtEmailSignIn = (EditText) findViewById(R.id.txtEmailSignIn);
        txtPasswordSignIn = (EditText) findViewById(R.id.txtPasswordSignIn);

        emailLayout = (TextInputLayout) findViewById(R.id.emailSignInLayout);
        pwLayout = (TextInputLayout) findViewById(R.id.passwordSignInLayout);

        final AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SplashActivity.this.users = new UserCollection(db.getReference().child("user").getRef());
                SplashActivity.this.users.run();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                showSignInLayout();
            }
        };
        task.execute();
    }

    private boolean validateEmail(String email) {
        boolean isValidEmail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValidEmail) {
            txtEmailSignIn.setError("Geçerli bir e-posta adresi giriniz!");
            txtEmailSignIn.requestFocus();
        } else {
            emailLayout.setErrorEnabled(false);
        }
        return isValidEmail;
    }

    private boolean validatePassword(String password) {
        if (password.trim().isEmpty()) {
            pwLayout.setError("Geçerli bir şifre giriniz!");
            return false;
        }
        pwLayout.setErrorEnabled(false);
        return true;
    }

    private void showSignInLayout(){
        pbSignIn.setVisibility(View.GONE);
        signInLayout.setVisibility(View.VISIBLE);
    }

}