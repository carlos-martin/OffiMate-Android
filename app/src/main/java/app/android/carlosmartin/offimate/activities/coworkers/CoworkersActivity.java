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

public class CoworkersActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference coworkerRef;
    private DatabaseReference officeRef;

    //UI
    private TextView coworkersTextView;
    private ListView listView;
    private CoworkersListAdapter adapter;

    //DataSource
    private List<Coworker> coworkerList = new ArrayList<Coworker>();
    private Coworker selectedCoworker;
    private int loadingCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coworkers);
        this.initFirebase();
        this.initUI();
        this.observerCoworker();
    }

    private void observerCoworker() {
        this.startLoadingView();
        final String currentOfficeId = OffiMate.currentUser.getOffice().id;

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> raw = (Map<String, String>) dataSnapshot.getValue();

                final String id = dataSnapshot.getKey();
                final String email    = raw.get("email");
                final String name     = raw.get("name");
                final String userId   = raw.get("userId");
                final String officeId = raw.get("officeId");

                if (!email.equals(OffiMate.currentUser.getEmail())) {
                    officeRef.orderByKey().equalTo(officeId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            stopLoadingView();
                            Map<String, Object>       raw    = (Map<String, Object>) dataSnapshot.getValue();
                            Map.Entry<String, Object> entry  = raw.entrySet().iterator().next();
                            Map<String, String>       values = (Map<String, String>) entry.getValue();

                            String officeName = values.get("name");
                            Office office = new Office(officeId, officeName);

                            coworkerList.add(new Coworker(id, userId, email, name, office));

                            Collections.sort(coworkerList, new Comparator<Coworker>() {
                                @Override
                                public int compare(Coworker o1, Coworker o2) {
                                    return o1.name.compareTo(o2.name);
                                }
                            });
                            stopLoadingView();
                            reloadListView();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            stopLoadingView();
                            Toast.makeText(CoworkersActivity.this, "Firebase Office failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map<String, String> raw = (Map<String, String>) dataSnapshot.getValue();

                String id = dataSnapshot.getKey();
                String email    = raw.get("email");
                String name     = raw.get("name");
                String userId   = raw.get("userId");
                String officeId = raw.get("officeId");

                if (!officeId.equals(OffiMate.currentUser.getOffice().id)) {
                    Coworker toRemove = new Coworker(id, userId, email, name, new Office(officeId, ""));
                    coworkerList.remove(toRemove);
                } else {
                    Coworker toUpdate = new Coworker(id, userId, email, name, OffiMate.currentUser.getOffice());
                    int index = coworkerList.indexOf(toUpdate);
                    coworkerList.set(index, toUpdate);
                }

                stopLoadingView();
                reloadListView();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Map<String, String> raw = (Map<String, String>) dataSnapshot.getValue();

                String id = dataSnapshot.getKey();
                String email    = raw.get("email");
                String name     = raw.get("name");
                String userId   = raw.get("userId");
                String officeId = raw.get("officeId");
                coworkerList.remove(new Coworker(id, userId, email, name, new Office(officeId,"")));

                stopLoadingView();
                reloadListView();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO: onChildMoved never will happen
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                View view = findViewById(R.id.actionButton);
                String message = "Firebase error.";
                Tools.showInfoMessage(view, message);
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

    private void initUI() {
        setTitle("Coworkers");

        this.stopLoadingView();

        this.coworkersTextView = findViewById(R.id.textViewCoworkers);
        this.coworkersTextView.setText("YOUR OFFICE COWORKERS");

        this.listView = findViewById(R.id.coworkersListView);
        this.listView.setOnItemClickListener(this);
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.coworkerRef = this.database.getReference("coworkers");
        this.officeRef =   this.database.getReference("office");
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
        Intent intent = new Intent(CoworkersActivity.this, CoworkerProfileActivity.class);
        intent.putExtra("coworker", this.selectedCoworker);
        startActivity(intent);
    }
}
