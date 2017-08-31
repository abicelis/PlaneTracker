package ve.com.abicelis.planetracker.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by abicelis on 29/8/2017.
 */

@Entity(tableName = "airline",
        indices = {@Index("name"), @Index("iata"), @Index("icao"), @Index("callsign")}
)
public class Airline {

    @PrimaryKey
    @ColumnInfo(name = "airline_id")
    private long mId;                       //Unique OpenFlights identifier for this airline

    @ColumnInfo(name = "name")
    private String mName;                   //Name of the airline

    @ColumnInfo(name = "alias")
    private String mAlias;                  //Alias of the airline. For example, All Nippon Airways is commonly known as "ANA"

    @ColumnInfo(name = "iata")
    private String mIata;                   //2-letter IATA code. Null if unknown

    @ColumnInfo(name = "icao")
    private String mIcao;                   //3-letter ICAO code. Null if unknown

    @ColumnInfo(name = "callsign")
    private String mCallsign;               //Airline Callsign

    @ColumnInfo(name = "country")
    private String mCountry;               //Country or territory where airline is incorporated.


    public Airline(long id, String name, String alias, String iata, String icao, String callsign, String country) {
        mId = id;
        mName = name;
        mAlias = alias;
        mIata = iata;
        mIcao = icao;
        mCallsign = callsign;
        mCountry = country;
    }

    public long getId() {return mId;}
    public String getName() {return mName;}
    public String getAlias() {return mAlias;}
    public String getIata() {return mIata;}
    public String getIcao() {return mIcao;}
    public String getCallsign() {return mCallsign;}
    public String getCountry() {return mCountry;}


    @Override
    public String toString() {
        return    "Airline ID=" + mId
                + ": name=" + mName
                + ": alias=" + mAlias
                + ", iata=" + mIata
                + ", icao=" + mIcao
                + ", callsign=" + mCallsign
                + ", country=" + mCountry;
    }
}
