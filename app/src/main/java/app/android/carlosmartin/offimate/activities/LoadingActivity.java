package app.android.carlosmartin.offimate.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
        if (OffiMate.currentUser == null && OffiMate.firebaseUser == null) {
            stopLoading();
            goToOnBoardActivity();
        } else if (OffiMate.currentUser != null && OffiMate.firebaseUser == null) {
            String userEmail = OffiMate.currentUser.getEmail();
            String userPassword = OffiMate.currentUser.getPassword();
            OffiMate.mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    stopLoading();
                    if (task.isSuccessful()) {
                        goToMainActivity();
                    } else {
                        goToOnBoardActivity();
                    }
                }
            });

        } else {
            stopLoading();
            goToMainActivity();
        }

    }


    private void goToOnBoardActivity() {
        intent = new Intent(LoadingActivity.this, OnBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToMainActivity() {
        intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void stopLoading() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}
