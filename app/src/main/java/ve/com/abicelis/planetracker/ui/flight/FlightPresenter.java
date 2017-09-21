package ve.com.abicelis.planetracker.ui.flight;

import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 19/9/2017.
 */

public class FlightPresenter extends BasePresenter<FlightActivity> {

    //DATA
    private long mTripId;
    private long mFlightId;
    private long mFlightPosition;
    private FlightActivityStatus mStatus;

    private DataManager mDataManager;
    public enum FlightActivityStatus {NEW_FLIGHT_IN_NEW_TRIP, NEW_FLIGHT_IN_EXISTING_TRIP, EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP}

    public FlightPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    public void initialize(long tripId, long flightId, int flightPosition) {
        mTripId = tripId;
        mFlightId = flightId;
        mFlightPosition = flightPosition;

        if(mTripId == -1) {                         //Intent came from HomeActivity, NEW_FLIGHT_IN_NEW_TRIP
            mStatus = FlightActivityStatus.NEW_FLIGHT_IN_NEW_TRIP;
            //TODO: Save the newly created flight into a new trip, finish FlightActivity and go to TripDetailActivity
        } else {
            if (mFlightPosition != -1) {
                if(mFlightId != -1) {               //Intent came from TripDetailActivity, EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP
                    mStatus = FlightActivityStatus.EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP;

                } else {                            //Intent came from TripDetailActivity, NEW_FLIGHT_IN_EXISTING_TRIP
                    mStatus = FlightActivityStatus.NEW_FLIGHT_IN_EXISTING_TRIP;

                }

            }

        }

    }
}
