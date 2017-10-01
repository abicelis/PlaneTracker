package ve.com.abicelis.planetracker.data.model;

import java.io.Serializable;

/**
 * Created by abicelis on 30/9/2017.
 */

public class FlightProgressViewModel implements Serializable {

    private boolean isFirstFlight;
    private boolean isLastFlight;
    private Flight mFlight;

    public FlightProgressViewModel(boolean firstFlight, boolean lastFlight, Flight flight) {
        isFirstFlight = firstFlight;
        isLastFlight = lastFlight;
        mFlight = flight;
    }

    public boolean isFirstFlight() {return isFirstFlight;}
    public boolean isLastFlight() {return isLastFlight;}
    public Flight getFlight() {return mFlight;}
}
