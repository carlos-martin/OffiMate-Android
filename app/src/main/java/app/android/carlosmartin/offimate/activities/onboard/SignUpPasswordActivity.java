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

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.LoadingActivity;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.Office;
import app.android.carlosmartin.offimate.user.CurrentUser;
import io.realm.Realm;

public class SignUpPasswordActivity extends AppCompatActivity {

    //DataSource
    private String userName;
    private String userEmail;
    private Office userOffice;
    private String userPassword;

    //UI
    private TextView textViewPassword;
    private EditText editTextPassword;
    private MenuItem barMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_password);
        this.initUI();
    }

    private void initUI() {
        setTitle("Password");

        this.initLoadingView();

        this.textViewPassword = findViewById(R.id.textViewPassword);
        this.textViewPassword.setText("ADD A PASSWORD FOR YOUR ACCOUNT");

        this.editTextPassword = findViewById(R.id.editTextPassword);
        this.editTextPassword.setHint("Enter password...");
        this.editTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            createUserAccount();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        //Fetching data from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.userName = bundle.getString("user_name");
            this.userEmail = bundle.getString("user_email");
            this.userOffice = (Office) bundle.getSerializable("user_office");
        }

        Log.d("{{userName}}:", this.userName);
        Log.d("{{userEmail}}:", this.userEmail);
        Log.d("{{userOffice}}:", this.userOffice.toString());
    }

    private void createUserAccount() {
        this.userPassword = this.editTextPassword.getText().toString();
        if (Tools.isValidPassword(this.userPassword)) {
            /*
             * TODO: Create user account
             */

            startLoadingView();

            OffiMate.mAuth.createUserWithEmailAndPassword(this.userEmail, this.userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            stopLoadingView();
                            if (task.isSuccessful()) {

                                //TODO: Create Coworker entry

                                OffiMate.firebaseUser = OffiMate.mAuth.getCurrentUser();
                                OffiMate.realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        OffiMate.currentUser = new CurrentUser(
                                                userName, userEmail, userPassword, userOffice);
                                        realm.copyToRealm(OffiMate.currentUser);
                                    }
                                });

                                moveToLoadingActivity();

                            } else {
                                /*
                                 * task.getException());
                                 */
                                Toast.makeText(SignUpPasswordActivity.this,
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            this.userPassword = null;

            String error_message = "The password must be a minimum of 8 characters and must " +
                    "contain at least one uppercase, one lowercase, one number and one " +
                    "special character.";

            Toast.makeText(SignUpPasswordActivity.this,
                    error_message, Toast.LENGTH_LONG).show();
        }
    }

    //MARK: - Navigation

    private void moveToLoadingActivity () {
        Intent intent = new Intent(SignUpPasswordActivity.this,
                LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //MARK: - Loading View

    private void initLoadingView() {
        findViewById(R.id.signUpLoadingPanel).setVisibility(View.GONE);
    }

    private void startLoadingView () {
        findViewById(R.id.signUpLoadingPanel).setVisibility(View.VISIBLE);
        this.barMenuButton.setVisible(false);
    }

    private void stopLoadingView() {
        findViewById(R.id.signUpLoadingPanel).setVisibility(View.GONE);
        this.barMenuButton.setVisible(true);
    }

    //MARK: - Menu Button
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
                this.createUserAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
