package app.android.carlosmartin.offimate.Models;

import android.support.annotation.NonNull;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class Coworker implements Comparable<Coworker> {
    public final String id;
    public final String uid;
    public final String email;
    public final String name;
    public final Office office;

    public Coworker(String id, String uid, String email, String name, Office office){
        this.id = id;
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.office = office;
    }

    public String toString() {
        return "Coworker:\n" +
                "├── id:     " + this.id + "\n" +
                "├── uid:    " + this.uid + "\n" +
                "├── name:   " + this.name + "\n" +
                "├── email:  " + this.email + "\n" +
                "└── office: " + this.office + "\n";
    }


    @Override
    public int compareTo(@NonNull Coworker coworker) {
        return this.id.compareTo(coworker.id);
    }
}
