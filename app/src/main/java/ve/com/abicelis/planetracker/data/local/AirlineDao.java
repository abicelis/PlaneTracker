package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import ve.com.abicelis.planetracker.data.model.Airline;

/**
 * Created by abicelis on 30/8/2017.
 */

@Dao
public interface AirlineDao {

    @Query("SELECT * FROM airline")
    Maybe<List<Airline>> getAll();

    @Query("SELECT * FROM airline where airline_id = :airlineId")
    Maybe<Airline> getById(long airlineId);

    @Query("SELECT * FROM airline where airline_id IN (:airlineIds)")
    Maybe<List<Airline>> getByIds(long[] airlineIds);

    @Query("SELECT * FROM airline where iata = :iata")
    Maybe<Airline> getByIata(String iata);

    @Query("SELECT * FROM airline where icao = :icao")
    Maybe<Airline> getByIcao(String icao);

    @Query("SELECT * FROM airline WHERE name LIKE :query OR iata LIKE :query OR icao LIKE :query OR callsign LIKE :query")
    Maybe<List<Airline>> find(String query);

    @Insert
    long[] insert(Airline ... airlines);

    @Update
    int update(Airline airline);

    @Query("DELETE FROM airline")
    int deleteAll();

}
