package app.android.carlosmartin.offimate.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.main.MainActivity;
import app.android.carlosmartin.offimate.activities.onboard.OnBoardActivity;
import app.android.carlosmartin.offimate.application.OffiMate;
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
        try {
            validationProcess();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Loading...");
        this.updateLabel("...");
    }

    private void validationProcess() throws InterruptedException {
        this.updateLabel("Signing in...");

        if (OffiMate.currentUser == null && OffiMate.firebaseUser == null) {
            goToOnBoardActivity();
        } else if (OffiMate.currentUser != null && OffiMate.firebaseUser == null) {
            String userEmail = OffiMate.currentUser.getEmail();
            String userPassword = OffiMate.currentUser.getPassword();
            OffiMate.mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //TODO: connect properly to async func
                        try {
                            initFetching();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        goToOnBoardActivity();
                    }
                }
            });
        } else {
            //TODO: connect properly to async func
            initFetching();
        }
    }

    private void initFetching() throws InterruptedException {
        CountDownLatch startOfficeSignal   = new CountDownLatch(1);
        CountDownLatch startCoworkerSignal = new CountDownLatch(1);
        CountDownLatch doneOfficeSignal    = new CountDownLatch(1);
        CountDownLatch doneCoworkerSignal  = new CountDownLatch(1);
        new Thread(new WorkerOffice(startOfficeSignal, doneOfficeSignal)).start();
        new Thread(new WorkerCoworker(startCoworkerSignal, doneCoworkerSignal)).start();

        updateLabel("Fetching Offices...");
        startOfficeSignal.countDown();
        doneOfficeSignal.await();

        updateLabel("Fetching Coworkers...");
        startCoworkerSignal.countDown();
        doneCoworkerSignal.await();

        stopLoading();
        goToMainActivity();
    }

    private void fetchOffice() {
        FirebaseDatabase.getInstance().getReference("office").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> rawMap = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> rawEntry : rawMap.entrySet()) {
                    Map<String, String> officeMap = (Map<String, String>) rawEntry.getValue();
                    String id   = rawEntry.getKey();
                    String name = officeMap.get("name");
                    OffiMate.offices.put(id, new Office(id, name));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void fetchCoworker() {
        FirebaseDatabase.getInstance().getReference("coworkers").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> rawMap = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> rawEntry : rawMap.entrySet()) {
                    Map<String, String> coworkerMap = (Map<String, String>) rawEntry.getValue();

                    String id       = rawEntry.getKey();
                    String email    = coworkerMap.get("email");
                    String name     = coworkerMap.get("name");
                    String userId   = coworkerMap.get("userId");
                    String officeId = coworkerMap.get("officeId");
                    Office office   = (Office) OffiMate.offices.get(officeId);

                    OffiMate.coworkers.put(id, new Coworker(id, userId, email, name, office));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void updateLabel(@NonNull String message) {
        ((TextView) findViewById(R.id.loadingTextView)).setText(message);
    }

    private void goToOnBoardActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopLoading();
                intent = new Intent(LoadingActivity.this, OnBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }

    private void goToMainActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopLoading();
                intent = new Intent(LoadingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }

    private void stopLoading() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    class WorkerOffice implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;

        WorkerOffice(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
        }
        public void run() {
            try {
                startSignal.await();
                doWork();
                doneSignal.countDown();
            } catch (InterruptedException ex) {} // return;
        }

        void doWork() {
            fetchOffice();
        }
    }

    class WorkerCoworker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        WorkerCoworker(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
        }
        public void run() {
            try {
                startSignal.await();
                doWork();
                doneSignal.countDown();
            } catch (InterruptedException ex) {} // return;
        }

        void doWork() {
            fetchCoworker();
        }
    }
}

