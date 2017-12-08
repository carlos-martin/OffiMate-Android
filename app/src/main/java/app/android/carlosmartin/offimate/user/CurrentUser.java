package app.android.carlosmartin.offimate.user;

import android.util.Log;

import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.Office;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by carlos.martin on 01/12/2017.
 */

public class CurrentUser extends RealmObject {
    @PrimaryKey
    private String uid;

    @Required
    private String name;

    @Required
    private String email;

    @Required
    private String password;

    @Required
    private String officeId;

    @Required
    private String officeName;

    /*
     * TODO: Add the Channels list associated to the current user office.
     *
    private RealmList<Channel> channels;
     */

    public CurrentUser() {}

    public CurrentUser(String uid, String name, String email, String password, Office office) {
        this.uid = uid; //OffiMate.CurrentUserID.incrementAndGet().toString();
        this.name = name;
        this.email = email;
        this.password = password;
        this.officeId = office.id;
        this.officeName = office.name;
        //this.channels = new RealmList<Channel>();
    }
    public CurrentUser(CurrentUser currentUser) {
        this.uid = currentUser.getUid();
        this.name = currentUser.getName();
        this.email = currentUser.getEmail();
        this.password = currentUser.getPassword();
        this.officeId = currentUser.getOffice().id;
        this.officeName = currentUser.getOffice().name;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Office getOffice() {
        return new Office(this.officeId, this.officeName);
    }

    public void setOffice(Office office) {
        this.officeId = office.id;
        this.officeName = office.name;
    }

    public void printLog (String tag) {
        Log.d("OffiMate", tag + ": CurrentUser name:"       + this.getName());
        Log.d("OffiMate", tag + ": CurrentUser email:"      + this.getEmail());
        Log.d("OffiMate", tag + ": CurrentUser password:"   + this.getPassword());
        Log.d("OffiMate", tag + ": CurrentUser office:"     + this.getOffice().name);
    }

    /*
     * TODO: All functions need to implement the current user channels associated to the offices
     *
    public void initChannel (List<Channel> channels) {
        //TODO: Init this.channels
    }

    public void cleanChannels () {
        //TODO: Remove all channels list
    }

    public void addChannel (Channel channel, NewDate lastAccess) {
        //TODO: Add a new channel to the channels list
    }

    public void updateChannel (Channel channel, NewDate lastAccess) {
        //TODO: Update the a specific channel already at the channels list
    }

    public void insertChannel (int at, Channel channel, NewDate lastAccess) {
        //TODO: Add a new channel on a specific position at the channels list
    }

    public void removeChannel (int index) {
        //TODO: Remove the channel at index position at the channels list
    }
     */
}
