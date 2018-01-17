package app.android.carlosmartin.offimate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import app.android.carlosmartin.offimate.user.CurrentUser;

public class LoadingActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        this.initUI();
        this.start();
    }

    private void initUI() {
        setTitle("Loading...");
        this.updateLabel("Init data...");
    }

    private void start() {
        this.updateLabel("Validating data...");
        int userStatus = this.getUserStatus();
        switch (userStatus) {
            case 0:
                this.checkingEmailStatus();
                break;
            case 1:
                this.tryToConnect();
                break;
            default:
                this.goToOnBoardActivity();
                break;
        }
    }

    private int getUserStatus() {
        /**
         │ firebaseUser │ currentUser  │ status  │
         ├──────────────┼──────────────┼─────────┤
         │    !null     │    !null     │    0    │
         ├──────────────┼──────────────┼─────────┤
         │     null     │    !null     │    1    │
         ├──────────────┼──────────────┼─────────┤
         │    !null     │     null     │    2    │
         ├──────────────┼──────────────┼─────────┤
         │     null     │     null     │    3    │
         └──────────────┴──────────────┴─────────┘
         */
        int status = 0;

        if (OffiMate.firebaseUser == null)
            status += 1;

        if (OffiMate.currentUser == null)
            status += 2;

        return status;
    }

    private void tryToConnect() {
        String userMail = OffiMate.currentUser.getEmail();
        String userPass = OffiMate.currentUser.getPassword();
        OffiMate.mAuth.signInWithEmailAndPassword(userMail, userPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkingEmailStatus();
                } else {
                    goToOnBoardActivity();
                }
            }
        });
    }

    private void checkingEmailStatus() {
        if (OffiMate.firebaseUser.isEmailVerified()) {
            this.fetchData();
        } else {
            String title = "Email Not Verified";
            String message = "Your email has not yet been verified. ";
            message += "Do you want us to send another verification email to ";
            message += OffiMate.firebaseUser.getEmail() + "?";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //YES button pressed
                    OffiMate.firebaseUser.sendEmailVerification();
                    goToOnBoardActivity();
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //NO button pressed
                    goToOnBoardActivity();
                }
            })
            .show();
        }
    }

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
                            if (OffiMate.currentUser.getUid().equals(coworker.uid)) {
                                OffiMate.coworkerId = coworker.id;
                            }
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

}