package ve.com.abicelis.planetracker.data.model;

/**
 * Created by abicelis on 22/9/2017.
 */

public class AirportAirlineHeader implements AirportAirlineItem {

    private String mTitle;

    public AirportAirlineHeader(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
