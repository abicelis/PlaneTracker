package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.util.Calendar;

import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 26/8/2017.
 */
@Entity(tableName = "flight")
public class Flight {

    @ColumnInfo(name = "flight_id")
    private long mId;

    @ColumnInfo(name = "flight_aware_id")
    private String mFlightAwareId;

    @ColumnInfo(name = "callsign")
    private String mCallsign;                //CMP317       Airline 3-letter ICAO Flight #

    @ColumnInfo(name = "origin")
    private Airport mOrigin;

    @ColumnInfo(name = "destination")
    private Airport mDestination;

    @ColumnInfo(name = "airline")
    private Airline mAirline;

    @ColumnInfo(name = "departure")
    private Calendar mDeparture;

    @ColumnInfo(name = "arrival")
    private Calendar mArrival;

    @ColumnInfo(name = "aircraft_model")
    private String mAircraftModel;           //E190      Embraer 190

    @Ignore
    private double latitude;

    @Ignore
    private double longitude;

    @Ignore
    private int altitude;                   //In meters

    @Ignore
    private float speed;                    //In knots

    @Ignore
    private float verticalSpeed;            //In ft/m

    @Ignore
    private float heading;                  //In degrees


    public Flight(String flightAwareId, String callsign, Airport origin, Airport destination, Airline airline, Calendar departure, Calendar arrival, String aircraftModel) {
        mFlightAwareId = flightAwareId;
        mCallsign = callsign;
        mOrigin = origin;
        mDestination = destination;
        mAirline = airline;
        mDeparture = departure;
        mArrival = arrival;
        mAircraftModel = aircraftModel;
    }

    public String getFlightAwareId() {return mFlightAwareId;}
    public String getCallsign() {return mCallsign;}
    public Airport getOrigin() {return mOrigin;}
    public Airport getDestination() {return mDestination;}
    public Airline getAirline() {return mAirline;}
    public Calendar getDeparture() {return mDeparture;}
    public Calendar getArrival() {return mArrival;}
    public String getAircraftModel() {return mAircraftModel;}



    @Override
    public String toString() {
        return    "Flight ID="      + mId
                + "\n   flightAwareID=" + (mFlightAwareId != null ? mFlightAwareId : "NULL")
                + "\n   callsign="      + (mCallsign != null ? mCallsign : "NULL")
                + "\n   origin="        + (mOrigin != null ? mOrigin.toString() : "NULL")
                + "\n   destination="   + (mDestination != null ? mDestination.toString() : "NULL")
                + "\n   airline="       + (mAirline != null ? mAirline.toString() : "NULL")
                + "\n   departure="     + (mDeparture != null ? CalendarUtil.getStringDateFromCalendar(mDeparture) : "NULL")
                + "\n   arrival="       + (mArrival != null ? CalendarUtil.getStringDateFromCalendar(mArrival) : "NULL")
                + "\n   aircraftModel=" + (mAircraftModel != null ? mAircraftModel : "NULL")
                ;
    }
}
