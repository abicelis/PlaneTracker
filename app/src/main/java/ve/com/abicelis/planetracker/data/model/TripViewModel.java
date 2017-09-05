package ve.com.abicelis.planetracker.data.model;

import java.security.InvalidParameterException;

/**
 * Created by abicelis on 4/9/2017.
 * Helper class to list trips and headers for Home view recycler.
 */

public class TripViewModel {

    private Trip mTrip;
    private TripViewModelType mTripViewModelType;

    public TripViewModel(Trip trip) {
        mTripViewModelType = TripViewModelType.TRIP;
        mTrip = trip;
    }

    public TripViewModel(TripViewModelType tripViewModelType) throws InvalidParameterException {
        if(tripViewModelType == TripViewModelType.TRIP)
            throw new InvalidParameterException("Invalid type. Use alternate constructor to init a TripViewModel.TRIP");

        mTripViewModelType = tripViewModelType;
    }


    public Trip getTrip() {return mTrip;}
    public TripViewModelType getTripViewModelType() {return mTripViewModelType;}

    public enum TripViewModelType { HEADER_UPCOMING, HEADER_CURRENT, HEADER_PAST, TRIP }
}
