package ve.com.abicelis.planetracker.ui.flight;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import ve.com.abicelis.planetracker.data.model.AppThemeType;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.customviews.CustomEditText2;
import ve.com.abicelis.planetracker.ui.customviews.CustomTextView2;
import ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment.AirportAirlineSearchFragment;
import ve.com.abicelis.planetracker.ui.flight.dateselectfragment.DateSelectFragment;
import ve.com.abicelis.planetracker.ui.flight.flightresultsfragment.FlightResultsFragment;
import ve.com.abicelis.planetracker.ui.flight.numberselectfragment.NumberSelectFragment;
import ve.com.abicelis.planetracker.ui.tripdetail.TripDetailActivity;
import ve.com.abicelis.planetracker.util.CalendarUtil;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 19/9/2017.
 */

public class FlightActivity extends BaseActivity implements FlightMvpView,
        AirportAirlineSearchFragment.AirportAirlineSelectedListener,
        DateSelectFragment.DateSelectedListener,
        FlightResultsFragment.FlightSelectedListener,
        NumberSelectFragment.NumberEnteredListener
{

    //DATA
    @Inject
    FlightPresenter mPresenter;
    private enum ContentFrameType {AIRPORT_AIRLINE_SEARCH_FRAGMENT, DATE_SELECT_FRAGMENT, NUMBER_SELECT_FRAGMENT, FLIGHT_RESULTS_FRAGMENT, NONE}


    //UI
    Disposable mAirportAirlineSearchDisposable;
    Disposable mRouteOriginTextChangeDisposable;
    Disposable mRouteDestinationTextChangeDisposable;
    Disposable mRouteOriginClickDisposable;
    Disposable mRouteDestinationClickDisposable;
    Disposable mRouteDateClickDisposable;
    Disposable mRouteSearchClickDisposable;

    Disposable mFlightAirlineTextChangeDisposable;
    Disposable mFlightAirlineClickDisposable;
    Disposable mFlightNumberClickDisposable;
    Disposable mFlightDateClickDisposable;
    Disposable mFlightSearchClickDisposable;

    @BindView(R.id.activity_flight_container)
    LinearLayout mContainer;
    @BindView(R.id.activity_flight_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_flight_search_airport_airline)
    CustomEditText2 mSearchAirportAirline;

    //SEARCH BY ROUTE
    @BindView(R.id.activity_flight_search_by_route_container)
    LinearLayout mSearchByRouteContainer;
    @BindView(R.id.activity_flight_search_by_route_origin)
    CustomEditText2 mSearchByRouteOrigin;
    @BindView(R.id.activity_flight_search_by_route_destination)
    CustomEditText2 mSearchByRouteDestination;
    @BindView(R.id.activity_flight_search_by_route_date)
    CustomTextView2 mSearchByRouteDate;
    @BindView(R.id.activity_flight_search_by_route_search)
    ImageView mSearchByRouteSearch;

    //SEARCH BY FLIGHT NUMBER
    @BindView(R.id.activity_flight_search_by_flight_number_container)
    LinearLayout mSearchByFlightNumberContainer;
    @BindView(R.id.activity_flight_search_by_flight_number_airline)
    CustomEditText2 mSearchByFlightNumberAirline;
    @BindView(R.id.activity_flight_search_by_flight_number_number)
    CustomTextView2 mSearchByFlightNumberNumber;
    @BindView(R.id.activity_flight_search_by_flight_number_date)
    CustomTextView2 mSearchByFlightNumberDate;
    @BindView(R.id.activity_flight_search_by_flight_number_search)
    ImageView mSearchByFlightNumberSearch;

    AirportAirlineSearchFragment mAirportAirlineSearchFragment;
    DateSelectFragment mDateSelectFragment;
    FlightResultsFragment mFlightResultsFragment;
    NumberSelectFragment mNumberSelectFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);

        setUpToolbar();
        initViews();

        long tripId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_TRIP_ID, -1);
        long flightId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_ID, -1);
        int flightPosition = getIntent().getIntExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION, -1);

        mPresenter.initialize(tripId, flightId, flightPosition);
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        if(fragment instanceof AirportAirlineSearchFragment){
            mAirportAirlineSearchFragment = (AirportAirlineSearchFragment)fragment;
        }
        if(fragment instanceof DateSelectFragment){
            mDateSelectFragment = (DateSelectFragment) fragment;
        }
        if(fragment instanceof FlightResultsFragment){
            mFlightResultsFragment = (FlightResultsFragment) fragment;
        }
        if(fragment instanceof NumberSelectFragment){
            mNumberSelectFragment = (NumberSelectFragment) fragment;
        }
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

    private void initViews() {

        mAirportAirlineSearchDisposable = RxTextView.textChanges(mSearchAirportAirline)
                .skip(1)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mAirportAirlineSearchFragment != null && mPresenter.getStep() == FlightPresenter.FlightStep.SEARCHING_AIRPORTS_AIRLINES) {
                        String query = charSequence.toString().trim();
                        Timber.d("Text changed %s", query);
                        if (query.length() > 0) {
                            mAirportAirlineSearchFragment.search(query);
                        } else {
                            mAirportAirlineSearchFragment.search(null);
                        }
                    }
                });


        /* Search by Route */
        mRouteOriginClickDisposable = RxView.focusChanges(mSearchByRouteOrigin)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mSearchByRouteOrigin.hasFocus() && mPresenter.getStep() != FlightPresenter.FlightStep.ROUTE_SEARCH_SEARCHING_ORIGIN) {
                        mPresenter.resetToStep(FlightPresenter.FlightStep.ROUTE_SEARCH_SEARCHING_ORIGIN);
                        mSearchByRouteOrigin.setSelection(mSearchByRouteOrigin.getText().length());
                    }
                });
        mRouteDestinationClickDisposable = RxView.focusChanges(mSearchByRouteDestination)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mSearchByRouteDestination.hasFocus() && mPresenter.getStep() != FlightPresenter.FlightStep.ROUTE_SEARCH_SEARCHING_DESTINATION) {
                        mPresenter.resetToStep(FlightPresenter.FlightStep.ROUTE_SEARCH_SEARCHING_DESTINATION);
                        mSearchByRouteDestination.setSelection(mSearchByRouteDestination.getText().length());
                    }
                });
        mRouteDateClickDisposable = RxView.clicks(mSearchByRouteDate)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mSearchByRouteDate.hasFocus() && mPresenter.getStep() != FlightPresenter.FlightStep.ROUTE_SEARCH_SETTING_DATE)
                        mPresenter.resetToStep(FlightPresenter.FlightStep.ROUTE_SEARCH_SETTING_DATE);
                });
        mRouteOriginTextChangeDisposable = RxTextView.textChanges(mSearchByRouteOrigin)
                .skip(1)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mAirportAirlineSearchFragment != null && mPresenter.getStep() == FlightPresenter.FlightStep.ROUTE_SEARCH_SEARCHING_ORIGIN) {
                        String query = charSequence.toString().trim();
                        if (query.length() > 0) {
                            mAirportAirlineSearchFragment.search(query);
                        } else {
                            mAirportAirlineSearchFragment.search(null);
                        }
                    }
                });
        mRouteDestinationTextChangeDisposable = RxTextView.textChanges(mSearchByRouteDestination)
                .skip(1)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mAirportAirlineSearchFragment != null && mPresenter.getStep() == FlightPresenter.FlightStep.ROUTE_SEARCH_SEARCHING_DESTINATION) {
                        String query = charSequence.toString().trim();
                        if (query.length() > 0) {
                            mAirportAirlineSearchFragment.search(query);
                        } else {
                            mAirportAirlineSearchFragment.search(null);
                        }
                    }
                });
        mRouteSearchClickDisposable = RxView.clicks(mSearchByRouteSearch)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    mPresenter.searchByRoute();
                });



        /* Search by Flight Number */
        mFlightAirlineClickDisposable = RxView.focusChanges(mSearchByFlightNumberAirline)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mSearchByFlightNumberAirline.hasFocus() && mPresenter.getStep() != FlightPresenter.FlightStep.FLIGHT_SEARCH_SEARCHING_AIRLINE) {
                        mPresenter.resetToStep(FlightPresenter.FlightStep.FLIGHT_SEARCH_SEARCHING_AIRLINE);
                        mSearchByFlightNumberAirline.setSelection(mSearchByFlightNumberAirline.getText().length());
                    }
                });
        mFlightNumberClickDisposable = RxView.clicks(mSearchByFlightNumberNumber)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mSearchByFlightNumberNumber.hasFocus() && mPresenter.getStep() != FlightPresenter.FlightStep.FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER)
                        mPresenter.resetToStep(FlightPresenter.FlightStep.FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER);
                });
        mFlightDateClickDisposable = RxView.clicks(mSearchByFlightNumberDate)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mSearchByFlightNumberDate.hasFocus() && mPresenter.getStep() != FlightPresenter.FlightStep.FLIGHT_SEARCH_SETTING_DATE)
                        mPresenter.resetToStep(FlightPresenter.FlightStep.FLIGHT_SEARCH_SETTING_DATE);
                });
        mFlightAirlineTextChangeDisposable = RxTextView.textChanges(mSearchByFlightNumberAirline)
                .skip(1)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mAirportAirlineSearchFragment != null && mPresenter.getStep() == FlightPresenter.FlightStep.FLIGHT_SEARCH_SEARCHING_AIRLINE) {
                        String query = charSequence.toString().trim();
                        if (query.length() > 0) {
                            mAirportAirlineSearchFragment.search(query);
                        } else {
                            mAirportAirlineSearchFragment.search(null);
                        }
                    }
                });
        mFlightSearchClickDisposable = RxView.clicks(mSearchByFlightNumberSearch)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    mPresenter.searchByFlightNumber();
                });
    }




    /* FlightMvpView implementation */
    @Override
    public void updateViews(FlightPresenter.FlightStep step, @Nullable Flight flight) {
        handleHeaderFieldVisibility(step);
        switch (step) {

            case SEARCHING_AIRPORTS_AIRLINES:
                //Init content frame with AirportAirlineSearchFragment
                setUpContentFrame(ContentFrameType.AIRPORT_AIRLINE_SEARCH_FRAGMENT, AirportAirlineSearchType.BOTH);
                break;


            case ROUTE_SEARCH_SEARCHING_ORIGIN:
                //Load content frame
                setUpContentFrame(ContentFrameType.AIRPORT_AIRLINE_SEARCH_FRAGMENT, AirportAirlineSearchType.AIRPORT);

                //Highlight destination CustomEditText
                if (!mSearchByRouteOrigin.hasFocus())
                    mSearchByRouteOrigin.requestFocus();
                mSearchByRouteOrigin.setSelection(mSearchByRouteOrigin.getText().length());
                break;


            case ROUTE_SEARCH_SEARCHING_DESTINATION:
                //Load content frame
                setUpContentFrame(ContentFrameType.AIRPORT_AIRLINE_SEARCH_FRAGMENT, AirportAirlineSearchType.AIRPORT);

                //Highlight destination CustomEditText
                if (!mSearchByRouteDestination.hasFocus())
                    mSearchByRouteDestination.requestFocus();
                mSearchByRouteDestination.setSelection(mSearchByRouteDestination.getText().length());

                //Change title
                mToolbar.setTitle(R.string.activity_flight_toolbar_title_by_route);

                //Set origin airport
                if(!flight.getOrigin().getIata().isEmpty())
                    mSearchByRouteOrigin.setText(flight.getOrigin().getIata());
                else if (!flight.getOrigin().getName().isEmpty())
                    mSearchByRouteOrigin.setText(flight.getOrigin().getName());
                break;


            case ROUTE_SEARCH_SETTING_DATE:
                //Load content frame
                setUpContentFrame(ContentFrameType.DATE_SELECT_FRAGMENT, null);

                //Highlight date CustomTextView
                if (!mSearchByRouteDate.hasFocus())
                    mSearchByRouteDate.requestFocus();

                //Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchByRouteDate.getWindowToken(), 0);

                //Set destination airport
                if(!flight.getDestination().getIata().isEmpty())
                    mSearchByRouteDestination.setText(flight.getDestination().getIata());
                else if (!flight.getDestination().getName().isEmpty())
                    mSearchByRouteDestination.setText(flight.getDestination().getName());
                break;


            case ROUTE_SEARCH_SEARCHING_FLIGHTS:
                //TODO: Block origin, destination and date fields while searching

                //Hide search button
                mSearchByRouteSearch.setVisibility(View.GONE);

                //Show searching overlay
                mDateSelectFragment.showSearchingOverlay();
                break;


            case ROUTE_SEARCH_SELECTING_FLIGHT:
                //Load content frame
                setUpContentFrame(ContentFrameType.FLIGHT_RESULTS_FRAGMENT, null);
                break;




            //Searching by flight # workflow
            case FLIGHT_SEARCH_SEARCHING_AIRLINE:
                //Load content frame
                setUpContentFrame(ContentFrameType.AIRPORT_AIRLINE_SEARCH_FRAGMENT, AirportAirlineSearchType.AIRLINE);

                //Highlight flight airline CustomEditText
                if (!mSearchByFlightNumberAirline.hasFocus())
                    mSearchByFlightNumberAirline.requestFocus();
                mSearchByFlightNumberAirline.setSelection(mSearchByFlightNumberAirline.getText().length());
                break;


            case FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER:
                //Load content frame
                setUpContentFrame(ContentFrameType.NUMBER_SELECT_FRAGMENT, null);

                //Highlight flight number CustomEditText
                if (!mSearchByFlightNumberNumber.hasFocus())
                    mSearchByFlightNumberNumber.requestFocus();

                //Hide keyboard
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(mSearchByRouteDate.getWindowToken(), 0);

                //Change title
                mToolbar.setTitle(R.string.activity_flight_toolbar_title_by_number);

                //Set airline
                if(!flight.getAirline().getIcao().isEmpty())
                    mSearchByFlightNumberAirline.setText(flight.getAirline().getIcao());
                else if (!flight.getAirline().getName().isEmpty())
                    mSearchByFlightNumberAirline.setText(flight.getAirline().getName());
                break;


            case FLIGHT_SEARCH_SETTING_DATE:
                //Load content frame
                setUpContentFrame(ContentFrameType.DATE_SELECT_FRAGMENT, null);

                //Highlight date CustomTextView
                if (!mSearchByFlightNumberDate.hasFocus())
                    mSearchByFlightNumberDate.requestFocus();


                //Hide keyboard
                InputMethodManager imm3 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm3.hideSoftInputFromWindow(mSearchByRouteDate.getWindowToken(), 0);

                //Set destination airport
                if(!flight.getCallsign().isEmpty())
                    mSearchByFlightNumberNumber.setText(flight.getCallsign());
                break;


            case FLIGHT_SEARCH_SEARCHING_FLIGHTS:
                //TODO: Block airline, flight number and date fields while searching

                //Hide search button
                mSearchByRouteSearch.setVisibility(View.GONE);

                //Show searching overlay
                mDateSelectFragment.showSearchingOverlay();
                break;


            case FLIGHT_SEARCH_SELECTING_FLIGHT:
                //Load content frame
                setUpContentFrame(ContentFrameType.FLIGHT_RESULTS_FRAGMENT, null);
                break;
        }
    }

    @Override
    public void updateRouteFieldsWithExistingFlightInfo(Flight flight) {
        //Set origin airport
        if(!flight.getOrigin().getIata().isEmpty())
            mSearchByRouteOrigin.setText(flight.getOrigin().getIata());
        else if (!flight.getOrigin().getName().isEmpty())
            mSearchByRouteOrigin.setText(flight.getOrigin().getName());

        //Set destination airport
        if(!flight.getDestination().getIata().isEmpty())
            mSearchByRouteDestination.setText(flight.getDestination().getIata());
        else if (!flight.getDestination().getName().isEmpty());

        mSearchByRouteDate.setText(CalendarUtil.getCuteStringDateFromCalendar(flight.getDeparture()));
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
            case NUMBER_SELECT_FRAGMENT:
                int style = (new SharedPreferenceHelper().getAppThemeType() == AppThemeType.DARK ? R.style.BetterPickersDialogFragment : R.style.BetterPickersDialogFragment_Light);
                Integer defaultNumber;
                try {
                    defaultNumber = Integer.valueOf(mPresenter.getFlight().getCallsign());
                } catch (NumberFormatException e) {
                    defaultNumber = null;
                }
                NumberSelectFragment frag = NumberSelectFragment.newInstance(-1, style, new BigDecimal(1), new BigDecimal(9000), View.GONE, View.GONE, getString(R.string.fragment_number_select_title), defaultNumber, null, null);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.activity_flight_content_frame,
                        frag,
                        Constants.TAG_ACTIVITY_FLIGHT_NUMBER_SELECT_FRAGMENT)
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

    private void handleHeaderFieldVisibility(FlightPresenter.FlightStep step) {
        switch (step) {
            case SEARCHING_AIRPORTS_AIRLINES:
                mSearchByRouteContainer.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchAirportAirline.setVisibility(View.VISIBLE);
                break;


            case ROUTE_SEARCH_SEARCHING_ORIGIN:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.VISIBLE);

                mSearchByRouteOrigin.setDim(false);
                mSearchByRouteDestination.setDim(true);
                mSearchByRouteDate.setDim(true);
                mSearchByRouteSearch.setVisibility(View.GONE);
                break;

            case ROUTE_SEARCH_SEARCHING_DESTINATION:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.VISIBLE);

                mSearchByRouteOrigin.setDim(true);
                mSearchByRouteDestination.setDim(false);
                mSearchByRouteDate.setDim(true);
                mSearchByRouteSearch.setVisibility(View.GONE);
                break;

            case ROUTE_SEARCH_SETTING_DATE:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.VISIBLE);

                mSearchByRouteOrigin.setDim(true);
                mSearchByRouteDestination.setDim(true);
                mSearchByRouteDate.setDim(false);
                if(mPresenter.getFlight().getDeparture() != null)
                    mSearchByRouteSearch.setVisibility(View.VISIBLE);
                else
                    mSearchByRouteSearch.setVisibility(View.GONE);
                break;

            case ROUTE_SEARCH_SEARCHING_FLIGHTS:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.VISIBLE);

                mSearchByRouteOrigin.setDim(true);
                mSearchByRouteDestination.setDim(true);
                mSearchByRouteDate.setDim(true);
                mSearchByRouteSearch.setVisibility(View.VISIBLE);
                break;

            case ROUTE_SEARCH_SELECTING_FLIGHT:
                break;


            case FLIGHT_SEARCH_SEARCHING_AIRLINE:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.VISIBLE);

                mSearchByFlightNumberAirline.setDim(false);
                mSearchByFlightNumberNumber.setDim(true);
                mSearchByFlightNumberDate.setDim(true);
                mSearchByFlightNumberSearch.setVisibility(View.GONE);
                break;

            case FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.VISIBLE);

                mSearchByFlightNumberAirline.setDim(true);
                mSearchByFlightNumberNumber.setDim(false);
                mSearchByFlightNumberDate.setDim(true);
                mSearchByFlightNumberSearch.setVisibility(View.GONE);
                break;

            case FLIGHT_SEARCH_SETTING_DATE:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.VISIBLE);

                mSearchByFlightNumberAirline.setDim(true);
                mSearchByFlightNumberNumber.setDim(true);
                mSearchByFlightNumberDate.setDim(false);

                if(mPresenter.getFlight().getCallsign() != null)
                    mSearchByFlightNumberSearch.setVisibility(View.VISIBLE);
                else
                    mSearchByFlightNumberSearch.setVisibility(View.GONE);
                break;

            case FLIGHT_SEARCH_SEARCHING_FLIGHTS:
                mSearchAirportAirline.setVisibility(View.GONE);
                mSearchByRouteContainer.setVisibility(View.GONE);
                mSearchByFlightNumberContainer.setVisibility(View.VISIBLE);

                mSearchByFlightNumberAirline.setDim(true);
                mSearchByFlightNumberNumber.setDim(true);
                mSearchByFlightNumberDate.setDim(true);
                mSearchByFlightNumberSearch.setVisibility(View.VISIBLE);
                break;

            case FLIGHT_SEARCH_SELECTING_FLIGHT:
                break;
        }
    }

    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);
    }

    @Override
    public void tripSaved(long tripId, FlightPresenter.FlightProcedure procedure) {
        switch (procedure) {
            case NEW_FLIGHT_IN_NEW_TRIP:
                BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        finish();
                        Intent startTripActivity = new Intent(FlightActivity.this, TripDetailActivity.class);
                        startTripActivity.putExtra(Constants.EXTRA_ACTIVITY_TRIP_DETAIL_TRIP_ID, tripId);
                        startActivity(startTripActivity);
                    }
                };
                showMessage(Message.SUCCESS_FLIGHT_ADDED, callback);
            break;

            case NEW_FLIGHT_IN_EXISTING_TRIP:
                BaseTransientBottomBar.BaseCallback<Snackbar> callback2 = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        finish();
                    }
                };
                showMessage(Message.SUCCESS_FLIGHT_ADDED, callback2);
                break;

            case EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP:
                BaseTransientBottomBar.BaseCallback<Snackbar> callback3 = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        finish();
                    }
                };
                showMessage(Message.SUCCESS_FLIGHT_EDITED, callback3);
                break;

        }
    }




    /* AirportAirlineSearchFragment.AirportAirlineSelectedListener implementation */
    @Override
    public void onAirportAirlineSelected(AirportAirlineItem item) {
        mPresenter.airportOrAirlineSelected(item);
    }

    @Override
    public @Nullable String getInitialQueryOrShowRecents() {
        switch (mPresenter.getStep()) {
            case SEARCHING_AIRPORTS_AIRLINES:
                return null; //Show recents.
            case ROUTE_SEARCH_SEARCHING_ORIGIN:
                String orig = mSearchByRouteOrigin.getText().toString().trim();
                if(!orig.isEmpty())
                    return orig;
                else return null;

            case ROUTE_SEARCH_SEARCHING_DESTINATION:
                String dest = mSearchByRouteDestination.getText().toString().trim();
                if(!dest.isEmpty())
                    return dest;
                else return null;

            case FLIGHT_SEARCH_SEARCHING_AIRLINE:
                String airline = mSearchByFlightNumberAirline.getText().toString().trim();
                if(!airline.isEmpty())
                    return airline;
                else return null;
            default:
                throw new IllegalStateException("getInitialQueryOrShowRecents: Step is illegal. Step=" + mPresenter.getStep().name());
        }
    }




    /* DateSelectFragment.DateSelectedListener implementation */
    @Override
    public void onDateSelected(Calendar calendar) {
        switch (mPresenter.getStep()) {
            case ROUTE_SEARCH_SETTING_DATE:
                mSearchByRouteDate.setText(CalendarUtil.getCuteStringDateFromCalendar(calendar));
                mSearchByRouteSearch.setVisibility(View.VISIBLE);
                break;
            case FLIGHT_SEARCH_SETTING_DATE:
                mSearchByFlightNumberDate.setText(CalendarUtil.getCuteStringDateFromCalendar(calendar));
                mSearchByFlightNumberSearch.setVisibility(View.VISIBLE);
                break;
            default:
                throw new IllegalStateException("onDateSelected: Step is illegal. Step=" + mPresenter.getStep().name());
        }

        mPresenter.dateSelected(calendar);
    }
    @Nullable
    @Override
    public Calendar getInitialDateOrNone() {
        switch (mPresenter.getStep()) {
            case ROUTE_SEARCH_SETTING_DATE:
            case FLIGHT_SEARCH_SETTING_DATE:
                return mPresenter.getFlight().getDeparture();
            default:
                throw new IllegalStateException("getInitialDateOrNone: Step is illegal. Step=" + mPresenter.getStep().name());
        }
    }




    /* NumberSelectFragment.NumberEnteredListener implementation */
    @Override
    public void onNumberEntered(int number) {
        mPresenter.flightNumberSet(number);
    }




    /* FlightResultsFragment.FlightSelectedListener implementation */
    @Override
    public void onFlightSelected(Flight flight) {
        mPresenter.flightSelected(flight);
    }
    @Override
    public List<Flight> getFlights() {
        return mPresenter.getTempFlights();
    }
}
