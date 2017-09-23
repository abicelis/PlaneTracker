package ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment;

import java.util.List;

import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.AirportAirlineSearchType;
import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 20/9/2017.
 */

public interface AirportAirlineSearchMvpView extends MvpView {
    void showLoading();
    void hideLoading();
    void clearItems();
    void showItems(List<AirportAirlineItem> items, AirportAirlineSearchType type);
    void showRecents(List<AirportAirlineItem> items, AirportAirlineSearchType mSearchType);
}
