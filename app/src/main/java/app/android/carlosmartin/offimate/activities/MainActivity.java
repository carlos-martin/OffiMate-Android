package app.android.carlosmartin.offimate.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.onboard.LoginActivity;
import app.android.carlosmartin.offimate.activities.onboard.SignUpNameActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonSignUp;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI() {
        this.setTitle("On board");
        this.buttonSignUp = findViewById(R.id.buttonSignup);
        this.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSignupClicked();
            }
        });

        this.buttonLogin = findViewById(R.id.buttonLogin);
        this.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLoginClicked();
            }
        });
    }

    private void buttonSignupClicked() {
        Intent intentToSignup = new Intent(MainActivity.this, SignUpNameActivity.class);
        startActivity(intentToSignup);
    }

    private void buttonLoginClicked() {
        Intent intentToLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intentToLogin);
    }
}
