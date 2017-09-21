package ve.com.abicelis.planetracker.ui.flight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.AirportAirlineSearchType;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.customviews.CustomEditText;
import ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment.AirportAirlineSearchFragment;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 19/9/2017.
 */

public class FlightActivity extends BaseActivity implements FlightMvpView, AirportAirlineSearchFragment.AirportAirlineSelectedListener{

    //DATA
    @Inject
    FlightPresenter mPresenter;

    //UI
    @BindView(R.id.activity_flight_container)
    LinearLayout mContainer;
    @BindView(R.id.activity_flight_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_flight_query_airport_airline)
    CustomEditText mQueryAirportAirline;

    AirportAirlineSearchFragment mAirportAirlineSearchFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);

        setUpToolbar();

        //Init content frame with AirportAirlineSearchFragment
        getSupportFragmentManager().beginTransaction().add(
                R.id.activity_flight_content_frame,
                new AirportAirlineSearchFragment(),
                Constants.TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT)
                .commit();

//        //Capture fragment
//        mAirportAirlineSearchFragment =
//                (AirportAirlineSearchFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT);

        long tripId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_TRIP_ID, -1);
        long flightId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_ID, -1);
        int flightPosition = getIntent().getIntExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION, -1);

        mPresenter.initialize(tripId, flightId, flightPosition);



        RxTextView.textChanges(mQueryAirportAirline.getInternalEditText())
                .skip(1)
                .debounce(Constants.UI_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    if(mAirportAirlineSearchFragment != null) {
                        Timber.d("Text changed %s", charSequence.toString());
                        String query = mQueryAirportAirline.getText().trim();
                        if (query.length() > 0) {
                            mAirportAirlineSearchFragment.search(query, AirportAirlineSearchType.BOTH);
                        } else {
                            mAirportAirlineSearchFragment.search(null, AirportAirlineSearchType.BOTH);
                        }
                    }
                });
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Timber.d("onAttachFragment TAG=%s", fragment.getTag());

        if(fragment instanceof AirportAirlineSearchFragment){
            mAirportAirlineSearchFragment = (AirportAirlineSearchFragment)fragment;
        }
    }

    private void setUpToolbar() {
        //Setup toolbar
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.activity_flight_toolbar_title));
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


    /* FlightMvpView implementation */
    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);

    }


    /* AirportAirlineSearchFragment.AirportAirlineSelectedListener */
    @Override
    public void onAirportAirlineSelected(AirportAirlineItem item) {
        Timber.d("Airport or airline selected: %s", item.toString());
        Toast.makeText(this, "Airport or airline selected: " + item.toString(), Toast.LENGTH_LONG).show();
    }
}
