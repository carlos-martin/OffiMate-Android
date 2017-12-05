package app.android.carlosmartin.offimate.activities.onboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.onboard.SignUpOfficesListAdapter;
import app.android.carlosmartin.offimate.models.Office;

public class SignUpOfficesActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    //DataSource
    private String userName;
    private String userEmail;
    private Office userOffice;
    private List<Office> officeList;

    //UI
    private TextView textViewOffice;
    private ListView listView;
    private SignUpOfficesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_offices);

        this.observerOffice();
        this.initUI();
    }

    private void initUI() {
        setTitle("Office");

        this.textViewOffice = findViewById(R.id.textViewOffice);
        this.textViewOffice.setText("WHAT'S YOUR SIGMA OFFICE?");

        this.listView = findViewById(R.id.officeListView);
        this.listView.setOnItemClickListener(this);

        this.adapter = new SignUpOfficesListAdapter(this, R.layout.list_item_offices, this.officeList);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(listView);

        //Fetching data from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.userName = bundle.getString("user_name");
            this.userEmail = bundle.getString("user_email");
        }
    }

    private void observerOffice() {
        this.officeList = new ArrayList<Office>() {{
            add(new Office("000","Jönköping"));
            add(new Office("001","Göteborg"));
            add(new Office("002","Linköping"));
        }};
    }

    //MARK: - List view function

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        this.userOffice = this.officeList.get(position);
        this.goToSignUpPasswordActivity();
    }

    private void goToSignUpPasswordActivity() {
        Intent intent = new Intent(SignUpOfficesActivity.this,
                SignUpPasswordActivity.class);
        intent.putExtra("user_name",  this.userName);
        intent.putExtra("user_email", this.userEmail);
        intent.putExtra("user_office", this.userOffice);
        startActivity(intent);
    }
}
