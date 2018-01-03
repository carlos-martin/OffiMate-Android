package app.android.carlosmartin.offimate.helpers;

import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.android.carlosmartin.offimate.application.OffiMate;
import app.android.carlosmartin.offimate.models.Coworker;
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
            office = new Office(officeId, "unknown");
        } else {
            office = (Office) OffiMate.offices.get(officeId);
        }

        return new Coworker(id, userId, email, name, office);
    }
}
