package ve.com.abicelis.planetracker.ui.home;

import java.util.List;

import ve.com.abicelis.planetracker.data.model.TripViewModel;
import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 4/9/2017.
 */

public interface HomeMvpView extends MvpView {

   void showLoading();
   void showTrips(List<TripViewModel> trips);
}
