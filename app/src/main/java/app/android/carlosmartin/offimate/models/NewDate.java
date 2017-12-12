package app.android.carlosmartin.offimate.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by carlos.martin on 17/11/2017.
 */

public class NewDate implements Comparable<NewDate>, Serializable {
    final public Date date;
    final public int year;
    final public int month;
    final public int day;
    final public int hour;
    final public int minutes;
    final public int seconds;
    final public long id;

    public NewDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.date    = date;
        this.year    = calendar.get(Calendar.YEAR);
        this.month   = calendar.get(Calendar.MONTH) + 1;
        this.day     = calendar.get(Calendar.DAY_OF_MONTH);
        this.hour    = calendar.get(Calendar.HOUR_OF_DAY);
        this.minutes = calendar.get(Calendar.MINUTE);
        this.seconds = calendar.get(Calendar.SECOND);
        this.id = (this.year    * 10000000000L) +
                  (this.month   * 100000000) +
                  (this.day     * 1000000) +
                  (this.hour    * 10000) +
                  (this.minutes * 100) +
                  (this.seconds);
    }

    public NewDate(long id) {
        this.id = id;

        long l_year    = id / 10000000000L;
        long l_month   = (id - (l_year * 10000000000L)) / 100000000;
        long l_day     = (id - (l_year * 10000000000L) - (l_month * 100000000)) / 1000000;
        long l_hour    = (id - (l_year * 10000000000L) - (l_month * 100000000) - (l_day * 1000000)) / 10000;
        long l_minutes = (id - (l_year * 10000000000L) - (l_month * 100000000) - (l_day * 1000000) - (l_hour * 10000)) / 100;
        long l_seconds = (id - (l_year * 10000000000L) - (l_month * 100000000) - (l_day * 1000000) - (l_hour * 10000) - (l_minutes * 100));

        this.year    = (int) l_year;
        this.month   = (int) l_month;
        this.day     = (int) l_day;
        this.hour    = (int) l_hour;
        this.minutes = (int) l_minutes;
        this.seconds = (int) l_seconds;

        Calendar calendar = new GregorianCalendar();
        calendar.set(this.year, this.month, this.day, this.hour, this.minutes, this.seconds);
        this.date = calendar.getTime();
    }

    @Override
    public String toString() {
        return this.date.toString();
    }

    @Override
    public int compareTo(@NonNull NewDate newDate) {
        return ((Long) this.id).compareTo((Long) newDate.id);
    }

    @Override
    public boolean equals(@NonNull Object obj) {
        if (!NewDate.class.isAssignableFrom(obj.getClass())) {
            return false;
        } else {
            final NewDate newDate = (NewDate) obj;
            return this.id == newDate.id;
        }
    }
}
