package ve.com.abicelis.planetracker.ui.flight;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.AirportAirlineSearchType;
import ve.com.abicelis.planetracker.data.model.DateFormat;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.customviews.CustomEditText;
import ve.com.abicelis.planetracker.ui.customviews.CustomTextView;
import ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment.AirportAirlineSearchFragment;
import ve.com.abicelis.planetracker.ui.flight.dateselectfragment.DateSelectFragment;
import ve.com.abicelis.planetracker.ui.flight.flightresultsfragment.FlightResultsFragment;
import ve.com.abicelis.planetracker.util.CalendarUtil;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 19/9/2017.
 */

public class FlightActivity extends BaseActivity implements FlightMvpView,
        AirportAirlineSearchFragment.AirportAirlineSelectedListener,
        DateSelectFragment.DateSelectedListener,
        FlightResultsFragment.FlightSelectedListener {

    //DATA
    @Inject
    FlightPresenter mPresenter;

    private enum ContentFrameType {AIRPORT_AIRLINE_SEARCH_FRAGMENT, DATE_SELECT_FRAGMENT, FLIGHT_RESULTS_FRAGMENT}

    //UI
    Observable<CharSequence> mEditTextObservable;
    Disposable mDisposable;

    @BindView(R.id.activity_flight_container)
    LinearLayout mContainer;
    @BindView(R.id.activity_flight_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_flight_search_airport_airline)
    CustomEditText mSearchAirportAirline;

    //SEARCH BY ROUTE
    @BindView(R.id.activity_flight_search_by_route_container)
    LinearLayout mSearchByRouteContainer;
    @BindView(R.id.activity_flight_search_by_route_origin)
    CustomEditText mSearchByRouteOrigin;
    @BindView(R.id.activity_flight_search_by_route_destination)
    CustomEditText mSearchByRouteDestination;
    @BindView(R.id.activity_flight_search_by_route_date)
    CustomTextView mSearchByRouteDate;
    @BindView(R.id.activity_flight_search_by_route_search)
    ImageView mSearchByRouteSearch;

    //SEARCH BY FLIGHT NUMBER
    @BindView(R.id.activity_flight_search_by_flight_number_container)
    LinearLayout mSearchByFlightNumberContainer;
    @BindView(R.id.activity_flight_search_by_flight_number_airline)
    CustomEditText mSearchByFlightNumberAirline;
    @BindView(R.id.activity_flight_search_by_flight_number_number)
    CustomEditText mSearchByFlightNumberNumber;
    @BindView(R.id.activity_flight_search_by_flight_number_date)
    CustomEditText mSearchByFlightNumberDate;

    AirportAirlineSearchFragment mAirportAirlineSearchFragment;
    DateSelectFragment mDateSelectFragment;
    FlightResultsFragment mFlightResultsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);

        setUpToolbar();

        long tripId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_TRIP_ID, -1);
        long flightId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_ID, -1);
        int flightPosition = getIntent().getIntExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION, -1);

        mPresenter.initialize(tripId, flightId, flightPosition);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Timber.d("onAttachFragment TAG=%s", fragment.getTag());

        if(fragment instanceof AirportAirlineSearchFragment){
            mAirportAirlineSearchFragment = (AirportAirlineSearchFragment)fragment;
        }
        if(fragment instanceof DateSelectFragment){
            mDateSelectFragment = (DateSelectFragment) fragment;
        }
        if(fragment instanceof FlightResultsFragment){
            mFlightResultsFragment = (FlightResultsFragment) fragment;
        }
        //        //Capture fragment
