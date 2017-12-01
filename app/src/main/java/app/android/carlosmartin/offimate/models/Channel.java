package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class Channel implements Comparable<Channel> {
    public final String id;
    public final String name;
    public final String creator;
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
}
