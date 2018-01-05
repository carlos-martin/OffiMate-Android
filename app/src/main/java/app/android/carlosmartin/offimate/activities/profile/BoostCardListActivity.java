package app.android.carlosmartin.offimate.activities.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
import app.android.carlosmartin.offimate.adapters.profile.BoostCardActivityType;
import app.android.carlosmartin.offimate.adapters.profile.BoostCardListAdapter;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.BoostCard;

public class BoostCardListActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference boostCardRef;

    //UI
    private ListView listView;
    private BoostCardListAdapter adapter;

    //DataSource
    private BoostCardActivityType activityType;
    private List<BoostCard> boostCardList = new ArrayList<>();
    private BoostCard selectedBoostCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost_card_list);
        this.initFirebase();
        this.fetchBundle();
        this.initUI();
        this.observerBoostCard();
    }

    private void observerBoostCard() {
        this.startLoadingView();
        final String uid = OffiMate.currentUser.getUid();
        String type = (this.activityType == BoostCardActivityType.INBOX ? "receiverId" : "senderId");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BoostCard boostCard = Tools.dataSnapshotToBoostCard(dataSnapshot);
                addBoostCard(boostCard);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /* Update in case we add remove functionality */
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = "Firebase error.";
                Tools.showInfoMessage(listView, message);
            }

            public void addBoostCard(BoostCard boostCard) {
                boostCardList.add(boostCard);

                Collections.sort(boostCardList, new Comparator<BoostCard>() {
                    @Override
                    public int compare(BoostCard o1, BoostCard o2) {
                        return o2.date.compareTo(o1.date);
                    }
                });
                stopLoadingView();
                reloadListView();
            }
        };

        this.boostCardRef.orderByChild(type).equalTo(uid)
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
                String message = "Firebase error.";
                Tools.showInfoMessage(listView, message);
            }
        };

        this.boostCardRef.orderByChild(type).equalTo(uid)
                .addValueEventListener(valueEventListener);
    }


    private void reloadListView() {
        if (this.boostCardList == null || this.boostCardList.size() <= 0) {
            View view = findViewById(android.R.id.content);
            Tools.showInfoMessage(view, "You don't have any boost card yet.");
        }

        this.adapter = new BoostCardListAdapter(this, R.layout.list_item_boost_card, this.boostCardList, BoostCardActivityType.INBOX);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(listView);
    }

    private void fetchBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.activityType = (BoostCardActivityType) bundle.get("activityType");
        }
    }

    private void initUI() {
        switch (this.activityType) {
            case INBOX:
                setTitle("Inbox");
                break;
            default:
                setTitle("Sent");
                break;
        }

        this.stopLoadingView();

        this.listView = findViewById(R.id.coworkersListView);
        this.listView.setOnItemClickListener(this);
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.boostCardRef = this.database.getReference("boostcard");
    }

    //MARK: - List view function

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //MARK: - Loading View

    private void startLoadingView() {
        findViewById(R.id.inboxLoadingPanel).setVisibility(View.VISIBLE);
    }

    private void stopLoadingView() {
        findViewById(R.id.inboxLoadingPanel).setVisibility(View.GONE);
    }

    //MARK: - Navigation
}
