package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class Message implements Comparable<Message>, IMessage {
    public final String id;
    public final String senderId;
    public final String name;
    public final String text;
    public final long date;

    public Message(String id, String senderId, String name, String text, long date) {
        this.id = id;
        this.senderId = senderId;
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", this.date);
        result.put("text", this.text);
        result.put("uid", this.senderId);
        return result;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public IUser getUser() {
        return new IUser() {
            @Override
            public String getId() {
                return senderId;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getAvatar() {
                return null;
            }
        };
    }

    @Override
    public Date getCreatedAt() {
        return (new NewDate(date)).date;
    }

    @Override
    public String toString() {
        return "Message:\n" +
                "   ├── id:        " + this.id + "\n" +
                "   ├── senderId:  " + this.senderId + "\n" +
                "   ├── date:      " + this.date + "\n" +
                "   └── text:      " + this.text + "\n";
    }

    @Override
    public int compareTo(@NonNull Message message) {
        return (int)(this.date - message.date);
    }

    @Override
    public boolean equals(@NonNull Object obj) {
        if (!Message.class.isAssignableFrom(obj.getClass())) {
            return false;
        } else {
            final Message message = (Message) obj;
            return this.id.equals(message.id);
        }
    }
}
