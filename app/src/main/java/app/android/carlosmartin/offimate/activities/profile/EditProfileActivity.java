package app.android.carlosmartin.offimate.activities.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.Office;

public class EditProfileActivity extends AppCompatActivity {

    //DataSource
    private String userName;
    private Office userOffice;
    private ArrayList<Office> officeList  = new ArrayList<>();
    private String[] displayName;

    //UI
    private TextView     headerNameTextView;
    private TextView     headerOfficeTextView;
    private EditText     nameEditText;
    private NumberPicker officeNumberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        this.initUI();
        this.initData();
    }

    private void initUI() {
        setTitle("Profile");

        this.headerNameTextView   = findViewById(R.id.headerNameTextView);
        this.headerOfficeTextView = findViewById(R.id.headerOfficeTextView);
        this.nameEditText         = findViewById(R.id.userNameEditText);
        this.officeNumberPicker   = findViewById(R.id.officeNumberPicker);
        this.headerNameTextView
                .setText("YOUR NAME:");
        this.headerOfficeTextView
                .setText("YOUR OFFICE:");
    }

    private void initData() {
        this.displayName = new String[OffiMate.offices.size()];
        this.userName    = OffiMate.currentUser.getName();
        this.userOffice  = OffiMate.currentUser.getOffice();

        this.officeNumberPicker.setMinValue(0);
        this.officeNumberPicker.setMaxValue(OffiMate.offices.size()-1);

        int i = 0;
        for (Map.Entry<String, Object> entry : OffiMate.offices.entrySet()) {
            Office o = (Office) entry.getValue();
            this.officeList.add(o);
            this.displayName[i] = o.name;
            i++;
        }
        this.officeNumberPicker.setDisplayedValues(this.displayName);
        this.officeNumberPicker.setValue(this.officeList.indexOf(this.userOffice));

        this.nameEditText.setText(this.userName);
        this.stopLoading();
    }
    private void stopLoading() {
        findViewById(R.id.editProfileLoadingPanel).setVisibility(View.GONE);
    }

}
