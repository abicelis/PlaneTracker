package ve.com.abicelis.planetracker.ui.tracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 27/9/2017.
 */

public interface TrackerMvpView extends MvpView {

    void initViewPager(List<Flight> flights);
    void initMap(Trip trip);
    void setRestrictZoom(boolean restrict);
    void moveCameraToLocation(@NonNull LatLng latLng, boolean restrictZoom, int delay, @Nullable GoogleMap.CancelableCallback callback);
    void drawRouteAndMoveCameraToBoundsOf(Airport origin, Airport destination);

    void animateCamera(Flight flight, int delay);
    void stopAnimatingCamera();
}
