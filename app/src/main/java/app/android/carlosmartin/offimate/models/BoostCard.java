package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BoostCard implements Comparable<BoostCard>, Serializable {
    public final String id;
    public final String senderId;
    public final String receiverId;
    public final BoostCardType type;
    public final String header;
    public final String message;
    public final NewDate date;

    public BoostCard (String id, String senderId, String receiverId, BoostCardType type, String header, String message, NewDate date) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.header = header;
        this.message = message;
        this.date = date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        String stype;
        if (this.type == BoostCardType.EXECUTION) {
            stype = "execution";
        } else {
            stype = "passion";
        }
        result.put("date", this.date.id);
        result.put("header", this.header);
        result.put("message", this.message);
        result.put("receiverId", this.receiverId);
        result.put("senderId", this.senderId);
        result.put("type", stype);
        result.put("unread", true);
        return result;
    }

    @Override
    public String toString() {
        return "BoostCard:\n" +
                "├── date:       " + this.date       + "\n" +
                "├── id:         " + this.id         + "\n" +
                "├── senderId:   " + this.senderId   + "\n" +
                "├── receiverId: " + this.receiverId + "\n" +
                "├── type:       " + this.type       + "\n" +
                "├── header:     " + this.header     + "\n" +
                "└── message:    " + this.message    + "\n";
    }

    @Override
    public int compareTo(@NonNull BoostCard boostCard) {
        return this.date.compareTo(boostCard.date);
    }
}
