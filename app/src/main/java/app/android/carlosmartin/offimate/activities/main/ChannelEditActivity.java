package app.android.carlosmartin.offimate.activities.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.models.Channel;

public class ChannelEditActivity extends AppCompatActivity {

    //Firebase
    private FirebaseDatabase  database;
    private DatabaseReference channelRef;

    //DataSource
    private Channel currentChannel;
    private String  newName;

    //UI
    private TextView nameTextView;
    private EditText nameEditText;
    private FloatingActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_edit);

        this.initUI();
    }

    private void initUI() {
        this.stopLoading();

        this.nameTextView = findViewById(R.id.channelNameTextView);
        this.nameEditText = findViewById(R.id.channelNameEditText);
        this.actionButton = findViewById(R.id.saveNameActionButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.currentChannel = (Channel) bundle.getSerializable("channel");
        } else {
            this.currentChannel = new Channel("", "", "");
        }

        this.initFirebase();

        this.nameTextView.setText("NEW CHANNEL NAME");
        this.nameEditText.setText(currentChannel.name);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName = nameEditText.getText().toString();
                if (!newName.equals(currentChannel.name) && !newName.isEmpty()) {
                    currentChannel.name = newName;
                    channelRef.child("name").setValue(newName);
                    Intent intent = new Intent();
                    intent.putExtra("channel", currentChannel);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.channelRef = this.database.getReference("channels").child(this.currentChannel.id);
    }

    private void stopLoading() {
        findViewById(R.id.editChannelNameLoadingPanel).setVisibility(View.GONE);
    }
}
