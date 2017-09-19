package ve.com.abicelis.planetracker.data.model;

import android.content.Context;

import java.util.Calendar;
import java.util.Locale;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 17/9/2017.
 * Helper class to list flights and layover header.
 */

public class FlightViewModel {

    private Flight mFlight;

    private String mTitle;
    private String mLayover;

    private FlightViewModelType mFlightViewModelType;



    public FlightViewModel(Flight flight) {
        mFlightViewModelType = FlightViewModelType.FLIGHT;
        mFlight = flight;
    }

    public FlightViewModel(String title, Calendar start, Calendar end) {
        mFlightViewModelType = FlightViewModelType.HEADER_LAYOVER;
        mTitle = title;
        mLayover = CalendarUtil.getCuteTimeStringBetweenCalendars(start, end);
    }


    public Flight getFlight() {return mFlight;}
    public String getHeaderTitle() {return mTitle;}
    public String getHeaderLayover() {return mLayover;}
    public FlightViewModelType getFlightViewModelType() {return mFlightViewModelType;}

    public enum FlightViewModelType { HEADER_LAYOVER, FLIGHT }
}
