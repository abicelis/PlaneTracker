package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by abicelis on 20/9/2017.
 * Helper class used by airlineDao().find()
 * That method calculates results based on relevance, which uses mRelevance.
 */

public class AirlineRelevance extends Airline implements AirportAirlineItem {

    @ColumnInfo(name = "relevance")
    public int mRelevance;

    public AirlineRelevance(long id, String name, String alias, String iata, String icao, String callsign, String country) {
        super(id, name, alias, iata, icao, callsign, country);
    }

    public int getRelevance() {
        return mRelevance;
    }

    public void setRelevance(int relevance) {
        this.mRelevance = relevance;
    }
}
