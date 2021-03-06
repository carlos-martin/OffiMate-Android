package app.android.carlosmartin.offimate.helpers;

import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.google.firebase.database.DataSnapshot;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.BoostCard;
import app.android.carlosmartin.offimate.models.BoostCardType;
import app.android.carlosmartin.offimate.models.Channel;
import app.android.carlosmartin.offimate.models.Coworker;
import app.android.carlosmartin.offimate.models.Message;
import app.android.carlosmartin.offimate.models.NewDate;
import app.android.carlosmartin.offimate.models.Office;

/**
 * Created by carlos.martin on 30/11/2017.
 */

public class Tools {
    public static boolean isValidEmail(CharSequence target) {
        boolean valid = (!TextUtils.isEmpty(target) &&
                Patterns.EMAIL_ADDRESS.matcher(target).matches());

        if (valid) {
            String[] splited = target.toString().split("@");
            String domain = splited[1];
            Log.d("DOMAIN", domain);
            valid = (domain.equals("sigma.se") || domain.equals("sigmatechnology.se"));
        }

        return valid;
    }

    public static boolean isValidPassword(final String password) {
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static void showInfoMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public static Office rawToOffice (Map.Entry<String, Object> rawEntry) {
        Map<String, String> officeMap = (Map<String, String>) rawEntry.getValue();
        String id   = rawEntry.getKey();
        String name = officeMap.get("name");
        return new Office(id, name);
    }

    public static Coworker rawToCoworker (Map.Entry<String, Object> rawEntry) {
        Map<String, String> coworkerMap = (Map<String, String>) rawEntry.getValue();
        String id       = rawEntry.getKey();
        String email    = coworkerMap.get("email");
        String name     = coworkerMap.get("name");
        String userId   = coworkerMap.get("userId");
        String officeId = coworkerMap.get("officeId");
        Office office;
        if (OffiMate.offices.get(officeId) == null) {
            office = new Office(officeId, "Unknown Office");
        } else {
            office = (Office) OffiMate.offices.get(officeId);
        }
        return new Coworker(id, userId, email, name, office);
    }

    public static Coworker dataSnapshotToCoworker (DataSnapshot dataSnapshot) {
        Map<String, String> raw = (Map<String, String>) dataSnapshot.getValue();
        String   id       = dataSnapshot.getKey();
        String   email    = raw.get("email");
        String   name     = raw.get("name");
        String   userId   = raw.get("userId");
        String   officeId = raw.get("officeId");
        Office   office;
        if (OffiMate.offices.get(officeId) != null) {
            office = (Office) OffiMate.offices.get(officeId);
        } else {
            office = new Office(officeId, "Unknown Office");
        }
        return new Coworker(id, userId, email, name, office);
    }

    public static BoostCard dataSnapshotToBoostCard (DataSnapshot dataSnapshot) {
        Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();

        String  id         = dataSnapshot.getKey();
        NewDate date       = new NewDate((long) raw.get("date"));
        String  header     = (String) raw.get("header");
        String  message    = (String) raw.get("message");
        String  receiverId = (String) raw.get("receiverId");
        String  senderId   = (String) raw.get("senderId");
        String  stype      = (String) raw.get("type");
        BoostCardType type = (stype.equals("execution") ? BoostCardType.EXECUTION : BoostCardType.PASSION);
        //boolean unread     = (boolean) raw.get("unread");

        return new BoostCard(id, senderId, receiverId, type, header, message, date);
    }

    public static Channel dataSnapshotToChannel (DataSnapshot dataSnapshot) {
        return null;
    }

    public static Message dataSnapshotToMessage (DataSnapshot dataSnapshot) {
        Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
        String id       = dataSnapshot.getKey();
        String senderId = (String) raw.get("uid");
        String text     = (String) raw.get("text");
        long   date     = (long)   raw.get("date");
        if (OffiMate.coworkers.get(senderId) == null) {
            return null;
        } else {
            Coworker coworker = (Coworker) OffiMate.coworkers.get(senderId);
            return new Message(id, senderId, coworker.name, text, date);
        }
    }

    public static Message dataSnapshotToMessage (DataSnapshot dataSnapshot, boolean weak) {
        if (!weak) {
            return dataSnapshotToMessage(dataSnapshot);
        } else {
            Map<String, Object> raw = (Map<String, Object>) dataSnapshot.getValue();
            String id       = dataSnapshot.getKey();
            String senderId = (String) raw.get("uid");
            String text     = (String) raw.get("text");
            long   date     = (long)   raw.get("date");
            if (OffiMate.coworkers.get(senderId) == null) {
                return new Message(id, senderId, "unknown user", text, date);
            } else {
                Coworker coworker = (Coworker) OffiMate.coworkers.get(senderId);
                return new Message(id, senderId, coworker.name, text, date);
            }
        }
    }

    public static Message mapEntryToMessage (Map.Entry<String, Object> messageRaw) {
        Map<String, Object> messageMap = (Map<String, Object>) messageRaw.getValue();
        String id       = messageRaw.getKey();
        String senderId = (String) messageMap.get("uid");
        String text     = (String) messageMap.get("text");
        long   date     = (long)   messageMap.get("date");
        if (OffiMate.coworkers.get(senderId) == null) {
            return null;
        } else {
            Coworker coworker = (Coworker) OffiMate.coworkers.get(senderId);
            return new Message(id, senderId, coworker.name, text, date);
        }
    }

    public static Message mapEntryToMessage (Map.Entry<String, Object> messageRaw, boolean weak) {
        if (!weak) {
            return mapEntryToMessage(messageRaw);
        } else {
            Map<String, Object> messageMap = (Map<String, Object>) messageRaw.getValue();
            String id       = messageRaw.getKey();
            String senderId = (String) messageMap.get("uid");
            String text     = (String) messageMap.get("text");
            long   date     = (long)   messageMap.get("date");
            if (OffiMate.coworkers.get(senderId) == null) {
                return new Message(id, senderId, "unknown user", text, date);
            } else {
                Coworker coworker = (Coworker) OffiMate.coworkers.get(senderId);
                return new Message(id, senderId, coworker.name, text, date);
            }
        }
    }
}
