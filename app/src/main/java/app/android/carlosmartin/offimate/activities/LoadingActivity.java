package app.android.carlosmartin.offimate.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.main.MainActivity;
import app.android.carlosmartin.offimate.activities.onboard.OnBoardActivity;
import app.android.carlosmartin.offimate.application.OffiMate;

public class LoadingActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (OffiMate.currentUser == null && OffiMate.firebaseUser == null) {
                    stopLoading();
                    /*
                     * GO TO: OnBoardActivity
                     */
                    intent = new Intent(LoadingActivity.this, OnBoardActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else if (OffiMate.currentUser != null && OffiMate.firebaseUser == null) {
                    //TODO: try to login with the currentUser values
                } else {
                    stopLoading();
                    /*
                     * GO TO: MainActivity
                     */
                    intent = new Intent(LoadingActivity.this, MainActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        }, 2000);
    }

    private void stopLoading() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}
