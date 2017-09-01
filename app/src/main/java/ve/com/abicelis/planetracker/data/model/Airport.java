package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.TimeZone;

import ve.com.abicelis.planetracker.util.TimezoneUtil;

/**
 * Created by abicelis on 29/8/2017.
 */

@Entity(tableName = "airport",
        indices = {@Index("name"), @Index("iata"), @Index("icao")}
)
public class Airport {

    @PrimaryKey
    @ColumnInfo(name = "airport_id")
    private long mId;                       //Unique OpenFlights identifier for this airport.

    @ColumnInfo(name = "name")
    private String mName;                   //Name of airport. May or may not contain the City name.

    @ColumnInfo(name = "city")
    private String mCity;                   //Main city served by airport. May be spelled differently from Name.

    @ColumnInfo(name = "country")
    private String mCountry;                //Country or territory where airport is located

    @ColumnInfo(name = "iata")
    private String mIata;                   //3-letter IATA code. Null if not assigned/unknown.

    @ColumnInfo(name = "icao")
    private String mIcao;                   //4-letter ICAO code. Null if not assigned.

    @ColumnInfo(name = "latitude")
    private float mLatitude;                //Decimal degrees, usually to six significant digits. Negative is South, positive is North.

    @ColumnInfo(name = "longitude")
    private float mLongitude;               //Decimal degrees, usually to six significant digits. Negative is West, positive is East.

    @ColumnInfo(name = "altitude")
    private int mAltitude;                  //In feet.

    @ColumnInfo(name = "timezone_offset")
    private String mTimezoneOffset;         //Hours offset from UTC. Fractional hours are expressed as decimals, eg. India is 5.5.

    @ColumnInfo(name = "timezone_olson")    //Timezone in Olson format, eg. "America/Los_Angeles"
    private String mTimezoneOlson;          // See https://en.wikipedia.org/wiki/Tz_database

    @ColumnInfo(name = "dst")               //Daylight savings time. One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown)
    private String mDst;                    //See also https://openflights.org/help/time.html



    public Airport(long id, String name, String city, String country, String iata, String icao, float latitude,
                   float longitude, int altitude, String timezoneOffset, String timezoneOlson, String dst) {
        mId = id;
        mName = name;
        mCity = city;
        mCountry = country;
        mIata = iata;
        mIcao = icao;
        mLatitude = latitude;
        mLongitude = longitude;
        mAltitude = altitude;
        mTimezoneOffset = timezoneOffset;
        mTimezoneOlson = timezoneOlson;
        mDst = dst;
    }

    public long getId() {return mId;}
    public String getName() {return mName;}
    public String getCity() {return mCity;}
    public String getCountry() {return mCountry;}
    public String getIata() {return mIata;}
    public String getIcao() {return mIcao;}
    public float getLatitude() {return mLatitude;}
    public float getLongitude() {return mLongitude;}
    public int getAltitude() {return mAltitude;}
    public TimeZone getTimezone() {return TimezoneUtil.getTimeZoneFromString(mTimezoneOffset, mTimezoneOlson);}
    public String getTimezoneOffset() {return mTimezoneOffset;}
    public String getTimezoneOlson() {return mTimezoneOlson;}
    public String getDst() {return mDst;}


    @Override
    public String toString() {
        return    "Airport ID="         + mId
                + ": name="             + (mName != null ? mName : "NULL")
                + ", city="             + (mCity != null ? mCity : "NULL")
                + ", country="          + (mCountry != null ? mCountry : "NULL")
                + ", iata="             + (mIata != null ? mIata : "NULL")
                + ", icao="             + (mIcao != null ? mIcao : "NULL")
                + ", lat="              + mLatitude
                + ", long="             + mLongitude
                + ", altitude="         + mAltitude
                + ", timezone="         + (getTimezone() != null ? getTimezone().getDisplayName() : "NULL")
                + ", timezoneOffset="   + (mTimezoneOffset != null ? mTimezoneOffset : "NULL")
                + ", timezoneOlson="    + (mTimezoneOlson != null ? mTimezoneOlson : "NULL")
                + ", dst="              + (mDst != null ? mDst : "NULL")
                ;
    }
}
