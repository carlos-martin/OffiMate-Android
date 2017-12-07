package app.android.carlosmartin.offimate.activities.main;

import android.content.Intent;
import android.os.Bundle;
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
import app.android.carlosmartin.offimate.activities.onboard.OnBoardActivity;
import app.android.carlosmartin.offimate.adapters.main.ChannelsListAdapter;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.Channel;
import app.android.carlosmartin.offimate.user.CurrentUser;
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
    private List<Channel> channelList;

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference officeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initFirebase();
        this.initUI();
        this.observerChannel();
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.officeRef = this.database.getReference("channels");
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

    private void reloadListView() {
        this.adapter = new ChannelsListAdapter(this, R.layout.list_item_channels, this.channelList);
        this.listView.setAdapter(this.adapter);
        registerForContextMenu(this.listView);
    }

    private void observerChannel() {
        this.startLoadingView();

        this.channelList = new ArrayList<Channel>();
        this.officeRef.orderByChild("officeId").equalTo(OffiMate.currentUser.getOffice().id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();

                String channelId;
                String channelName;
                String channelCreatorId;

                if (raw.size() > 0) {
                    for (Map.Entry<String, Object> item : raw.entrySet()) {
                        channelId = item.getKey();
                        Map<String, String> rawChannel = (Map<String, String>) item.getValue();
                        channelName =       (String) rawChannel.get("name");
                        channelCreatorId =  (String) rawChannel.get("creator");
                        channelList.add(new Channel(channelId, channelName, channelCreatorId));
                    }
                }
                stopLoadingView();
                reloadListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                stopLoadingView();
                Toast.makeText(MainActivity.this, "Firebase error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initFloatingButton() {
        this.actionButton = (FloatingActionButton) findViewById(R.id.actionButton);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        this.headerLayout = this.navigationView.getHeaderView(0);

        this.headerNameTextView =  this.headerLayout.findViewById(R.id.name_nav_header_text_view);
        this.headerNameTextView.setText(OffiMate.currentUser.getName());

        this.headerEmailTextView = this.headerLayout.findViewById(R.id.email_nav_header_text_view);
        this.headerEmailTextView.setText(OffiMate.currentUser.getEmail());

        this.headerImageView = this.headerLayout.findViewById(R.id.imageView);

        //MENU
        this.leftSideMenu = this.navigationView.getMenu();

        this.officeMenuItem = this.leftSideMenu.findItem(R.id.nav_office);
        this.officeMenuItem.setTitle(OffiMate.currentUser.getOffice().name);

        this.passwordMenuItem = this.leftSideMenu.findItem(R.id.nav_password);
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

    private void goToCoworkersActivity() {
        Toast.makeText(
                MainActivity.this, "TO COWORKERS ACTIVITY", Toast.LENGTH_SHORT)
                .show();
    }

    //MARK: - Left Side Menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case R.id.nav_office:
                this.goToSelectionOffice();
                return true;
            case R.id.nav_password:
                this.changeViewPassword();
                return true;
            case R.id.nav_inbox:
                this.goToBoostCardInbox();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_send:
                this.goToBoostCardSend();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_log_out:
                this.logOutAction();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }
    }

    private void goToSelectionOffice() {
        //Toast.makeText(MainActivity.this, "TO SELECTION OFFICE ACTIVITY", Toast.LENGTH_SHORT).show();
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

    private void goToBoostCardInbox() {
        //Toast.makeText(MainActivity.this, "TO INBOX ACTIVITY", Toast.LENGTH_SHORT).show();
    }

    private void goToBoostCardSend() {
        //Toast.makeText(MainActivity.this, "TO SENT ACTIVITY", Toast.LENGTH_SHORT).show();
    }

    private void logOutAction() {
        //Toast.makeText(MainActivity.this, "Good Bye!", Toast.LENGTH_SHORT).show();

        OffiMate.mAuth.signOut();
        OffiMate.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OffiMate.realm.deleteAll();
            }
        });

        Intent intent = new Intent(MainActivity.this, OnBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //MARK: - List view function

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //MARK: - Loading View

    private void startLoadingView () {
        findViewById(R.id.mainLoadingPanel).setVisibility(View.VISIBLE);
    }

    private void stopLoadingView() {
        findViewById(R.id.mainLoadingPanel).setVisibility(View.GONE);
    }
}
