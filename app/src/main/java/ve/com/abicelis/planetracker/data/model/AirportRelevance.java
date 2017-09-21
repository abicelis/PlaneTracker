package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by abicelis on 20/9/2017.
 * Helper class used by airportDao().find()
 * That method calculates results based on relevance, which uses mRelevance.
 */

public class AirportRelevance extends Airport implements AirportAirlineItem {

    @ColumnInfo(name = "relevance")
    public int mRelevance;

    public AirportRelevance(long id, String name, String city, String country, String iata, String icao, float latitude,
                   float longitude, int altitude, String timezoneOffset, String timezoneOlson, String dst) {
        super(id, name, city, country, iata, icao, latitude, longitude, altitude, timezoneOffset, timezoneOlson, dst);
    }

    public int getRelevance() {
        return mRelevance;
    }

    public void setRelevance(int relevance) {
        this.mRelevance = relevance;
    }
}
