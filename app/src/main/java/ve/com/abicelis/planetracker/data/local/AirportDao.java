package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import ve.com.abicelis.planetracker.data.model.Airport;

/**
 * Created by abicelis on 30/8/2017.
 */

@Dao
public interface AirportDao {

    @Query("SELECT * FROM airport")
    Single<List<Airport>> getAll();

    @Query("SELECT * FROM airport WHERE name LIKE :query")
    Single<List<Airport>> find(String query);

    @Insert
    public void insert(Airport... airports);

    @Update
    public void update(Airport... airports);

    @Query("DELETE FROM airport")
    public void deleteAll();

}
