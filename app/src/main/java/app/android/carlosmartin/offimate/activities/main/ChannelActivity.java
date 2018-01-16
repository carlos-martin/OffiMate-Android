package app.android.carlosmartin.offimate.activities.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;
import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.helpers.Tools;
import app.android.carlosmartin.offimate.models.Channel;
import app.android.carlosmartin.offimate.adapters.main.CustomIncomingMessageViewHolder;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.Message;
import app.android.carlosmartin.offimate.models.NewDate;
import app.android.carlosmartin.offimate.user.CurrentUser;

public class ChannelActivity extends AppCompatActivity implements DateFormatter.Formatter {

    //UI
    private MenuItem     barMenuButton;
    private MessageInput inputView;
    private MessagesList messagesList;
    private MessagesListAdapter<Message> adapter;

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference channelRef;
    private DatabaseReference messageRef;
    private DatabaseReference coworkerRef;

    //DataSource
    private Channel channel;
    private Channel deprecatedChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        this.fetchBundle();
        this.initFirebase();
        this.initUI();
        this.observerMessage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.deprecatedChannel = this.channel;
            this.channel = (Channel) data.getSerializableExtra("channel");
            setTitle(this.channel.name);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.deprecatedChannel != null && !this.deprecatedChannel.name.equals(this.channel.name)) {
            Intent intent = new Intent();
            intent.putExtra("channel", channel);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void initFirebase() {
        this.database = FirebaseDatabase.getInstance();
        this.channelRef  = this.database.getReference("channels").child(this.channel.id);
        this.messageRef  = this.channelRef.child("messages");
        this.coworkerRef = this.database.getReference("coworkers");
    }

    private void fetchBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.channel = (Channel) bundle.getSerializable("channel");
        }
    }

    private void initUI() {
        setTitle(this.channel.name);
        this.inputView = (MessageInput) findViewById(R.id.input);
        this.inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                sendMessage(input.toString());
                return true;
            }
        });

        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        MessagesListAdapter.HoldersConfig holdersConfig = new MessagesListAdapter.HoldersConfig();
        holdersConfig.setIncoming(CustomIncomingMessageViewHolder.class, R.layout.item_incoming_text_message);
        this.adapter = new MessagesListAdapter<>(OffiMate.currentUser.getUid(), holdersConfig, null);
        this.adapter.setDateHeadersFormatter(this);
        this.messagesList.setAdapter(adapter);
    }

    private void observerMessage() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = Tools.dataSnapshotToMessage(dataSnapshot);
                if (message == null) {
                    final DataSnapshot mDataSnapshot = dataSnapshot;
                    String uid = (String)((Map<String, Object>) dataSnapshot.getValue()).get("uid");
                    coworkerRef.orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot coworkerDataSnapshot) {
                            //Updating coworker
                            Coworker c = Tools.rawToCoworker(
                                    ((Map<String, Object>) coworkerDataSnapshot.getValue())
                                            .entrySet().iterator().next()
                            );
                            OffiMate.coworkers.put(c.id, c);

                            //Adding message
                            Message m = Tools.dataSnapshotToMessage(mDataSnapshot, true);
                            addMessage(m);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Message m = Tools.dataSnapshotToMessage(mDataSnapshot, true);
                            addMessage(m);
                        }
                    });
                } else {
                    addMessage(message);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Tools.showInfoMessage(inputView, "Oops! Backend error fetching messages");
            }

            public void addMessage(Message message) {
                channel.addMessage(message);
                adapter.addToStart(message,true);
            }
        };
        this.messageRef.addChildEventListener(childEventListener);
    }

    @Override
    public String format(Date date) {
        return (new NewDate(date)).getChannelFormat();
    }

    private void sendMessage(String messageText) {
        DatabaseReference newMessageRef = this.messageRef.push();
        Message newMessage = new Message("",
                OffiMate.currentUser.getUid(),
                OffiMate.currentUser.getName(),
                messageText,
                (new NewDate(new Date())).id);
        Map<String, Object> messageValue = newMessage.toMap();
        newMessageRef.setValue(messageValue);
    }

    //MARK: - Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu_info, menu);
        this.barMenuButton = menu.findItem(R.id.info_bar_button_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_bar_button_item:
                this.channelInfoAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void channelInfoAction() {
        //Tools.showInfoMessage(this.inputView, "Channel info");
        Intent intent = new Intent(ChannelActivity.this, ChannelInfoActivity.class);
        intent.putExtra("channel", this.channel);
        startActivityForResult(intent, 1);
    }
}
