package app.android.carlosmartin.offimate.activities.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    MessagesList messagesList;
    MessagesListAdapter<Message> adapter;
    MessageInput inputView;

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
        this.inputView = (MessageInput) findViewById(R.id.input);
        inputView.setInputListener(new MessageInput.InputListener() {
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
                Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
                final String   id       = dataSnapshot.getKey();
                final String   senderId = (String)   raw.get("uid");
                final String   text     = (String)   raw.get("text");
                final long     date     = (long)     raw.get("date");

                if (OffiMate.coworkers.get(senderId) == null) {
                    coworkerRef.orderByChild("userId").equalTo(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
                            Map.Entry<String, Object> entry = raw.entrySet().iterator().next();
                            //Updating coworker
                            Coworker coworker = Tools.rawToCoworker(entry);
                            OffiMate.coworkers.put(coworker.id, coworker);
                            //Adding message
                            addMessage(new Message(id, senderId, coworker.name, text, date));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { /*TODO: handler error*/ }
                    });
                } else {
                    Coworker coworker = (Coworker) OffiMate.coworkers.get(senderId);
                    addMessage(new Message(id, senderId, coworker.name, text, date));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) { /*TODO: Handle error*/ }

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
}
