package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

public class BoostCard implements Comparable<BoostCard> {
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

    public String toString() {
        return "BoostCard:\n" +
                "├── date:        " + this.date + "\n" +
                "├── id:          " + this.id + "\n" +
                "├── senderId:    " + this.senderId + "\n" +
                "├── receiverId:  " + this.receiverId + "\n" +
                "├── type:        " + this.type + "\n" +
                "├── header:      " + this.header + "\n" +
                "└── message:     " + this.message + "\n";
    }

    @Override
    public int compareTo(@NonNull BoostCard boostCard) {
        /*
        TODO: depends on the NewDate class
        return this.date.compareTo(boostCard.date);
        */
        return 0;
    }
}
