package ve.com.abicelis.planetracker.data.model;

import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;

/**
 * Created by abicelis on 4/9/2017.
 */

public enum TimeFormat {
    HOUR_24(R.string.time_format_24),
    HOUR_12(R.string.time_format_12)
    ;


    private @StringRes int friendlyName;


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
