package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.Trip;

/**
 * Created by abicelis on 30/8/2017.
 */

@Database(entities = {Airport.class, Airline.class, Flight.class, Trip.class}, version = 21)
@TypeConverters({CalendarConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AirportDao airportDao();
    public abstract AirlineDao airlineDao();
    public abstract FlightDao flightDao();
    public abstract TripDao tripDao();
}
