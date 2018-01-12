package app.android.carlosmartin.offimate.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.activities.LoadingActivity;
import app.android.carlosmartin.offimate.activities.coworkers.CoworkerListActivity;
import app.android.carlosmartin.offimate.activities.profile.BoostCardListActivity;
import app.android.carlosmartin.offimate.activities.profile.EditProfileActivity;
import app.android.carlosmartin.offimate.adapters.main.ChannelsListAdapter;
import app.android.carlosmartin.offimate.adapters.profile.BoostCardActivityType;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.Channel;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListView.OnItemClickListener {

    //UI
    private Toolbar               toolbar;
    private DrawerLayout          drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView        navigationView;

    private View      headerLayout;
    private ImageView headerImageView;
    private TextView  headerNameTextView;
    private TextView  headerEmailTextView;

    private FloatingActionButton actionButton;

    private Menu     leftSideMenu;
    private MenuItem officeMenuItem;
    private MenuItem passwordMenuItem;
    private Boolean  isPasswordHide = true;
    private String   hidePassword = "";
    private String   showPassword = OffiMate.currentUser.getPassword();

    private TextView channelTextView;
    private ListView listView;
    private ChannelsListAdapter adapter;

    //DataSource
    private List<Channel> channelList = new ArrayList<Channel>();

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference channelRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initFirebase();
        this.initUI();
        this.observerChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.channelList.set(requestCode, (Channel) data.getSerializableExtra("channel"));
            this.reloadListView();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initData() {
        this.headerNameTextView.setText(OffiMate.currentUser.getName());
        this.headerEmailTextView.setText(OffiMate.currentUser.getEmail());
    }

    private void observerChannel() {
        this.startLoadingView();
        String currentOfficeId = OffiMate.currentUser.getOffice().id;

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, String> raw = (Map<String, String>) dataSnapshot.getValue();

                String channelId = dataSnapshot.getKey();
                String channelName =      raw.get("name");
                String channelCreatorId = raw.get("creator");
                channelList.add(new Channel(channelId, channelName, channelCreatorId));

                stopLoadingView();
                reloadListView();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO: update the message counter
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Map<String, String> raw = (Map<String, String>) dataSnapshot.getValue();

                String channelId = dataSnapshot.getKey();
                String channelName =      raw.get("name");
                String channelCreatorId = raw.get("creator");
                channelList.remove(new Channel(channelId, channelName, channelCreatorId));

                stopLoadingView();
                reloadListView();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO: onChildMoved never will happen
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*
                View view = findViewById(R.id.actionButton);
                String message = "Firebase error.";
                Tools.showInfoMessage(view, message);
                */
            }
        };

        this.channelRef.orderByChild("officeId").equalTo(currentOfficeId)
                .addChildEventListener(childEventListener);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    stopLoadingView();
                    reloadListView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*
                View view = findViewById(R.id.actionButton);
                String message = "Firebase error.";
                Tools.showInfoMessage(view, message);
                */
            }
        };

        this.channelRef.orderByChild("officeId").equalTo(currentOfficeId)
                .addValueEventListener(valueEventListener);
    }

    private void reloadListView() {
        if (this.channelList == null || this.channelList.size() <= 0) {
            View view = findViewById(R.id.actionButton);
            String message = "There are no channel yet.";
            Tools.showInfoMessage(view, message);
        }

        this.adapter = new ChannelsListAdapter(this, R.layout.list_item_channels, this.channelList);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(this.listView);
    }

    private void initUI() {
        //Navigation Title
        setTitle("OffiMate");
        for (int i = 0; i < this.showPassword.length(); i++) {
            this.hidePassword += "×";
        }
        //FloatingButton
        this.initFloatingButton();
        //LeftSideMenu
        this.initLeftSideMenu();
        //Navigation
        this.initNavigationView();
        //ListView
        this.listView = findViewById(R.id.channelListView);
        this.listView.setOnItemClickListener(this);
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.channelRef = this.database.getReference("channels");
    }

    private void initFloatingButton() {
        this.actionButton = (FloatingActionButton) findViewById(R.id.actionButton);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                goToNewChannelActivity();
            }
        });
    }

    private void initLeftSideMenu() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.toggle = new ActionBarDrawerToggle(
                this, this.drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawer.addDrawerListener(toggle);
        this.toggle.syncState();
    }

    private void initNavigationView() {
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);

        //HEADER
        this.headerLayout        = this.navigationView.getHeaderView(0);
        this.headerNameTextView  = this.headerLayout.findViewById(R.id.name_nav_header_text_view);
        this.headerEmailTextView = this.headerLayout.findViewById(R.id.email_nav_header_text_view);
        this.headerImageView     = this.headerLayout.findViewById(R.id.imageView);

        //MENU
        this.leftSideMenu     = this.navigationView.getMenu();
        this.passwordMenuItem = this.leftSideMenu.findItem(R.id.nav_password);
        this.officeMenuItem   = this.leftSideMenu.findItem(R.id.nav_office);
        this.officeMenuItem.setTitle(OffiMate.currentUser.getOffice().name);
        this.changeViewPassword();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //MARK: - Action Bar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu_coworkers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.coworkers_bar_button_item:
                this.goToCoworkersActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //MARK: - Left Side Menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case R.id.nav_office:
                return true;
            case R.id.nav_password:
                this.changeViewPassword();
                return true;
            case R.id.nav_edit:
                this.goToEditProfile();
                //drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_inbox:
                this.goToBoostCardInbox();
                //drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_send:
                this.goToBoostCardSend();
                //drawer.closeDrawer(GravityCompat.START);
                return true;
            /* TODO: connexion to create a new office here
            case R.id.nav_new_office:
                this.goToNewOffice();
                return true;
            */
            case R.id.nav_log_out:
                this.logOutAction();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }
    }

    private void changeViewPassword() {
        if (this.isPasswordHide) {
            this.passwordMenuItem.setTitle(this.hidePassword);
            this.passwordMenuItem.setIcon(R.drawable.ic_action_show);
            this.isPasswordHide = false;
        } else {
            this.passwordMenuItem.setTitle(this.showPassword);
            this.passwordMenuItem.setIcon(R.drawable.ic_action_hide);
            this.isPasswordHide = true;
        }
    }

    private void logOutAction() {
        //Toast.makeText(MainActivity.this, "Good Bye!", Toast.LENGTH_SHORT).show();
        startLoadingView();
        OffiMate.mAuth.signOut();
        OffiMate.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OffiMate.realm.deleteAll();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopLoadingView();
                OffiMate.currentUser = null;
                OffiMate.firebaseUser = null;
                goToLoadingActivity();
            }
        }, 1000);
    }

    //MARK: - List view function

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO: complete to go to selected channel activity.
        this.goToChannelActivity(this.channelList.get(position), position);
    }

    //MARK: - Navigation

    private void goToChannelActivity(Channel channel, int position) {
        Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
        intent.putExtra("channel", channel);
        startActivityForResult(intent, position);
    }

    private void goToCoworkersActivity() {
        Intent intent = new Intent(MainActivity.this, CoworkerListActivity.class);
        startActivity(intent);
    }

    private void goToLoadingActivity() {
        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goToEditProfile() {
        //TODO: complete to go to edit profile activity
        //Toast.makeText(MainActivity.this, "TO SELECTION OFFICE ACTIVITY", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    private void goToBoostCardInbox() {
        Intent intent = new Intent(MainActivity.this, BoostCardListActivity.class);
        intent.putExtra("activityType", BoostCardActivityType.INBOX);
        startActivity(intent);
    }

    private void goToBoostCardSend() {
        Intent intent = new Intent(MainActivity.this, BoostCardListActivity.class);
        intent.putExtra("activityType", BoostCardActivityType.SENT);
        startActivity(intent);
    }

    private void goToNewOffice() {
        //TODO: complete to go to new office activity
    }

    private void goToNewChannelActivity() {
        Intent intent = new Intent(MainActivity.this, NewChannelActivity.class);
        startActivity(intent);
    }

    //MARK: - Loading View

    private void startLoadingView() {
        findViewById(R.id.mainLoadingPanel).setVisibility(View.VISIBLE);
    }

    private void stopLoadingView() {
        findViewById(R.id.mainLoadingPanel).setVisibility(View.GONE);
    }
}
