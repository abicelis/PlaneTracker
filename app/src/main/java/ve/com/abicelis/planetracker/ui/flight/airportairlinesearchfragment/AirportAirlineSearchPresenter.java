package ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.AirportAirlineSearchType;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 20/9/2017.
 */

public class AirportAirlineSearchPresenter extends BasePresenter<AirportAirlineSearchMvpView> {

    //DATA
    @Inject
    DataManager mDataManager;
    AirportAirlineSearchType mSearchType;


    public AirportAirlineSearchPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void setSearchType(AirportAirlineSearchType searchType) {
        mSearchType = searchType;
    }

    void getRecents() {
        switch (mSearchType) {
            case BOTH:
                mDataManager.getRecentAirportsAndAirlines()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> {
                            getMvpView().showRecents(items, mSearchType);
                        });
                break;
            case AIRPORT:
                mDataManager.getRecentAirports()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(airports -> {
                            List<AirportAirlineItem> items = new ArrayList<>();
                            items.addAll(airports);
                            getMvpView().showRecents(items, mSearchType);
                        });
                break;
            case AIRLINE:
                mDataManager.getRecentAirlines()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(airlines -> {
                            List<AirportAirlineItem> items = new ArrayList<>();
                            items.addAll(airlines);
                            getMvpView().showRecents(items, mSearchType);
                        });
                break;
            default:
                //TODO error, notify user! searchType problem.
        }
    }

    void search(String query) {
        //getMvpView().showLoading();
        switch (mSearchType) {
            case BOTH:
                mDataManager.findAirportsOrAirlines(query, Constants.ROOM_DATABASE_MAX_SEARCH_RESULTS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(airportAirlineItems -> {
                            getMvpView().showItems(airportAirlineItems, mSearchType);
                        }, throwable -> {
                            Timber.e(throwable, "Error getting airports or airlines for query=%s", query);
                            getMvpView().showMessage(Message.ERROR_GETTING_AIRPORTS_OR_AIRLINES, null);
                            getMvpView().hideLoading();
                        });
                break;


            case AIRLINE:
                mDataManager.findAirlines(query, Constants.ROOM_DATABASE_MAX_SEARCH_RESULTS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(airlines -> {
                            List<AirportAirlineItem> items = new ArrayList<>();
                            items.addAll(airlines);
                            getMvpView().showItems(items, mSearchType);
                        }, throwable -> {
                            Timber.e(throwable, "Error getting airlines for query=%s", query);
                            getMvpView().showMessage(Message.ERROR_GETTING_AIRLINES, null);
                            getMvpView().hideLoading();
                        });
                break;
            case AIRPORT:
                mDataManager.findAirports(query, Constants.ROOM_DATABASE_MAX_SEARCH_RESULTS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(airports -> {
                            List<AirportAirlineItem> items = new ArrayList<>();
                            items.addAll(airports);
                            getMvpView().showItems(items, mSearchType);
                        }, throwable -> {
                            Timber.e(throwable, "Error getting airports for query=%s", query);
                            getMvpView().showMessage(Message.ERROR_GETTING_AIRPORTS, null);
                            getMvpView().hideLoading();
                        });
                break;
            default:
                //TODO error, notify user! searchType problem.
        }
    }


}
