package app.android.carlosmartin.offimate.activities.onboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.onboard.SignUpOfficesListAdapter;
import app.android.carlosmartin.offimate.models.Office;

public class SignUpOfficesActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference officeRef;

    //DataSource
    private String userName;
    private String userEmail;
    private Office userOffice;
    private List<Office> officeList;

    //UI
    private TextView officeTextView;
    private ListView listView;
    private SignUpOfficesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_offices);

        this.initFirebase();

        this.initUI();

        this.observerOffice();
    }


    private void initUI() {
        setTitle("Office");

        this.stopLoadingView();

        this.officeTextView = findViewById(R.id.textViewOffice);
        this.officeTextView.setText("WHAT'S YOUR SIGMA OFFICE?");

        //Fetching data from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.userName = bundle.getString("user_name");
            this.userEmail = bundle.getString("user_email");
        }

        this.listView = findViewById(R.id.officeListView);
        this.listView.setOnItemClickListener(this);
    }

    private void reloadListView() {
        this.adapter = new SignUpOfficesListAdapter(this, R.layout.list_item_offices, this.officeList);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(this.listView);
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.officeRef = this.database.getReference("office");
    }

    private void observerOffice() {
        this.startLoadingView();

        this.officeList = new ArrayList<Office>();
        this.officeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();

                String id;
                String name = "";

                for (Map.Entry<String, Object> item : raw.entrySet()) {
                    id = item.getKey();
                    Map<String, String> rawName = (Map<String, String>) item.getValue();
                    for (Map.Entry<String, String> itemName : rawName.entrySet()) {
                        name = itemName.getValue();
                    }
                    officeList.add(new Office(id, name));
                }
                reloadListView();
                stopLoadingView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                stopLoadingView();
                Toast.makeText(SignUpOfficesActivity.this,
                        "Firebase error", Toast.LENGTH_LONG).show();
            }
        });

    }

    //MARK: - Navigation

    private void goToSignUpPasswordActivity() {
        Intent intent = new Intent(SignUpOfficesActivity.this,
                SignUpPasswordActivity.class);
        intent.putExtra("user_name",  this.userName);
        intent.putExtra("user_email", this.userEmail);
        intent.putExtra("user_office", this.userOffice);
        startActivity(intent);
    }

    //MARK: - Loading View

    private void startLoadingView () {
        findViewById(R.id.officesLoadingPanel).setVisibility(View.VISIBLE);
    }

    private void stopLoadingView() {
        findViewById(R.id.officesLoadingPanel).setVisibility(View.GONE);
    }

    //MARK: - List view function

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        this.userOffice = this.officeList.get(position);
        this.goToSignUpPasswordActivity();
    }


}
