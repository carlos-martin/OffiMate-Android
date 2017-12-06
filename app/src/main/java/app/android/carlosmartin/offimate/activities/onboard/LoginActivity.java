package app.android.carlosmartin.offimate.activities.onboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Set;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.LoadingActivity;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.Office;
import app.android.carlosmartin.offimate.user.CurrentUser;
import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference coworkerRef;
    private DatabaseReference officeRef;

    //DataSource
    private String userName;
    private String userEmail;
    private String userPassword;
    private Office userOffice;
    private String userId;

    //UI
    private TextView textViewEmail;
    private TextView textViewPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private MenuItem barMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initFirebase();
        this.initUI();
    }

    private void initFirebase() {
        this.mAuth =        FirebaseAuth.getInstance();
        this.database =     FirebaseDatabase.getInstance();
        this.coworkerRef =  this.database.getReference("coworkers");
        this.officeRef =    this.database.getReference("office");
    }

    private void initUI() {
        setTitle("Login");

        this.initLoadingView();

        this.textViewEmail = findViewById(R.id.textViewUserEmail);
        this.textViewEmail.setText("E-MAIL");

        this.textViewPassword = findViewById(R.id.textViewUserPassword);
        this.textViewPassword.setText("PASSWORD");

        this.editTextEmail = findViewById(R.id.editTextUserEmail);
        this.editTextEmail.setHint("Enter your email...");
        this.editTextEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            userEmail = editTextEmail.getText().toString();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        this.editTextPassword = findViewById(R.id.editTextUserPassword);
        this.editTextPassword.setHint("Enter your password...");
        this.editTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            userPassword = editTextPassword.getText().toString();
                            loginAction();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        //TODO: Remove it
        this.userName = "Anonymous User";
        this.userOffice = new Office("-1", "Anonymous Office");
    }

    private void loginAction() {
        int error_counter = 0;

        this.userEmail = this.editTextEmail.getText().toString();
        if (this.userEmail == null || this.userEmail.isEmpty()) {
            String error_message = "Email field cannot be empty";
            Toast.makeText(LoginActivity.this, error_message, Toast.LENGTH_SHORT).show();
            error_counter++;
        } else {
            if (Tools.isValidEmail(this.userEmail)) {
                Log.d("LOGIN","User email: " + this.userEmail);
            } else {
                error_counter++;
                this.userEmail = null;

                String error_message = "Your email must have one of this two domains: sigma.se or " +
                        "sigmatechnology.se";

                Toast.makeText(LoginActivity.this,
                        error_message , Toast.LENGTH_LONG).show();
            }
        }

        this.userPassword = this.editTextPassword.getText().toString();
        if (this.userPassword == null || this.userPassword.isEmpty()) {
            String error_message = "Password field cannot be empty";
            Toast.makeText(LoginActivity.this, error_message, Toast.LENGTH_SHORT).show();
            error_counter++;
        } else {
            if (Tools.isValidPassword(this.userPassword)) {
                Log.d("LOGIN","User password: " + this.userPassword);
            } else {
                error_counter++;
                this.userPassword = null;

                String error_message = "The password must be a minimum of 8 characters and must " +
                        "contain at least one uppercase, one lowercase, one number and one " +
                        "special character.";

                Toast.makeText(LoginActivity.this,
                        error_message, Toast.LENGTH_LONG).show();
            }
        }

        if (error_counter == 0) {
            this.firebaseLogIn();
        }
    }

    private void firebaseLogIn() {
        startLoadingView();
        OffiMate.mAuth.signInWithEmailAndPassword(this.userEmail, this.userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    OffiMate.firebaseUser = OffiMate.mAuth.getCurrentUser();
                    userId =  OffiMate.firebaseUser.getUid();

                    coworkerRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
                            Map.Entry<String, Object> entry = raw.entrySet().iterator().next();
                            Map<String, String> values = (Map<String, String>) entry.getValue();
                            userName = values.get("name");
                            userId   = values.get("userId");
                            final String officeId = values.get("officeId");

                            officeRef.orderByKey().equalTo(officeId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    stopLoadingView();
                                    Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
                                    Map.Entry<String, Object> entry = raw.entrySet().iterator().next();
                                    Map<String, String> values = (Map<String, String>) entry.getValue();
                                    String officeName = values.get("name");
                                    userOffice = new Office(officeId, officeName);

                                    //EVERYTHING IS READY TO CONTINUE:
                                    moveToLoadingActivity();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    stopLoadingView();
                                    Toast.makeText(LoginActivity.this, "Back-end Office failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            stopLoadingView();
                            Toast.makeText(LoginActivity.this, "Back-end Coworkers failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    stopLoadingView();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void moveToLoadingActivity () {

        //FIRST: SAVE LOCAL DATA
        OffiMate.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OffiMate.currentUser = new CurrentUser(
                        userId, userName, userEmail, userPassword, userOffice);
                realm.copyToRealm(OffiMate.currentUser);
            }
        });

        //SECOND: GO AHEAD TO NEXT ACTIVITY
        Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initLoadingView (){
        findViewById(R.id.loginLoadingPanel).setVisibility(View.GONE);
    }

    private void startLoadingView () {
        findViewById(R.id.loginLoadingPanel).setVisibility(View.VISIBLE);
        this.barMenuButton.setVisible(false);
    }

    private void stopLoadingView() {
        findViewById(R.id.loginLoadingPanel).setVisibility(View.GONE);
        this.barMenuButton.setVisible(true);
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu_done, menu);
        this.barMenuButton = menu.findItem(R.id.done_bar_button_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_bar_button_item:
                this.loginAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
