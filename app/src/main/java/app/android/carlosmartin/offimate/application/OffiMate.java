package app.android.carlosmartin.offimate.application;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

import app.android.carlosmartin.offimate.user.CurrentUser;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by carlos.martin on 04/12/2017.
 */

public class OffiMate extends Application {

    /* Variable to generate Current User IDs
     *
     * If CurrentUserID == 0;   we don't have any user created
     * Else;                    we have at least one user created
     *
     */
    public static AtomicInteger CurrentUserID = new AtomicInteger();

    /* Static variable where we will host the current user data */
    public static CurrentUser currentUser;

    /* Connexion with our local data base */
    private Realm realm;


    @Override
    public void onCreate() {

        Toast.makeText(this, "WELCOME TO OFFIMATE!", Toast.LENGTH_LONG).show();

        this.realmConfigurationSepUp();
        this.realmFetchingData();

        Log.d("OffiMate", "CurrentUserID = " + CurrentUserID.toString());

        super.onCreate();

    }

    private void realmConfigurationSepUp() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }

    private void realmFetchingData() {
        this.realm = Realm.getDefaultInstance();

        //Fetching CurrenUserID
        this.CurrentUserID = getIdByTable(realm, CurrentUser.class);

        //Fetching currentUser
        this.currentUser = this.realm.where(CurrentUser.class).findAll().first();
        Log.d("OffiMate", this.currentUser.toString());

        this.realm.close();
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass) {
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }
}
