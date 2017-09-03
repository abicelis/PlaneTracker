package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import ve.com.abicelis.planetracker.data.model.Trip;

/**
 * Created by abicelis on 30/8/2017.
 */

@Dao
public interface TripDao {

    @Query("SELECT * FROM trip")
    Maybe<List<Trip>> getAll();

    @Query("SELECT * FROM trip where trip_id = :tripId")
    Maybe<Trip> getById(long tripId);

    @Query("SELECT * FROM trip where trip_id IN (:tripIds)")
    Maybe<List<Trip>> getByIds(long[] tripIds);


    @Insert
    void insert(Trip... trips);

    @Update
    void update(Trip... trips);

    @Delete
    void delete(Trip... trips);

}
