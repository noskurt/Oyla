package com.ygznsl.noskurt.oyla;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {

    private LinearLayout loginLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loginLayout = (LinearLayout) findViewById(R.id.signInLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


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
