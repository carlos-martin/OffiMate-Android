package app.android.carlosmartin.offimate.application;

import android.app.Application;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.android.carlosmartin.offimate.user.CurrentUser;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by carlos.martin on 04/12/2017.
 */

public class OffiMate extends Application {

    /* Static variable where we will host the current user data */
    public static CurrentUser currentUser;
    public static FirebaseUser firebaseUser;
    public static FirebaseAuth mAuth;

    /* Connexion with our local data base */
    public static Realm realm;

    private RealmResults<CurrentUser> results;


    @Override
    public void onCreate() {
        Toast.makeText(this, "WELCOME TO OFFIMATE!", Toast.LENGTH_LONG).show();
        this.firebaseAuth();
        this.realmConfigurationSepUp();
        this.realmFetchingData();
        super.onCreate();
    }

    private void firebaseAuth() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser =  this.mAuth.getCurrentUser();
    }

    private void realmConfigurationSepUp() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }

    private void realmFetchingData() {
        this.realm = Realm.getDefaultInstance();

        //Fetching currentUser
        this.results = this.realm.where(CurrentUser.class).findAll();
        if (this.results.size() > 0) {
            this.currentUser = this.results.first();
        } else {
            this.currentUser = null;
        }

        /*
         * TODO: Take care of this
         *
         * this.realm.close();
         *
         */
    }
}