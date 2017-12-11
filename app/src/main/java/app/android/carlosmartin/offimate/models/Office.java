package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by carlos.martin on 30/11/2017.
 */

public class Office implements Comparable<Office>, Serializable {
    public final String id;
    public final String name;

    public Office(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(@NonNull Office office) {
        return this.id.compareTo(office.id);
    }

    @Override
    public boolean equals(@NonNull Object obj) {
        if (!Office.class.isAssignableFrom(obj.getClass())) {
            return false;
        } else {
            final Office office = (Office) obj;
            return this.id.equals(office.id);
        }
    }
}
