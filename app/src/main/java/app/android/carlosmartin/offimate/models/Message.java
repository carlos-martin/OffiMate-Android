package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class Message implements Comparable<Message> {
    public final String id;
    public final String senderId;
    public final String text;
    public final int date;

    public Message(String id, String senderId, String text, int date) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.date = date;
    }

    public String toString() {
        return "Message:\n" +
                "   ├── id:        " + this.id + "\n" +
                "   ├── senderId:  " + this.senderId + "\n" +
                "   ├── date:      " + this.date + "\n" +
                "   └── text:      " + this.text + "\n";
    }

    @Override
    public int compareTo(@NonNull Message message) {
        return this.date - message.date;
    }
}
