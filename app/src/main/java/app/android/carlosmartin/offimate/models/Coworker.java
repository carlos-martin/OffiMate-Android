package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class Coworker implements Comparable<Coworker>, Serializable {
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

    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("email",     this.email);
        result.put("name",      this.name);
        result.put("officeId",  this.office.id);
        result.put("userId",    this.uid);
        return result;
    }

    @Override
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

    @Override
    public boolean equals(@NonNull Object obj) {
        if (!Coworker.class.isAssignableFrom(obj.getClass())) {
            return false;
        } else {
            final Coworker coworker = (Coworker) obj;
            return this.id.equals(coworker.id);
        }
    }
}
