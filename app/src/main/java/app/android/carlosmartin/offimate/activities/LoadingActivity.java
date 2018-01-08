package app.android.carlosmartin.offimate.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.main.MainActivity;
import app.android.carlosmartin.offimate.activities.onboard.OnBoardActivity;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.Office;

public class LoadingActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        this.initUI();

        //TODO: Verify if the user has verified the email
        this.validationProcess();
    }

    private void initUI() {
        setTitle("Loading...");
        this.updateLabel("Init data...");
    }

    private void validationProcess() {
        this.updateLabel("Validating data...");

        if (OffiMate.currentUser == null && OffiMate.firebaseUser == null) {
            goToOnBoardActivity();
        } else if (OffiMate.currentUser != null && OffiMate.firebaseUser == null) {
            String userEmail = OffiMate.currentUser.getEmail();
            String userPassword = OffiMate.currentUser.getPassword();
            OffiMate.mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        fetchData();
                    } else {
                        goToOnBoardActivity();
                    }
                }
            });
        } else {
            fetchData();
        }
    }

    //MARK:- Navigation Function
    private void goToOnBoardActivity() {
        stopLoading();
        intent = new Intent(LoadingActivity.this, OnBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToMainActivity() {
        stopLoading();
        intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //MARK:- UI Function
    private void updateLabel(@NonNull String message) {
        ((TextView) findViewById(R.id.loadingTextView)).setText(message);
    }

    private void stopLoading() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    //MARK:- Data Function
    private void fetchData() {
        FirebaseDatabase.getInstance().getReference("office").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                updateLabel("Fetching Offices...");

                Map<String, Object> rawMap = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> rawEntry : rawMap.entrySet()) {
                    Office office = Tools.rawToOffice(rawEntry);
                    OffiMate.offices.put(office.id, office);
                }

                FirebaseDatabase.getInstance().getReference("coworkers").orderByKey().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        updateLabel("Fetching Coworkers...");

                        Map<String, Object> rawMap = (Map<String, Object>) dataSnapshot.getValue();
                        for (Map.Entry<String, Object> rawEntry : rawMap.entrySet()) {
                            Coworker coworker = Tools.rawToCoworker(rawEntry);
                            OffiMate.coworkers.put(coworker.uid, coworker);
                        }

                        goToMainActivity();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //TODO: handler cancelled fetching coworkers
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: handler cancelled fetching office
            }
        });
    }
}