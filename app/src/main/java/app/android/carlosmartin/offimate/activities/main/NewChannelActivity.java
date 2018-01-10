package app.android.carlosmartin.offimate.activities.main;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.Channel;

public class NewChannelActivity extends AppCompatActivity {

    //Firebase
    private FirebaseDatabase  database;
    private DatabaseReference channelRef;

    //UI
    private TextView headerTextView;
    private EditText newChannelEditText;
    private FloatingActionButton saveActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_channel);

        this.initFirebase();
        this.initUI();
    }

    private void initUI() {
        setTitle("New Channel");

        this.headerTextView = findViewById(R.id.newChannelTitleTextView);
        this.headerTextView.setText("NEW CHANNEL NAME:");

        this.newChannelEditText = findViewById(R.id.newChannelEditText);

        this.saveActionButton = findViewById(R.id.saveChannelActionButton);
        this.saveActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelName = newChannelEditText.getText().toString();
                if (!channelName.isEmpty()) {
                    DatabaseReference newChannelRef = channelRef.push();
                    Channel channel = new Channel("", channelName, OffiMate.currentUser.getUid());
                    Map<String, String> channelValues = channel.toMap(OffiMate.currentUser.getOffice().id);
                    newChannelRef.setValue(channelValues);
                    finish();
                }
            }
        });
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.channelRef = this.database.getReference("channels");
    }
}
