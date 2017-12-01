package app.android.carlosmartin.offimate.activities.onboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.helpers.Tools;

public class SignUpEmailActivity extends AppCompatActivity {

    //DataSource
    private String userName;
    private String userEmail;

    //UI
    private TextView textViewEmail;
    private EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);
        this.initUI();
    }

    private void initUI() {
        setTitle("E-mail");

        this.textViewEmail = findViewById(R.id.textViewEmail);
        this.textViewEmail.setText("WHAT'S YOUR SIGMA E-MAIL?");

        this.editTextEmail = findViewById(R.id.editTextEmail);
        this.editTextEmail.setHint("Enter your email...");
        this.editTextEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            goToSignUpOfficeActivity();
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
        }
    }

    //MARK: - Action bar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_bar_button_item:
                this.goToSignUpOfficeActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToSignUpOfficeActivity() {

        this.userEmail = this.editTextEmail.getText().toString();

        if (Tools.isValidEmail(this.userEmail)) {

            Intent intentToOffice = new Intent(SignUpEmailActivity.this,
                    SignUpOfficesActivity.class);
            intentToOffice.putExtra("user_name",  this.userName);
            intentToOffice.putExtra("user_email", this.userEmail);
            startActivity(intentToOffice);

        } else {
            this.userEmail = null;

            String error_message = "Your email must have one of this two domains: sigma.se or " +
                    "sigmatechnology.se";

            Toast.makeText(SignUpEmailActivity.this,
                    error_message , Toast.LENGTH_LONG).show();
        }
    }
}
