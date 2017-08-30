package ve.com.abicelis.planetracker.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ve.com.abicelis.planetracker.data.model.Airport;

/**
 * Created by abicelis on 30/8/2017.
 */

@Database(entities = {Airport.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AirportDao airportDao();
}
