package ve.com.abicelis.planetracker.data.model;

import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
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

    public FlightViewModel(@NonNull FlightViewModelType type) {
        if(type == FlightViewModelType.HEADER_ERROR_DEPARTURE_BEFORE_ARRIVAL
                || type == FlightViewModelType.HEADER_ERROR_DIFFERENT_ORIGIN_DESTINATION
                || type == FlightViewModelType.HEADER_EDIT_ONLY) {
            mFlightViewModelType = type;
            mHeader = new FlightHeader();
        } else {
            throw new InvalidParameterException("Type can only be HEADER_ERROR_DEPARTURE_BEFORE_ARRIVAL or HEADER_ERROR_DIFFERENT_ORIGIN_DESTINATION");
        }
    }


    public Flight getFlight() {return mFlight;}
    public FlightHeader getHeader() {return mHeader;}
    public FlightViewModelType getFlightViewModelType() {return mFlightViewModelType;}

    public enum FlightViewModelType { HEADER_EDIT_ONLY, HEADER_ERROR_DIFFERENT_ORIGIN_DESTINATION, HEADER_ERROR_DEPARTURE_BEFORE_ARRIVAL, HEADER_LAYOVER, FLIGHT }
}
