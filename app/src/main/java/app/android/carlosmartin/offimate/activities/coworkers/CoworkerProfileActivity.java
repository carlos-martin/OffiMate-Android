package app.android.carlosmartin.offimate.activities.coworkers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.coworkers.CoworkerProfileListAdapter;
import app.android.carlosmartin.offimate.helpers.CoworkerOption;
import app.android.carlosmartin.offimate.models.Coworker;

public class CoworkerProfileActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    //UI
    private ImageView coworkerProfileImage;
    private TextView coworkerNameTextView;
    private TextView coworkerEmailTextView;
    private ListView listView;
    private CoworkerProfileListAdapter adapter;

    //DataSource
    private List<CoworkerOption> optionList;
    private Coworker coworker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coworker_profile);

        this.initUIData();
    }

    private void initUIData() {
        setTitle("Profile");

        //Fetching data from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.coworker = (Coworker) bundle.getSerializable("coworker");
        }

        this.coworkerNameTextView = (TextView) findViewById(R.id.coworkerProfileNameTextView);
        this.coworkerNameTextView.setText(this.coworker.name);

        this.coworkerEmailTextView = (TextView) findViewById(R.id.coworkerProfileEmailTextView);
        this.coworkerEmailTextView.setText(this.coworker.email);

        this.listView = findViewById(R.id.coworkerOptionListView);
        this.listView.setOnItemClickListener(this);

        CoworkerOption office = new CoworkerOption(R.drawable.ic_action_office, coworker.office.name, false);
        CoworkerOption boostCard = new CoworkerOption(R.drawable.ic_action_boost, "Send a Boost Card", true);
        this.optionList = new ArrayList<CoworkerOption>();
        this.optionList.add(office);
        this.optionList.add(boostCard);

        this.adapter = new CoworkerProfileListAdapter(this, R.layout.list_item_coworker_options, this.optionList);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(this.listView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(CoworkerProfileActivity.this, SendBoostCard1Activity.class);
        intent.putExtra("coworker", this.coworker);
        startActivity(intent);
    }
}
