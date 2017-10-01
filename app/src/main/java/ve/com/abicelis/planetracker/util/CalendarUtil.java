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
        SimpleDateFormat df;
        if(calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR))
            df = new SimpleDateFormat("MMM dd", Locale.getDefault());
        else
            df = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

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

    public static String getCuteTimeStringBetweenCalendars(@NonNull Calendar start, @NonNull Calendar end) {
        long startMillis = start.getTimeInMillis();
        long endMillis = end.getTimeInMillis();

        //endMillis +=86400;

        int SECONDS_IN_DAY = 86400;
        int SECONDS_IN_HOUR = 3600;
        int SECONDS_IN_MINUTE = 60;

        long seconds = Math.abs(endMillis - startMillis)/1000;

        int daysBetween = (int) (seconds / SECONDS_IN_DAY);
        seconds = seconds%SECONDS_IN_DAY;

        int hoursBetween = (int) (seconds / SECONDS_IN_HOUR);
        seconds = seconds%SECONDS_IN_HOUR;

        int minutesBetween = (int) (seconds / SECONDS_IN_MINUTE);

        String out = (daysBetween > 0 ? daysBetween + "d " : "");
        out += (hoursBetween > 0 ? hoursBetween + "h " : "");
        out += (minutesBetween > 0 ? minutesBetween + "m" : "");

        return out;
    }


//
//
//    public static int compareYearMonthDay(Calendar calendar1, Calendar calendar2) {
//        if(calendar1 == null)
//            return -1;
//        if (calendar2 == null)
//            return 1;
//
//        Calendar calendarFirst = getNewInstanceZeroedCalendar();
//        Calendar calendarSecond = getNewInstanceZeroedCalendar();
//        calendarFirst.set(Calendar.YEAR, calendar1.get(Calendar.YEAR));
//        calendarFirst.set(Calendar.DAY_OF_YEAR, calendar1.get(Calendar.DAY_OF_YEAR));
//        calendarFirst.setTimeZone(calendar1.getTimeZone());
//        calendarSecond.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
//        calendarSecond.set(Calendar.DAY_OF_YEAR, calendar2.get(Calendar.DAY_OF_YEAR));
//        calendarSecond.setTimeZone(calendar2.getTimeZone());
//
//        if(calendarFirst.after(calendarSecond))
//            return -1;
//        if (calendarFirst.before(calendarSecond))
//            return 1;
//        else return 0;
//    }
}
