package app.android.carlosmartin.offimate.activities.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.application.OffiMate;

public class MainActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private TextView userPasswordTextView;
    private TextView userOfficeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OffiMate.currentUser.printLog("MainActivity");

        this.initUI();
    }

    private void initUI() {
        this.userNameTextView = findViewById(R.id.textViewMainUserName);
        String userName = OffiMate.currentUser.getName();
        String userUID = OffiMate.currentUser.getUid();
        this.userNameTextView.setText(userName + " - " + userUID);

        this.userEmailTextView = findViewById(R.id.textViewMainUserEmail);
        this.userEmailTextView.setText(OffiMate.currentUser.getEmail());

        this.userPasswordTextView = findViewById(R.id.textViewMainUserPassword);
        this.userPasswordTextView.setText(OffiMate.currentUser.getPassword());

        this.userOfficeTextView = findViewById(R.id.textViewMainUserOffice);
        String officeId =  OffiMate.currentUser.getOffice().id;
        String officeName = OffiMate.currentUser.getOffice().name;
        String label = officeId + " " + officeName;
        this.userOfficeTextView.setText(label);
    }
}
