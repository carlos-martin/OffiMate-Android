package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class Channel implements Comparable<Channel>, Serializable {
    public String id;
    public String name;
    public String creator;
    public ArrayList<Message> message;

    public Channel(String id, String name, String creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.message = new ArrayList<Message>();
    }

    public Channel(String id, String name, String creator, ArrayList<Message> message) {
        this(id, name, creator);
        this.message = message;
    }

    public Map<String, String> toMap(String officeId) {
        HashMap<String, String> result = new HashMap<>();
        result.put("creator",  creator);
        result.put("name",     name);
        result.put("officeId", officeId);
        return result;
    }

    public void addMessage(Message message) {
        this.message.add(message);
        Collections.sort(this.message, new Comparator<Message>() {
            @Override
            public int compare(Message message1, Message message2) {
                return message1.compareTo(message2);
            }
        });
    }

    @Override
    public String toString() {
        return "Channel:\n" +
                "├── id:      " + this.id + "\n" +
                "├── name:    " + this.name + "\n" +
                "├── creator: " + this.creator + "\n" +
                "└── num:     " + this.message.size() + "\n";
    }

    @Override
    public int compareTo(@NonNull Channel channel) {
        return this.id.compareTo(channel.id);
    }

    @Override
    public boolean equals(@NonNull Object obj) {
        if (!Channel.class.isAssignableFrom(obj.getClass())) {
            return false;
        } else {
            final Channel channel = (Channel) obj;
            return this.id.equals(channel.id);
        }
    }
}
