package ve.com.abicelis.planetracker.data.model;

import java.util.Calendar;

/**
 * Created by abicelis on 17/9/2017.
 * Helper class to list flights and layover header.
 */

public class FlightViewModel {

    private Flight mFlight;
    private FlightHeader mHeader;
    private FlightViewModelType mFlightViewModelType;


    public FlightViewModel(Flight flight) {
        mFlightViewModelType = FlightViewModelType.FLIGHT;
        mFlight = flight;
    }

    public FlightViewModel(String title, Calendar start, Calendar end) {
        mFlightViewModelType = FlightViewModelType.HEADER_LAYOVER;
        mHeader = new FlightHeader(title, start, end);
    }

    public FlightViewModel() {
        mFlightViewModelType = FlightViewModelType.HEADER_EDIT_ONLY;
        mHeader = new FlightHeader();
    }


    public Flight getFlight() {return mFlight;}
    public FlightHeader getHeader() {return mHeader;}
    public FlightViewModelType getFlightViewModelType() {return mFlightViewModelType;}

    public enum FlightViewModelType { HEADER_EDIT_ONLY, HEADER_LAYOVER, FLIGHT }
}
