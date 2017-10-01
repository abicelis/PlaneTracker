package ve.com.abicelis.planetracker.ui.tracker;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.IntegerLatLng;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 27/9/2017.
 */

public interface TrackerMvpView extends MvpView {

    void initViewPager(List<Flight> flights);
    void initMap(Trip trip);
    void setRestrictZoom(boolean restrict);
    void moveCameraToLocation(@NonNull IntegerLatLng integerLatLng, boolean restrictZoom, int delay);
    void drawRouteAndMoveCameraToBoundsOf(Airport origin, Airport destination);

    // void initGoogleApiClient();
}
