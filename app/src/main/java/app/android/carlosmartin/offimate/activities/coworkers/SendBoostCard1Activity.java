package app.android.carlosmartin.offimate.activities.coworkers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.coworkers.BoostCardsListAdapter;
import app.android.carlosmartin.offimate.models.BoostCard;
import app.android.carlosmartin.offimate.models.BoostCardType;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.NewDate;

public class SendBoostCard1Activity extends AppCompatActivity implements ListView.OnItemClickListener {

    //UI
    private ListView listView;
    private BoostCardsListAdapter adapter;

    //DataSource
    private Coworker coworker;
    private BoostCardType type;
    private String header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_boost_card1);

        this.initUIData();
    }

    private void initUIData() {
        setTitle("Boost Card Type");

        //Fetching data from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.coworker = (Coworker) bundle.getSerializable("coworker");
        }

        this.listView = findViewById(R.id.boostCardsListView);
        this.listView.setOnItemClickListener(this);

        this.adapter = new BoostCardsListAdapter(
                this, R.layout.list_item_boostcard_header, R.layout.list_item_boostcard_body);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(this.listView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO: fetch type and header before move forward
        this.type =   this.getType(position);
        this.header = this.getHeader(position);

        if (positionInRange(position)) {
            Intent intent = new Intent(SendBoostCard1Activity.this, SendBoostCard2Activity.class);
            intent.putExtra("coworker", this.coworker);
            intent.putExtra("type",     this.type);
            intent.putExtra("header",   this.header);
            startActivity(intent);
        }
    }

    private boolean positionInRange (int position) {
        switch (position) {
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
                return true;
            default:
                return false;
        }
    }

    private String getHeader (int position) {
        return (String) this.adapter.getItem(position);
    }

    private BoostCardType getType (int position) {
        switch (position) {
            case 1:
            case 2:
            case 3:
                return BoostCardType.PASSION;
            case 5:
            case 6:
            case 7:
                return BoostCardType.EXECUTION;
            default:
                return null;
        }
    }
}
