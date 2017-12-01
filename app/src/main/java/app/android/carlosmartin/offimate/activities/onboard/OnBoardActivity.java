package app.android.carlosmartin.offimate.activities.onboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import app.android.carlosmartin.offimate.R;

public class OnBoardActivity extends AppCompatActivity {

    private Button buttonSignUp;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        initUI();
    }

    private void initUI() {
        setTitle("On Board");

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
        Intent intentToSignup = new Intent(OnBoardActivity.this, SignUpNameActivity.class);
        startActivity(intentToSignup);
    }

    private void buttonLoginClicked() {
        Intent intentToLogin = new Intent(OnBoardActivity.this, LoginActivity.class);
        startActivity(intentToLogin);
    }
}
