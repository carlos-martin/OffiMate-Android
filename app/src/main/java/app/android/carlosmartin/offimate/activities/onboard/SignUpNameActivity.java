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

public class SignUpNameActivity extends AppCompatActivity {

    //DataSource
    private String userName;

    //UI
    private TextView textViewName;
    private EditText editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_name);
        this.initUI();
    }

    private void initUI() {
        setTitle("Sign Up");

        this.textViewName = findViewById(R.id.textViewName);
        this.textViewName.setText("WHAT'S YOUR NAME?");

        this.editTextName = findViewById(R.id.editTextName);
        this.editTextName.setHint("Enter your name...");
        this.editTextName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            goToSignUpEmailActivity();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
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
                this.goToSignUpEmailActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void goToSignUpEmailActivity() {
        this.userName = this.editTextName.getText().toString();

        if (this.userName.isEmpty()) {
            Toast.makeText(SignUpNameActivity.this,
                    "Name text field cannot be empty", Toast.LENGTH_LONG).show();
        } else {
            Intent intentToEmail = new Intent(SignUpNameActivity.this,
                    SignUpEmailActivity.class);
            intentToEmail.putExtra("user_name",this.userName);
            startActivity(intentToEmail);
        }
    }
}
