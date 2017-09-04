package ve.com.abicelis.planetracker.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import javax.inject.Inject;

import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.model.DateFormat;
import ve.com.abicelis.planetracker.data.model.TimeFormat;

/**
 * Created by abice on 1/4/2017.
 */

public class SharedPreferenceHelper {

    private SharedPreferences mSharedPreferences;

    @Inject
    public SharedPreferenceHelper(Context applicationContext) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }


    /* TIME FORMAT */
    public TimeFormat getTimeFormat() {
        String value = mSharedPreferences.getString(Constants.SHARED_PREFERENCE_TIME_FORMAT, null);
        TimeFormat pref;
        try {
            pref = TimeFormat.valueOf(value);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Timber.d("getTimeFormat() found null, setting HOUR_24");
            pref = TimeFormat.HOUR_24;
            setTimeFormat(pref);
        }

        return pref;
    }
    public void setTimeFormat(TimeFormat value) {
        mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_TIME_FORMAT, value.name()).apply();
    }

    /* DATE FORMAT */
    public DateFormat getDateFormat() {
        String value = mSharedPreferences.getString(Constants.SHARED_PREFERENCE_DATE_FORMAT, null);
        DateFormat pref;
        try {
            pref = DateFormat.valueOf(value);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Timber.d("getDateFormat() found null, setting PRETTY_DATE");
            pref = DateFormat.PRETTY_DATE;
            setDateFormat(pref);
        }

        return pref;
    }
    public void setDateFormat(DateFormat value) {
        mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_DATE_FORMAT, value.name()).apply();
    }

    /* RECENT AIRPORTS */
    public long[] getRecentAirportIds() {
        String recentAirportsStr = mSharedPreferences.getString(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS, null);

        if(recentAirportsStr != null) {
            String[] stringIds = recentAirportsStr.split(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR);
            long[] longIds = new long[stringIds.length];

            for (int i = 0; i < stringIds.length ; i++)
                longIds[i] = Long.valueOf(stringIds[i]);
            return longIds;
        }

        return null;    //TODO: or maybe return new long[0]; ?
    }
    public void setAirportAsRecent(long id) {
        String recentAirportsStr = mSharedPreferences.getString(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS, null);

        if(recentAirportsStr == null) {
            mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS, String.valueOf(id)).apply();
            return;
        }

        String[] stringIds = recentAirportsStr.split(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR);
        if (stringIds.length < Constants.SHARED_PREFERENCE_MAX_RECENT_AIRPORTS_AIRLINES) {
            mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS, recentAirportsStr
                            + Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR
                            + String.valueOf(id)).apply();
        } else {
            recentAirportsStr = recentAirportsStr.substring(
                    recentAirportsStr.indexOf(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR)+1, recentAirportsStr.length());
            recentAirportsStr += Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR + String.valueOf(id);
            mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS, recentAirportsStr).apply();
        }
    }


    /* RECENT AIRLINES */
    public long[] getRecentAirlineIds() {
        String recentAirlinesStr = mSharedPreferences.getString(Constants.SHARED_PREFERENCE_RECENT_AIRLINES, null);

        if(recentAirlinesStr != null) {
            String[] stringIds = recentAirlinesStr.split(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR);
            long[] longIds = new long[stringIds.length];

            for (int i = 0; i < stringIds.length ; i++)
                longIds[i] = Long.valueOf(stringIds[i]);
            return longIds;
        }

        return null;    //TODO: or maybe return new long[0]; ?
    }
    public void setAirlineAsRecent(long id) {
        String recentAirlinesStr = mSharedPreferences.getString(Constants.SHARED_PREFERENCE_RECENT_AIRLINES, null);

        if(recentAirlinesStr == null) {
            mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_RECENT_AIRLINES, String.valueOf(id)).apply();
            return;
        }

        String[] stringIds = recentAirlinesStr.split(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR);
        if (stringIds.length < Constants.SHARED_PREFERENCE_MAX_RECENT_AIRPORTS_AIRLINES) {
            mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_RECENT_AIRLINES, recentAirlinesStr
                            + Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR
                            + String.valueOf(id)).apply();
        } else {
            recentAirlinesStr = recentAirlinesStr.substring(
                    recentAirlinesStr.indexOf(Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR)+1, recentAirlinesStr.length());
            recentAirlinesStr += Constants.SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR + String.valueOf(id);
            mSharedPreferences.edit().putString(Constants.SHARED_PREFERENCE_RECENT_AIRLINES, recentAirlinesStr).apply();
        }
    }
}
