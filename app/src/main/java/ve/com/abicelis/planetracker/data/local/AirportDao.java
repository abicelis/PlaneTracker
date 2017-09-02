package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import ve.com.abicelis.planetracker.data.model.Airport;

/**
 * Created by abicelis on 30/8/2017.
 */

@Dao
public interface AirportDao {

    @Query("SELECT * FROM airport")
    Maybe<List<Airport>> getAll();

    @Query("SELECT * FROM airport where airport_id = :airportId")
    Maybe<Airport> getById(long airportId);

    @Query("SELECT * FROM airport where airport_id IN (:airportIds)")
    Maybe<List<Airport>> getByIds(long[] airportIds);

    @Query("SELECT * FROM airport where iata = :iata")
    Maybe<Airport> getByIata(String iata);

    @Query("SELECT * FROM airport where icao = :icao")
    Maybe<Airport> getByIcao(String icao);

    @Query("SELECT * FROM airport WHERE name LIKE :query OR iata LIKE :query OR icao LIKE :query")
    Maybe<List<Airport>> find(String query);

    @Insert
    void insert(Airport... airports);

    @Update
    void update(Airport... airports);

    @Query("DELETE FROM airport")
    void deleteAll();

}
