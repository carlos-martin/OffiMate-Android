package app.android.carlosmartin.offimate.activities.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.profile.BoostCardActivityType;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.BoostCard;
import app.android.carlosmartin.offimate.models.BoostCardType;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.NewDate;

public class BoostCardActivity extends AppCompatActivity {

    //UI
    private View      headerView;
    private ImageView headerImageView;
    private TextView  typeTextView;
    private TextView  usernameTextView;
    private TextView  titleTextView;
    private TextView  dateTextView;
    private TextView  bodyTextView;

    //DataSource
    private String displayName;
    private BoostCard boostCard;
    private BoostCardActivityType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost_card);

        this.initData();
        this.initUI();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.boostCard = (BoostCard) bundle.getSerializable("boostcard");
            this.type = (BoostCardActivityType) bundle.get("type");
        }
    }

    private void initUI() {
        this.headerView      = findViewById(R.id.deepBCHeader);
        this.headerImageView = findViewById(R.id.deepBCImageView);
        this.typeTextView    = findViewById(R.id.deepBCTypeTextView);
        if (this.boostCard.type == BoostCardType.EXECUTION) {
            this.headerView.setBackgroundColor(getResources().getColor(R.color.execution));
            this.headerImageView.setBackgroundResource(R.drawable.ic_action_execution);
            this.typeTextView.setText("Execution");
        } else {
            this.headerView.setBackgroundColor(getResources().getColor(R.color.passion));
            this.headerImageView.setBackgroundResource(R.drawable.ic_action_passion);
            this.typeTextView.setText("Passion");
        }

        this.usernameTextView = findViewById(R.id.deepBCNameTextView);
        switch (this.type) {
            case INBOX:
                setTitle("Inbox");
                if (OffiMate.coworkers.get(boostCard.senderId) != null) {
                    this.displayName = ((Coworker) OffiMate.coworkers.get(boostCard.senderId)).name;
                } else {
                    this.displayName = "Unknown Coworker";
                }
                this.usernameTextView.setText("from: " + this.displayName);
                break;
            default:
                setTitle("Sent");
                if (OffiMate.coworkers.get(boostCard.receiverId) != null) {
                    this.displayName = ((Coworker) OffiMate.coworkers.get(boostCard.receiverId)).name;
                } else {
                    this.displayName = "Unknown Coworker";
                }
                this.usernameTextView.setText("to: " + this.displayName);
                break;
        }

        this.titleTextView = findViewById(R.id.deepBCTitleTextView);
        this.titleTextView.setText(this.boostCard.header);

        this.dateTextView = findViewById(R.id.deepBCDateTextView);
        NewDate date = this.boostCard.date;
        String hour =    (date.hour <= 9    ? "0"+date.hour    : ""+date.hour);
        String minutes = (date.minutes <= 9 ? "0"+date.minutes : ""+date.minutes);
        String sDate = date.getMonthName()+" "+date.day+", "+date.year+" - "+hour+":"+minutes;
        this.dateTextView.setText(sDate);

        this.bodyTextView = findViewById(R.id.deepBCBodyTextView);
        this.bodyTextView.setText(this.boostCard.message);
    }
}