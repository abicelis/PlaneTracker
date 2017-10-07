package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Calendar;

import timber.log.Timber;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 26/8/2017.
 */
@Entity(tableName = "flight",
        indices = {@Index("trip_fk"), @Index("origin_fk"), @Index("destination_fk"), @Index("airline_fk")},
        foreignKeys = {
                @ForeignKey(entity = Trip.class, parentColumns = "trip_id", childColumns = "trip_fk", onDelete=ForeignKey.CASCADE),
                @ForeignKey(entity = Airport.class, parentColumns = "airport_id", childColumns = "origin_fk", onDelete = ForeignKey.NO_ACTION),
                @ForeignKey(entity = Airport.class, parentColumns = "airport_id", childColumns = "destination_fk", onDelete = ForeignKey.NO_ACTION),
                @ForeignKey(entity = Airline.class, parentColumns = "airline_id", childColumns = "airline_fk", onDelete = ForeignKey.NO_ACTION)
        })
public class Flight implements Comparable<Flight>, Serializable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "flight_id")
    private long mId;

    @ColumnInfo(name="trip_fk")
    public long mTripId;

    @ColumnInfo(name = "flight_aware_id")
    private String mFlightAwareId;

    @ColumnInfo(name = "order_in_trip")     //Integer value, depicts the order in which this flight occurs on its trip. Can be 1, 2, 3, .... n.
    private int mOrderInTrip;

    @ColumnInfo(name = "callsign")
    private String mCallsign;                //CMP317       Airline 3-letter ICAO Flight #

    @ColumnInfo(name = "origin_fk")
    private long mOriginId;
    @Ignore Airport mOrigin;

    @ColumnInfo(name = "destination_fk")
    private long mDestinationId;
    @Ignore
    private Airport mDestination;

    @ColumnInfo(name = "airline_fk")
    private long mAirlineId;
    @Ignore
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




    public Flight(long id, String flightAwareId, int orderInTrip, String callsign, long originId, long destinationId, long airlineId, Calendar departure, Calendar arrival, String aircraftModel) {
        mId = id;
        mFlightAwareId = flightAwareId;
        mOrderInTrip = orderInTrip;
        mCallsign = callsign;
        mOriginId = originId;
        mDestinationId = destinationId;
        mAirlineId = airlineId;
        mDeparture = departure;
        mArrival = arrival;
        mAircraftModel = aircraftModel;
    }

    @Ignore
    public Flight(String flightAwareId, String callsign, Airport origin, Airport destination, Airline airline, Calendar departure, Calendar arrival, String aircraftModel) {
        //mId = -1;
        mOrderInTrip = -1;

        mFlightAwareId = flightAwareId;
        mCallsign = callsign;
        mOrigin = origin;
        mOriginId = mOrigin.getId();
        mDestination = destination;
        mDestinationId = mDestination.getId();
        mAirline = airline;
        mAirlineId = mAirline.getId();
        mDeparture = departure;
        mArrival = arrival;
        mAircraftModel = aircraftModel;
    }

    @Ignore
    public Flight() {       //Used when creating a new flight
        mOrderInTrip = -1;
    }

    public long getId() {return mId;}
    public long getTripId() {return mTripId;}
    public String getFlightAwareId() {return mFlightAwareId;}
    public int getOrderInTrip() {return mOrderInTrip;}
    public String getCallsign() {return mCallsign;}
    public long getOriginId() {return mOriginId;}
    public Airport getOrigin() {return mOrigin;}
    public long getDestinationId() {return mDestinationId;}
    public Airport getDestination() {return mDestination;}
    public long getAirlineId() {return mAirlineId;}
    public Airline getAirline() {return mAirline;}
    public Calendar getDeparture() {return mDeparture;}
    public Calendar getArrival() {return mArrival;}
    public String getAircraftModel() {return mAircraftModel;}
    public Status getStatus() {
        if(Calendar.getInstance().before(mDeparture))
            return Status.NOT_DEPARTED;
        if (Calendar.getInstance().after(mArrival))
            return Status.ARRIVED;
        else
            return Status.IN_AIR;
    }

    /**
     * Returns the total flight time in seconds
     */
    public long getFlightTime() {
        long startMillis = mDeparture.getTimeInMillis();
        long endMillis = mArrival.getTimeInMillis();

        long seconds = Math.abs(endMillis - startMillis)/1000;
        return seconds;
    }

    /**
     * If Status == IN_AIR, returns the amount of elapsed seconds between departure and now
     * otherwise, returns getFlightTime()
     * if NOT_DEPARTED returns 0
     * if ARRIVED returns getFlightTime();
     */
    public long getElapsedTime() {
        switch (getStatus()) {
            case NOT_DEPARTED:
                return 0;
            case ARRIVED:
                return getFlightTime();
            case IN_AIR:
                long startMillis = mDeparture.getTimeInMillis();
                long endMillis = Calendar.getInstance().getTimeInMillis();

                long seconds = Math.abs(endMillis - startMillis)/1000;
                return seconds;
            default:
                throw new InvalidParameterException("Invalid Flight Status!");
        }
    }

    /**
     * If Status == IN_AIR, returns a fraction (number between 0 - 1) representing the percentage
     * of elapsed flight
     * if NOT_DEPARTED returns 0
     * if ARRIVED returns 1
     */
    public double getElapsedFraction() {
        switch (getStatus()) {
            case NOT_DEPARTED:
                return 0;
            case ARRIVED:
                return 1;
            case IN_AIR:
                return  ((double)getElapsedTime()) / getFlightTime();
            default:
                throw new InvalidParameterException("Invalid Flight Status!");
        }
    }

    public double getFractionStepForMillis(int fractionMillis) {
        long flightTimeMillis = getFlightTime()*1000;
        return ((double)fractionMillis) / flightTimeMillis;
    }


    public int getTheoreticalProgress() {
        switch (getStatus()) {
            case NOT_DEPARTED:
                return 0;
            case ARRIVED:
                return 100;
            case IN_AIR:
                return (int)((((float)getElapsedTime())/getFlightTime())*100);
            default:
                Timber.e("Warning, wrong status!");
                return 0;
        }
    }

    public void setId(long mId) {this.mId = mId;}
    public void setTripId(long tripId) {this.mTripId = tripId;}
    public void setFlightAwareId(String mFlightAwareId) {this.mFlightAwareId = mFlightAwareId;}
    public void setOrderInTrip(int mOrderInTrip) {this.mOrderInTrip = mOrderInTrip;}
    public void setCallsign(String mCallsign) {this.mCallsign = mCallsign;}
    public void setOriginId(long mOriginId) {this.mOriginId = mOriginId;}
    public void setOrigin(Airport mOrigin) {this.mOrigin = mOrigin;}
    public void setDestinationId(long mDestinationId) {this.mDestinationId = mDestinationId;}
    public void setDestination(Airport mDestination) {this.mDestination = mDestination;}
    public void setAirlineId(long mAirlineId) {this.mAirlineId = mAirlineId;}
    public void setAirline(Airline mAirline) {this.mAirline = mAirline;}
    public void setDeparture(Calendar mDeparture) {this.mDeparture = mDeparture;}
    public void setArrival(Calendar mArrival) {this.mArrival = mArrival;}
    public void setmAircraftModel(String mAircraftModel) {this.mAircraftModel = mAircraftModel;}

    @Override
    public String toString() {
        return    "Flight ID="          + mId
                + "\n   tripID="        + mTripId
                + "\n   flightAwareID=" + (mFlightAwareId != null ? mFlightAwareId : "NULL")
                + "\n   orderInTrip="   + mOrderInTrip
                + "\n   callsign="      + (mCallsign != null ? mCallsign : "NULL")
                + "\n   originId="      + mOriginId
                + "\n   origin="        + (mOrigin != null ? mOrigin.toString() : "NULL")
                + "\n   destinationId=" + mDestinationId
                + "\n   destination="   + (mDestination != null ? mDestination.toString() : "NULL")
                + "\n   airlineId="     + mAirlineId
                + "\n   airline="       + (mAirline != null ? mAirline.toString() : "NULL")
                + "\n   departure="     + (mDeparture != null ? CalendarUtil.getStringDateFromCalendar(mDeparture) : "NULL")
                + "\n   arrival="       + (mArrival != null ? CalendarUtil.getStringDateFromCalendar(mArrival) : "NULL")
                + "\n   aircraftModel=" + (mAircraftModel != null ? mAircraftModel : "NULL")
                ;
    }

    @Override
    public int compareTo(@NonNull Flight flight) {
        if (this.mDeparture == null)
            return 1;
        if (flight.getDeparture() == null)
            return -1;
        return this.mDeparture.compareTo(flight.getDeparture());
    }



    public enum Status { NOT_DEPARTED, ARRIVED, IN_AIR }

}
