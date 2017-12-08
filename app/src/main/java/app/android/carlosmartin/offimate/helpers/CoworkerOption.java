package app.android.carlosmartin.offimate.helpers;

import android.support.annotation.DrawableRes;

/**
 * Created by carlos.martin on 08/12/2017.
 */

public class CoworkerOption {
    @DrawableRes
    private int iconRes;
    private String title;
    public boolean hasArrow;

    public CoworkerOption(@DrawableRes int iconRes, String title, boolean hasArrow) {
        this.iconRes = iconRes;
        this.title = title;
        this.hasArrow = hasArrow;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(@DrawableRes int iconRes) {
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
