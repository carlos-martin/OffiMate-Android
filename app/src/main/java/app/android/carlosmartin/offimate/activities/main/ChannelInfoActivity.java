package app.android.carlosmartin.offimate.activities.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.adapters.coworkers.CoworkersListAdapter;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.Channel;
import app.android.carlosmartin.offimate.models.Coworker;

public class ChannelInfoActivity extends AppCompatActivity {

    //Firebase
    private FirebaseDatabase  database;
    private DatabaseReference coworkerRef;

    //UI
    private MenuItem barMenuButton;
    private TextView channelTextView;
    private TextView membersTextView;
    private ListView membersListView;
    private CoworkersListAdapter adapter;

    //DataSource
    private List<Coworker> coworkerList = new ArrayList<Coworker>();
    private Channel currentChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_info);
        this.initFirebase();
        this.initUI();
        this.observerCoworker();
    }

    private void initUI() {
        setTitle("Channel Information");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.currentChannel = (Channel) bundle.getSerializable("channel");
        } else {
            this.currentChannel = new Channel("", "", "");
        }

        this.channelTextView = findViewById(R.id.channelNameTextView);
        this.channelTextView.setText(this.currentChannel.name);

        this.membersTextView = findViewById(R.id.membersTextView);
        this.membersTextView.setText("MEMBERS");

        this.membersListView = findViewById(R.id.membersListView);

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
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO: onChildMoved never will happen
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "Firebase error.";
                Tools.showInfoMessage(membersListView, message);
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
        this.membersListView.setAdapter(this.adapter);
        registerForContextMenu(this.membersListView);
    }

    //MARK: - Loading View

    private void startLoadingView() {
        findViewById(R.id.membersLoadingPanel).setVisibility(View.VISIBLE);
    }

    private void stopLoadingView() {
        findViewById(R.id.membersLoadingPanel).setVisibility(View.GONE);
    }

    //MARK: - Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (OffiMate.currentUser.getUid().equals(this.currentChannel.creator)) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.action_bar_menu_edit, menu);
            this.barMenuButton = menu.findItem(R.id.edit_bar_button_item);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_bar_button_item:
                this.editAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editAction() {

    }
}