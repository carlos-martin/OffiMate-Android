package app.android.carlosmartin.offimate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import app.android.carlosmartin.offimate.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //To stop de loading
        //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}
