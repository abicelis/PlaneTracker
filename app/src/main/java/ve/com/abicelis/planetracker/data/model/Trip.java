package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 26/8/2017.
 */

@Entity(tableName = "trip")
public class Trip implements Comparable<Trip>{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trip_id")
    private long mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "image")
    private byte[] mImage;


    @Ignore
    private List<Flight> mFlights = new ArrayList<>();

    public Trip() {}    //Room complains otherwise, I wish I knew why.

    public Trip(String name, byte[] image, List<Flight> flights) {
        mName = name;
        mImage = image;
        mFlights = flights;
    }

    public long getId() {return mId;}
    public String getName() {return mName;}
    public byte[] getImage() {return mImage;}
    public TripStatus getStatus() {
        if(mFlights == null || mFlights.size() == 0)
            return TripStatus.UPCOMING;

        //TODO check if this comparison takes Timestamps into account
        Calendar start = mFlights.get(0).getDeparture();
        Calendar end = mFlights.get(mFlights.size()-1).getArrival();
        Calendar today = Calendar.getInstance();

        if (today.compareTo(start) < 0)
            return TripStatus.UPCOMING;
        else if(today.compareTo(end) < 0)
            return TripStatus.CURRENT;

        return TripStatus.PAST;
    }
    public List<Flight> getFlights() {return mFlights;}

    public String getInfo(Context context) {
        if(mFlights.size() == 0)
            return context.getString(R.string.activity_home_trip_item_invalid_info);

        String start = CalendarUtil.getCuteStringDateFromCalendar(mFlights.get(0).getDeparture());
        String end = CalendarUtil.getCuteStringDateFromCalendar(mFlights.get(mFlights.size()-1).getArrival());

        switch (getStatus()) {
            case CURRENT:
                return String.format(Locale.getDefault(), context.getString(R.string.activity_home_trip_item_current_info), end);
            case UPCOMING:
                return String.format(Locale.getDefault(), context.getString(R.string.activity_home_trip_item_upcoming_info), start);
            case PAST:
                return String.format(Locale.getDefault(), context.getString(R.string.activity_home_trip_item_past_info), start, end);
            default:
                return context.getString(R.string.activity_home_trip_item_invalid_info);
        }
    }

    public void setId(long mId) {this.mId = mId;}
    public void setName(String mName) {this.mName = mName;}
    public void setImage(byte[] mImage) {this.mImage = mImage;}
    public void setFlights(List<Flight> mFlights) {this.mFlights = mFlights;}

    @Override
    public String toString() {
        String out =    "Trip ID="      + mId
                + "\n   name="          + (mName != null ? mName : "NULL")
                + "\n   image="         + (mImage != null  ? "Lenght:" + mImage.length : "NULL")
                + "\n   status="        + getStatus().name()
                + "\n   flights=";

        if(mFlights != null) {
            for (Flight f : mFlights)
                out += "\n   Flight=" + f.toString();
        } else
            out = " NULL";

        return out;
    }

    @Override
    public int compareTo(@NonNull Trip trip) {
        if (this.mFlights.size() == 0)
            return 1;
        if (trip.mFlights.size() == 0)
            return -1;
        return this.mFlights.get(0).compareTo(trip.mFlights.get(0));
    }
}
