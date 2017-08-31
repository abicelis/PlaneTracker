package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Single;
import ve.com.abicelis.planetracker.data.model.Airline;

/**
 * Created by abicelis on 30/8/2017.
 */

@Dao
public interface AirlineDao {

    @Query("SELECT * FROM airline")
    Single<List<Airline>> getAll();

    @Query("SELECT * FROM airline WHERE name LIKE :query OR iata LIKE :query OR icao LIKE :query OR callsign LIKE :query")
    Single<List<Airline>> find(String query);

    @Insert
    void insert(Airline... airlines);

    @Update
    void update(Airline... airlines);

    @Query("DELETE FROM airline")
    void deleteAll();

}