//        mAirportAirlineSearchFragment =
//                (AirportAirlineSearchFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void setUpToolbar() {
        //Setup toolbar
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.activity_flight_toolbar_title));
    }


    private void setUpContentFrame(ContentFrameType type, @Nullable AirportAirlineSearchType searchType){
        switch (type) {
            case AIRPORT_AIRLINE_SEARCH_FRAGMENT:
                AirportAirlineSearchFragment fragment = AirportAirlineSearchFragment.getInstance(searchType);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.activity_flight_content_frame,
                        fragment,
                        Constants.TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT)
                        .commit();
                break;
            case DATE_SELECT_FRAGMENT:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.activity_flight_content_frame,
                        new DateSelectFragment(),
                        Constants.TAG_ACTIVITY_FLIGHT_DATE_SELECT_FRAGMENT)
                        .commit();
                break;
            case FLIGHT_RESULTS_FRAGMENT:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.activity_flight_content_frame,
                        new FlightResultsFragment(),
                        Constants.TAG_ACTIVITY_FLIGHT_FLIGHT_RESULTS_FRAGMENT)
                        .commit();
                break;

        }

    }

    /* FlightMvpView implementation */

    @Override
    public void updateViews(FlightPresenter.FlightStep step, @Nullable Flight flight) {
        handleHeaderFieldVisibility(step);
        switch (step) {

            case SEARCHING_AIRPORTS_AIRLINES:

                //Init content frame with AirportAirlineSearchFragment
                setUpContentFrame(ContentFrameType.AIRPORT_AIRLINE_SEARCH_FRAGMENT, AirportAirlineSearchType.BOTH);


                //TODO: REMEMBER TO UNSUBSCRIBE FROM THIS SUBSCRIPTION SOME FREAKING DAY!!!!!!!
                //TODO: REMEMBER TO UNSUBSCRIBE FROM THIS SUBSCRIPTION SOME FREAKING DAY!!!!!!!
                mEditTextObservable = RxTextView.textChanges(mSearchAirportAirline.getInternalEditText())
                        .skip(1)
                        .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS);

                mDisposable = mEditTextObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(charSequence -> {
                            if(mAirportAirlineSearchFragment != null) {
                                String query = charSequence.toString().trim();
                                Timber.d("Text changed %s", query);
                                if (query.length() > 0) {
                                    mAirportAirlineSearchFragment.search(query);
                                } else {
                                    mAirportAirlineSearchFragment.search(null);
                                }
                            }
                        });
                //TODO: REMEMBER TO UNSUBSCRIBE FROM THIS SUBSCRIPTION SOME FREAKING DAY!!!!!!!
                //TODO: REMEMBER TO UNSUBSCRIBE FROM THIS SUBSCRIPTION SOME FREAKING DAY!!!!!!!
                break;

            case ROUTE_SEARCH_SEARCHING_DESTINATION:
                //Change title
                mToolbar.setTitle(R.string.activity_flight_toolbar_title_by_route);


                //Set origin airport
                if(!flight.getOrigin().getIata().isEmpty())
                    mSearchByRouteOrigin.setText(flight.getOrigin().getIata());
                else if (!flight.getOrigin().getName().isEmpty())
                    mSearchByRouteOrigin.setText(flight.getOrigin().getName());

                //Load content frame
                setUpContentFrame(ContentFrameType.AIRPORT_AIRLINE_SEARCH_FRAGMENT, AirportAirlineSearchType.AIRPORT);

                //Highlight destination CustomEditText
                mSearchByRouteDestination.requestFocus();

                if(mDisposable.isDisposed())
                    mDisposable.dispose();

                mEditTextObservable = RxTextView.textChanges(mSearchByRouteDestination.getInternalEditText())
                        .skip(1)
                        .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS);

                mDisposable = mEditTextObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(charSequence -> {
                            if(mAirportAirlineSearchFragment != null) {
                                String query = charSequence.toString().trim();
                                Timber.d("Text changed destination %s", query);
                                if (query.length() > 0) {
                                    mAirportAirlineSearchFragment.search(query);
                                } else {
                                    mAirportAirlineSearchFragment.search(null);
                                }
                            }
                        });

                break;

            case ROUTE_SEARCH_SETTING_DATE:

                //Highlight date CustomTextView
                mSearchByRouteDate.requestFocus();

                //Hide keyboard
                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchByRouteDate.getWindowToken(), 0);


                //Set destination airport
                if(!flight.getDestination().getIata().isEmpty())
                    mSearchByRouteDestination.setText(flight.getDestination().getIata());
                else if (!flight.getDestination().getName().isEmpty())
                    mSearchByRouteDestination.setText(flight.getDestination().getName());


                //Load content frame
                setUpContentFrame(ContentFrameType.DATE_SELECT_FRAGMENT, null);

                //Dispose last observable
                if(mDisposable.isDisposed())
                    mDisposable.dispose();

                mDisposable = RxView.clicks(mSearchByRouteSearch)
                        .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(charSequence -> {
                            mPresenter.searchByRoute();
                        });

                break;


            case ROUTE_SEARCH_SEARCHING_FLIGHTS:
                //Nothing to do here, check handleHeaderFieldVisibility()
                break;



            case ROUTE_SEARCH_SELECTING_FLIGHT:
                //Load content frame
                setUpContentFrame(ContentFrameType.FLIGHT_RESULTS_FRAGMENT, null);

                //Dispose last observable
                if(mDisposable.isDisposed())
                    mDisposable.dispose();
                break;





            case FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER:
                Toast.makeText(this, "TODO FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER", Toast.LENGTH_SHORT).show();
                break;



        }
    }

    private void handleHeaderFieldVisibility(FlightPresenter.FlightStep step) {
        switch (step) {
            case SEARCHING_AIRPORTS_AIRLINES:
                mSearchByRouteContainer.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchAirportAirline.setVisibility(View.VISIBLE);
                break;

            case ROUTE_SEARCH_SEARCHING_DESTINATION:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.VISIBLE);

                mSearchByRouteOrigin.setVisibility(View.VISIBLE);
                mSearchByRouteOrigin.setDim(true);

                mSearchByRouteDestination.setVisibility(View.VISIBLE);
                mSearchByRouteDestination.setDim(false);

                mSearchByRouteDate.setVisibility(View.GONE);
                mSearchByRouteSearch.setVisibility(View.GONE);
                break;

            case ROUTE_SEARCH_SETTING_DATE:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.VISIBLE);

                mSearchByRouteOrigin.setVisibility(View.VISIBLE);
                mSearchByRouteOrigin.setDim(true);

                mSearchByRouteDestination.setVisibility(View.VISIBLE);
                mSearchByRouteDestination.setDim(true);

                mSearchByRouteDate.setVisibility(View.VISIBLE);
                mSearchByRouteDate.setDim(false);

                mSearchByRouteSearch.setVisibility(View.GONE);
                break;

            case ROUTE_SEARCH_SEARCHING_FLIGHTS:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.VISIBLE);

                mSearchByRouteOrigin.setVisibility(View.VISIBLE);
                mSearchByRouteOrigin.setDim(true);

                mSearchByRouteDestination.setVisibility(View.VISIBLE);
                mSearchByRouteDestination.setDim(true);

                mSearchByRouteDate.setVisibility(View.VISIBLE);
                mSearchByRouteDate.setDim(true);

                mSearchByRouteSearch.setVisibility(View.VISIBLE);

                //Disable search button while searching
                mSearchByRouteSearch.setEnabled(false);
                mSearchByRouteSearch.setClickable(false);

                //Show searching overlay
                mDateSelectFragment.showSearchingOverlay();

                break;

            case ROUTE_SEARCH_SELECTING_FLIGHT:

                break;

        }
    }


    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);

    }







    /* AirportAirlineSearchFragment.AirportAirlineSelectedListener implementation */
    @Override
    public void onAirportAirlineSelected(AirportAirlineItem item) {
        mPresenter.airportOrAirlineSelected(item);
    }


    /* DateSelectFragment.DateSelectedListener implementation */
    @Override
    public void onDateSelected(Calendar calendar) {
        DateFormat df = new SharedPreferenceHelper().getDateFormat();

        switch (mPresenter.getStep()) {
            case ROUTE_SEARCH_SETTING_DATE:
                mSearchByRouteDate.setText(CalendarUtil.getCuteStringDateFromCalendar(calendar));
                mSearchByRouteSearch.setVisibility(View.VISIBLE);
                break;
            case FLIGHT_SEARCH_SETTING_DATE:
                mSearchByFlightNumberDate.setText(df.formatCalendar(calendar));
                //TODO mSearchByFlightNumberSearch.setVisibility(View.VISIBLE);
        }

        mPresenter.dateSelected(calendar);
    }


    /* FlightResultsFragment.FlightSelectedListener implementation */
    @Override
    public void onFlightSelected(Flight flight) {
        Toast.makeText(this, "Flight selected! " + flight.toString(), Toast.LENGTH_SHORT).show();
        //mPresenter.flightSelected(flight);
    }

    @Override
    public List<Flight> getFlights() {
        return mPresenter.getTempFlights();
    }

}
