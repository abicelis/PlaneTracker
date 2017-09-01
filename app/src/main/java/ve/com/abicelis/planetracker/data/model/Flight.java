package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.util.Calendar;

/**
 * Created by abicelis on 26/8/2017.
 */
@Entity(tableName = "flight")
public class Flight {


    @ColumnInfo(name = "flight_id")
    private long mId;

    @ColumnInfo(name = "callsign")
    private String mCallsign;                //CMP317

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


    @Ignore
    private String aircraftModel;           //Boeing 737NG 8V3/W

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


    public Flight(Airport origin, Airport destination, Airline airline, Calendar departure, Calendar arrival, String callsign) {
        mOrigin = origin;
        mDestination = destination;
        mAirline = airline;
        mDeparture = departure;
        mArrival = arrival;
        mCallsign = callsign;
    }

    public Airport getOrigin() {return mOrigin;}
    public Airport getDestination() {return mDestination;}
    public Airline getAirline() {return mAirline;}
    public Calendar getDeparture() {return mDeparture;}
    public Calendar getArrival() {return mArrival;}
    public String getCallsign() {return mCallsign;}



    @Override
    public String toString() {
        return    "Flight ID="      + mId
                + ": origin="       + (mOrigin != null ? mOrigin.toString() : "NULL")
                + ", destination="  + (mDestination != null ? mDestination.toString() : "NULL")
                + ", airline="      + (mAirline != null ? mAirline.toString() : "NULL")
                + ", departure="    + (mDeparture != null ? mDeparture.getTime().toString() : "NULL")
                + ", arrival="      + (mArrival != null ? mArrival.getTime().toString() : "NULL")
                + ", callsign="     + (mCallsign != null ? mCallsign : "NULL")
                ;
    }
}
