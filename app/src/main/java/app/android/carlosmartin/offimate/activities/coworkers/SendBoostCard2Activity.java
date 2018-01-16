package app.android.carlosmartin.offimate.activities.coworkers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.main.MainActivity;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.BoostCard;
import app.android.carlosmartin.offimate.models.BoostCardType;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.NewDate;

public class SendBoostCard2Activity extends AppCompatActivity {

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference boostCardRef;

    //UI
    private View      headerView;
    private ImageView headerImageView;
    private TextView  titleTextView;
    private TextView  receiverTextView;
    private EditText  messageEditText;

    //DataSource
    private Coworker coworker;
    private BoostCardType type;
    private String header;
    private String message;
    private NewDate date;
    private BoostCard boostCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_boost_card2);
        this.initData();
        this.initUI();
        this.initFirebase();
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.boostCardRef = this.database.getReference("boostcard").push();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.coworker = (Coworker) bundle.getSerializable("coworker");
            this.type = (BoostCardType) bundle.getSerializable("type");
            this.header = bundle.getString("header");
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initUI() {
        setTitle("Boost Card Message");

        this.headerView       = findViewById(R.id.boostCardMessageHeader);
        this.headerImageView  = findViewById(R.id.boostCardImageView);
        if (this.type == BoostCardType.EXECUTION) {
            this.headerView.setBackgroundColor(getResources().getColor(R.color.execution));
            this.headerImageView.setBackgroundResource(R.drawable.ic_action_execution);
        } else {
            this.headerView.setBackgroundColor(getResources().getColor(R.color.passion));
            this.headerImageView.setBackgroundResource(R.drawable.ic_action_passion);
        }

        this.titleTextView = findViewById(R.id.boostCardTitleTextView);
        this.titleTextView.setText(this.header);

        this.receiverTextView = findViewById(R.id.boostCardReceiverTextView);
        this.receiverTextView.setText("to: " + coworker.name);

        this.messageEditText  = findViewById(R.id.boostCardEditText);
        this.messageEditText.setHint("Why?");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_bar_button_item:
                this.sendBoostCard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void sendBoostCard() {
        this.message = this.messageEditText.getText().toString();
        if (this.message == null || this.message.isEmpty()) {
            String message = "The Boost Card cannot have an empty message.";
            Tools.showInfoMessage(this.messageEditText, message);
        } else {
            this.date = new NewDate(new Date());
            this.createBoostCard();
            Toast.makeText(SendBoostCard2Activity.this,
                    "Boost Card sent properly", Toast.LENGTH_LONG).show();
            this.goToMainActivity();
        }
    }

    private void createBoostCard() {
        this.boostCard = new BoostCard("", OffiMate.currentUser.getUid(),
                this.coworker.uid, this.type, this.header, this.message, this.date);
        this.boostCardRef.setValue(this.boostCard.toMap());
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SendBoostCard2Activity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
