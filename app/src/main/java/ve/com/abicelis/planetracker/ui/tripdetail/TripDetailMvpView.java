package ve.com.abicelis.planetracker.ui.tripdetail;

import java.util.List;

import ve.com.abicelis.planetracker.data.model.FlightViewModel;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 18/9/2017.
 */

public interface TripDetailMvpView extends MvpView {
    void showTrip(Trip trip, List<FlightViewModel> flights);

    void reloadTripImage(byte[] image);
    void reloadTripName(String tripName);

    void activateEditMode();
    void deactivateEditMode();

    void tripDeletedSoFinish();
}

