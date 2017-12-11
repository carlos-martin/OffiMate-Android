package app.android.carlosmartin.offimate.activities.coworkers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.coworkers.BoostCardsListAdapter;
import app.android.carlosmartin.offimate.models.Coworker;

public class SendBoostCard1Activity extends AppCompatActivity implements ListView.OnItemClickListener {

    //UI
    private ListView listView;
    private BoostCardsListAdapter adapter;

    //DataSource
    private Coworker coworker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_boost_card1);

        this.initUIData();
    }

    private void initUIData() {
        setTitle("Boost Card");

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

    }
}
