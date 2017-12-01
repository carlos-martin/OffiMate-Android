package app.android.carlosmartin.offimate.user;

import android.support.annotation.NonNull;

import app.android.carlosmartin.offimate.models.Office;

/**
 * Created by carlos.martin on 01/12/2017.
 */

public class CurrentUser {
    public static String name;
    public static String email;
    public static String password;
    public static Office office;

    public static boolean isInit() {
        return (name != null && email != null && password != null && office != null);
    }
}
