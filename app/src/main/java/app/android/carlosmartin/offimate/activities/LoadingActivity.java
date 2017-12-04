package app.android.carlosmartin.offimate.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.onboard.OnBoardActivity;
import app.android.carlosmartin.offimate.application.OffiMate;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: Move it to "will appears"
                if (OffiMate.currentUser == null) {
                    stopLoading();

                    Intent intentToOnBoard = new Intent(LoadingActivity.this,
                            OnBoardActivity.class);

                    intentToOnBoard.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intentToOnBoard);
                } else {
                    Toast.makeText(LoadingActivity.this,
                            "Current User was created successfully!",
                            Toast.LENGTH_LONG).show();
                }

            }
        }, 2000);

        /*
        if (!CurrentUser.isInit()) {
            this.stopLoading();

            Intent intentToOnBoard = new Intent(LoadingActivity.this,
                    OnBoardActivity.class);

            intentToOnBoard.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intentToOnBoard);
        }
        */
    }

    private void stopLoading() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}
