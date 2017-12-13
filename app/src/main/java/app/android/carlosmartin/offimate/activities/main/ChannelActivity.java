package app.android.carlosmartin.offimate.activities.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Map;

import app.android.carlosmartin.offimate.R;
import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.Channel;
import app.android.carlosmartin.offimate.models.Message;
import app.android.carlosmartin.offimate.user.CurrentUser;

public class ChannelActivity extends AppCompatActivity {

    //UI
    MessagesList messagesList;
    MessagesListAdapter<Message> adapter;

    //Firebase
    private FirebaseDatabase database;
    private DatabaseReference channelRef;
    private DatabaseReference messageRef;
    private DatabaseReference coworkerRef;

    //DataSource
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        this.fetchBundle();
        this.initFirebase();
        this.initUI();
        this.observerMessage();
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
        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        this.adapter = new MessagesListAdapter<>(OffiMate.currentUser.getUid(), null);
        this.messagesList.setAdapter(adapter);
    }

    private void observerMessage() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
                final String id = dataSnapshot.getKey();
                final String senderId = (String) raw.get("uid");
                final String text     = (String) raw.get("text");
                final long   date     = (long)   raw.get("date");
                coworkerRef.orderByChild("userId").equalTo(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
                        Map.Entry<String, Object> entry = raw.entrySet().iterator().next();
                        Map<String, String> values = (Map<String, String>) entry.getValue();
                        String name = values.get("name");
                        Message message = new Message(id, senderId, name, text, date);
                        channel.addMessage(message);
                        adapter.addToStart(message, true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { /*TODO: handler error*/ }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) { /*TODO: Handle error*/ }
        };
        this.messageRef.addChildEventListener(childEventListener);
    }

}
