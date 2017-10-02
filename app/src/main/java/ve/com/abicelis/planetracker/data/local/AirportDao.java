package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.AirportRelevance;

/**
 * Created by abicelis on 30/8/2017.
 */

@Dao
public interface AirportDao {

    @Query("SELECT count(*) FROM airport")
    Single<Integer> count();

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

    @Query("SELECT *" +
            ",(name LIKE :query) +" +
            " (city LIKE :query) +" +
            " (country LIKE :query) +" +
            " (CASE WHEN iata LIKE :query THEN 3 ELSE 0 END) +" +
            " (CASE WHEN icao LIKE :query THEN 3 ELSE 0 END)" +
            " AS relevance" +
            " FROM AIRPORT" +
            " WHERE name LIKE :query OR city LIKE :query OR country LIKE :query OR iata LIKE :query OR icao LIKE :query" +
            " ORDER BY [relevance] desc")
    Maybe<List<AirportRelevance>> find(String query);

    @Query("SELECT *" +
            ",(name LIKE :query) +" +
            " (city LIKE :query) +" +
            " (country LIKE :query) +" +
            " (CASE WHEN iata LIKE :query THEN 3 ELSE 0 END) +" +
            " (CASE WHEN icao LIKE :query THEN 3 ELSE 0 END)" +
            " AS relevance" +
            " FROM AIRPORT" +
            " WHERE name LIKE :query OR city LIKE :query OR country LIKE :query OR iata LIKE :query OR icao LIKE :query" +
            " ORDER BY [relevance] desc " +
            " LIMIT :limit")
    Maybe<List<AirportRelevance>> find(String query, int limit);

    @Insert
    long[] insert(Airport ... airports);

    @Update
    int update(Airport airport);

    @Delete
    void delete(Airport airport);

    @Query("DELETE FROM airport")
    int deleteAll();

}
