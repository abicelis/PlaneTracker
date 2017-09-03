package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import ve.com.abicelis.planetracker.data.model.Flight;

/**
 * Created by abicelis on 30/8/2017.
 */

@Dao
public interface FlightDao {

    @Query("SELECT * FROM flight")
    Maybe<List<Flight>> getAll();

    @Query("SELECT * FROM flight where flight_id = :flightId")
    Maybe<Flight> getById(long flightId);

    @Query("SELECT * FROM flight where flight_id IN (:flightIds)")
    Maybe<List<Flight>> getByIds(long[] flightIds);

    @Query("SELECT * FROM flight where trip_fk = :tripId ORDER BY order_in_trip ASC")
    Maybe<List<Flight>> getByTripId(long tripId);

    @Insert
    long insert(Flight flight);

    @Insert
    long[] insert(Flight... flights);

    @Update
    void update(Flight... flights);

    @Delete
    void delete(Flight... flights);

}
