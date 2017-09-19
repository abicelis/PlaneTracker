package ve.com.abicelis.planetracker.util;

import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by abice on 26/4/2017.
 */

public class TimezoneUtil {

    /**
     * This method tries to figure out the timezone, based first on {@code timezoneOlson} (e.g.: 'America/Caracas')
     * and if it cant, it'll try to figure out the timezone based on [@code timezoneOffset}
     * (See {@code TimezoneUtil.getTimezoneStringFromTimezoneOffset()})
     * @param timezoneOffset String like 'America/Caracas'
     * @param timezoneOlson String like 1, -1, 0 or 5.5 (GMT+1, GMT-1, GMT, GMT+5:30)
     * @return  A {@link TimeZone} based on parame
     */
    public static TimeZone getTimeZoneFromString(String timezoneOffset, String timezoneOlson){

        TimeZone tz = TimeZone.getTimeZone(timezoneOlson);
        TimeZone gmt = new SimpleTimeZone(0, "GMT");

        if(tz.equals(gmt)) {    //Since TimeZone.getTimeZone() returns GMT when it is GMT but also when it fails to recognize timezoneOlson, lets check timezoneOffset
            String tz2 = getTimezoneStringFromTimezoneOffset(timezoneOffset);

            if(tz2 == null || tz2.equals("GMT"))    //If we could not determine timeZone based on offset, or timezone is indeed GMT
                return tz;
            else //Offset was recognized, and it isn't GMT!
                return TimeZone.getTimeZone(tz2);
        }

        return tz;
    }

    /**
     * Converts timezone offsets such as -1, 0, 1, and 5.5 to GMT-1, GMT, GMT+1, GMT+5:30
     * Strings recognizable by {@code TimeZone.getTimeZone()}.
     *
     * If {@code timezoneOffset} cannot be recognized, this method returns null
     */
    public static String getTimezoneStringFromTimezoneOffset(String timezoneOffset) {

        if(timezoneOffset.trim().equals("0"))
            return "GMT";

        Pattern pattern = Pattern.compile("^-?\\d{1,2}(\\.5)?$");

        if(pattern.matcher(timezoneOffset).matches()) {

            boolean negative = timezoneOffset.contains("-");
            boolean halfHour = timezoneOffset.contains(".5");

            String digit = timezoneOffset.split("\\.")[0].replaceAll("-"," ");
            digit = digit.replaceAll(" ","");
            digit = digit.replaceAll("-","");
            digit = digit.replaceAll("\\+","");

            return "GMT" + " " + (negative? "-" : "+") + digit + (halfHour ? ":30" : "");
        }

        return null;
    }
}
