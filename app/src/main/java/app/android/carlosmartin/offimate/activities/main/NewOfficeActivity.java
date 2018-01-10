package app.android.carlosmartin.offimate.activities.main;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import app.android.carlosmartin.offimate.R;

public class NewOfficeActivity extends AppCompatActivity {

    //UI
    private TextView officeNameTextView;
    private TextView validateCodeTextView;
    private EditText officeNameEditText;
    private EditText validateCodeEditText;
    private FloatingActionButton saveActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_office);

        this.initUI();
    }

    private void initUI() {
        this.officeNameTextView   = findViewById(R.id.officeNameTextView);
        this.officeNameEditText   = findViewById(R.id.officeNameEditText);
        this.validateCodeTextView = findViewById(R.id.validationCodeTextView);
        this.validateCodeEditText = findViewById(R.id.validationCodeEditText);

        this.officeNameTextView.setText("WHAT'S YOUR OFFICE NAME?");
        this.officeNameEditText.setHint("Enter your office name...");
        this.validateCodeTextView.setText("WHAT'S YOUR VALIDATION CODE?");
        this.validateCodeEditText.setHint("Enter your validation code...");
    }
}
