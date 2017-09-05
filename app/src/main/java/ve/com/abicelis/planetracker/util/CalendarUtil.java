package ve.com.abicelis.planetracker.util;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by abice on 26/4/2017.
 */

public class CalendarUtil {

    /**
     * Returns a string representation of the calendar's date
     * TAKING INTO ACCOUNT the calendar's timezone!!!!
     */
    public static String getStringDateFromCalendar(Calendar calendar) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm Z", Locale.getDefault());
        df.setTimeZone(calendar.getTimeZone());

        return df.format(calendar.getTime()) + " " + calendar.getTimeZone().getID();
    }

    /**
     * Returns a cute string like "October 10", "January 25"
     */
    public static String getCuteStringDateFromCalendar(Calendar calendar) {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd", Locale.getDefault());
        df.setTimeZone(calendar.getTimeZone());

        return df.format(calendar.getTime());
    }

    public static Calendar getZeroedCalendarFromYearMonthDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    public static Calendar getNewInstanceZeroedCalendarForTimezone(TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar getNewInstanceZeroedCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static void copyCalendar(@NonNull Calendar copyFrom, Calendar copyTo) {

        if(copyTo == null)
            copyTo = getNewInstanceZeroedCalendarForTimezone(copyFrom.getTimeZone());
        else
            copyTo.setTimeZone(copyFrom.getTimeZone());

        copyTo.setTimeInMillis(copyFrom.getTimeInMillis());
    }

}
