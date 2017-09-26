package ve.com.abicelis.planetracker.ui.flight;

import android.support.annotation.Nullable;

import java.util.List;

import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 19/9/2017.
 */

public interface FlightMvpView extends MvpView {
    void updateViews(FlightPresenter.FlightStep step, @Nullable Flight flight);
    void updateRouteFieldsWithExistingFlightInfo(Flight flight);

    void tripSaved(long tripId, FlightPresenter.FlightProcedure mFlightProcedure);
}
