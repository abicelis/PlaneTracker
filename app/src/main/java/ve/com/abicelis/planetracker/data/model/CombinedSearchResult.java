package ve.com.abicelis.planetracker.data.model;

import ve.com.abicelis.planetracker.data.local.AirportDao_Impl;

/**
 * Created by abicelis on 29/8/2017.
 *
 * This is used by DataManager.find
 */

///TODO probably delete this
public class CombinedSearchResult {

    private long mId;                               //Unique id for this airport or airline

    private SearchResultType mSearchResultType;

    private String mName;                           //Name of airport or airline

    private String mIata;                           //3-letter IATA code. Null if not assigned/unknown.

    private String mIcao;                           //4-letter ICAO code. Null if not assigned.


    public CombinedSearchResult(Airport airport) {
        mId = airport.getId();
        mName = airport.getName();
        mSearchResultType = SearchResultType.AIRPORT;
        mIata = airport.getIata();
        mIcao = airport.getIcao();
    }

    public CombinedSearchResult(Airline airline) {
        mId = airline.getId();
        mName = airline.getName();
        mSearchResultType = SearchResultType.AIRLINE;
        mIata = airline.getIata();
        mIcao = airline.getIcao();
    }

    public CombinedSearchResult(long id, SearchResultType searchResultType, String name, String iata, String icao) {
        mId = id;
        mName = name;
        mSearchResultType = searchResultType;
        mIata = iata;
        mIcao = icao;
    }

    public long getId() {return mId;}
    public SearchResultType getmSearchResultType() {return mSearchResultType;}
    public String getName() {return mName;}
    public String getIata() {return mIata;}
    public String getIcao() {return mIcao;}


    @Override
    public String toString() {
        return    "CombinedSearchResult ID="    + mId
                + ": getmSearchResultType="     + mSearchResultType.name()
                + ", name="                     + (mName != null ? mName : "NULL")
                + ", iata="                     + (mIata != null ? mIata : "NULL")
                + ", icao="                     + (mIcao != null ? mIcao : "NULL")
                ;
    }

    public enum SearchResultType {
        AIRPORT,AIRLINE;
    }
}
