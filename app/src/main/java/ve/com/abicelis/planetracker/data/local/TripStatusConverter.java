package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;
import java.util.TimeZone;

import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.model.Trip;

/**
 * Created by abicelis on 2/9/2017.
 */

public class TripStatusConverter {

    @TypeConverter
    public static Trip.TripStatus toTripStatus(String tripStatusString) {
        if(tripStatusString == null)
            return null;
        return Trip.TripStatus.valueOf(tripStatusString);
    }

    @TypeConverter
    public static String toStr(Trip.TripStatus tripStatus) {
        if(tripStatus == null)
            return null;

        return tripStatus.name();
    }
}