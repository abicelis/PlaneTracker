package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;
import java.util.TimeZone;

import ve.com.abicelis.planetracker.application.Constants;

/**
 * Created by abicelis on 2/9/2017.
 */

public class CalendarConverter {

    @TypeConverter
    public static Calendar toCalendar(String calendarString) {
        if(calendarString == null)
            return null;

        String[] parts = calendarString.split(Constants.ROOM_DATABASE_CALENDAR_CONVERTER_SEPARATOR);
        if(parts.length != 2)
            return null;
        else {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(parts[1]));
            cal.setTimeInMillis(Long.parseLong(parts[0]));
            return cal;
        }
    }

    @TypeConverter
    public static String toStr(Calendar calendar) {
        if(calendar == null)
            return null;

        return calendar.getTimeInMillis() + Constants.ROOM_DATABASE_CALENDAR_CONVERTER_SEPARATOR + calendar.getTimeZone().getID();
    }
}