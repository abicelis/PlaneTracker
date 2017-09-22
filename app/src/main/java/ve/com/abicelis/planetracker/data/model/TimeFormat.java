package ve.com.abicelis.planetracker.data.model;

import android.support.annotation.StringRes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;

/**
 * Created by abicelis on 4/9/2017.
 */

public enum TimeFormat {
    HOUR_24(R.string.time_format_24) {
        @Override
        public String formatCalendar(Calendar calendar) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            formatter.setTimeZone(calendar.getTimeZone());
            String s = formatter.format(calendar.getTime());

            return formatter.format(calendar.getTime());
        }
    },
    HOUR_12(R.string.time_format_12) {
        @Override
        public String formatCalendar(Calendar calendar) {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            formatter.setTimeZone(calendar.getTimeZone());

            return formatter.format(calendar.getTime());
        }
    }
    ;


    private @StringRes int friendlyName;
    public abstract String formatCalendar(Calendar calendar);


    TimeFormat(@StringRes int friendlyName){
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return PlaneTrackerApplication.getAppContext().getString(friendlyName);
    }

    public static List<String> getFriendlyNames() {
        List<String> friendlyValues = new ArrayList<>();
        for (TimeFormat x : values()) {
            friendlyValues.add(PlaneTrackerApplication.getAppContext().getString(x.friendlyName));
        }
        return friendlyValues;
    }
}
