package app.android.carlosmartin.offimate.activities.coworkers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.coworkers.CoworkersListAdapter;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.Office;
import app.android.carlosmartin.offimate.user.CurrentUser;

public class CoworkerListActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    //Firebase
    private FirebaseDatabase  database;
    private DatabaseReference coworkerRef;

    //UI
    private TextView coworkersTextView;
    private ListView listView;
    private CoworkersListAdapter adapter;

    //DataSource
    private List<Coworker> coworkerList = new ArrayList<Coworker>();
    private Coworker selectedCoworker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coworker_list);
        this.initFirebase();
        this.initUI();
        this.observerCoworker();
    }

    private void initUI() {
        setTitle("Coworkers");

        this.stopLoadingView();

        this.coworkersTextView = findViewById(R.id.textViewCoworkers);
        this.coworkersTextView.setText("YOUR OFFICE COWORKERS");

        this.listView = findViewById(R.id.coworkersListView);
        this.listView.setOnItemClickListener(this);
    }

    private void initFirebase() {
        this.database    = FirebaseDatabase.getInstance();
        this.coworkerRef = this.database.getReference("coworkers");
    }

    private void observerCoworker() {
        this.startLoadingView();
        final String currentOfficeId = OffiMate.currentUser.getOffice().id;

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Coworker c = Tools.dataSnapshotToCoworker(dataSnapshot);
                if (!c.email.equals(OffiMate.currentUser.getEmail())) {
                    addCoworker(c);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Coworker c = Tools.dataSnapshotToCoworker(dataSnapshot);
                if (!c.office.id.equals(OffiMate.currentUser.getOffice().id)) {
                    removeCoworker(c);
                } else {
                    updateCoworker(c);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Coworker c = Tools.dataSnapshotToCoworker(dataSnapshot);
                removeCoworker(c);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "Firebase error.";
                Tools.showInfoMessage(listView, message);
            }

            public void addCoworker(Coworker coworker) {
                OffiMate.coworkers.put(coworker.uid, coworker);

                coworkerList.add(coworker);
                Collections.sort(coworkerList, new Comparator<Coworker>() {
                    @Override
                    public int compare(Coworker o1, Coworker o2) {
                        return o1.name.compareTo(o2.name);
                    }
                });
                stopLoadingView();
                reloadListView();
            }

            public void updateCoworker(Coworker coworker) {
                OffiMate.coworkers.put(coworker.uid, coworker);

                int index = coworkerList.indexOf(coworker);
                coworkerList.set(index, coworker);

                stopLoadingView();
                reloadListView();
            }

            public void removeCoworker(Coworker coworker) {
                OffiMate.coworkers.remove(coworker.uid);

                coworkerList.remove(coworker);
                stopLoadingView();
                reloadListView();
            }
        };

        this.coworkerRef.orderByChild("officeId").equalTo(currentOfficeId)
                .addChildEventListener(childEventListener);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 1) {
                    stopLoadingView();
                    reloadListView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                View view = findViewById(R.id.actionButton);
                String message = "Firebase error.";
                Tools.showInfoMessage(view, message);
            }
        };

        this.coworkerRef.orderByChild("officeId").equalTo(currentOfficeId)
                .addValueEventListener(valueEventListener);
    }

    private void reloadListView() {
        if (this.coworkerList == null || this.coworkerList.size() <= 0) {
            View view = findViewById(android.R.id.content);
            Tools.showInfoMessage(view, "There are no coworkers yet.");
        }
        this.adapter = new CoworkersListAdapter(this, R.layout.list_item_coworkers, this.coworkerList);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(this.listView);
    }



    //MARK: - List view function

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.selectedCoworker = this.coworkerList.get(position);
        this.goToCoworkerProfileActivity();
    }

    //MARK: - Loading View

    private void startLoadingView() {
        findViewById(R.id.coworkersLoadingPanel).setVisibility(View.VISIBLE);
    }

    private void stopLoadingView() {
        findViewById(R.id.coworkersLoadingPanel).setVisibility(View.GONE);
    }

    //MARK: - Navigation

    private void goToCoworkerProfileActivity() {
        Intent intent = new Intent(CoworkerListActivity.this, CoworkerProfileActivity.class);
        intent.putExtra("coworker", this.selectedCoworker);
        startActivity(intent);
    }
}
