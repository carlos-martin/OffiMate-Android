package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class Office implements Comparable<Office> {
    public final String id;
    public final String name;

    public Office(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull Office office) {
        return this.id.compareTo(office.id);
    }
}
