package ve.com.abicelis.planetracker.ui.flight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.AirportAirlineSearchType;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment.AirportAirlineSearchFragment;

/**
 * Created by abicelis on 19/9/2017.
 */

public class FlightActivity extends BaseActivity implements FlightMvpView, AirportAirlineSearchFragment.AirportAirlineSelectedListener{

    //DATA
    @Inject
    FlightPresenter mPresenter;

    //UI
    @BindView(R.id.activity_flight_text_query)
    EditText mQuery;
    @BindView(R.id.activity_flight_search)
    Button mSearch;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);

        //Init content frame with AirportAirlineSearchFragment
        getSupportFragmentManager().beginTransaction().add(
                R.id.activity_flight_content_frame,
                new AirportAirlineSearchFragment(),
                Constants.TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT)
                .commit();

        long tripId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_TRIP_ID, -1);
        long flightId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_ID, -1);
        int flightPosition = getIntent().getIntExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION, -1);

        mPresenter.initialize(tripId, flightId, flightPosition);


        mSearch.setOnClickListener(view -> {
            String query = mQuery.getText().toString().trim();
            if(!query.isEmpty()) {
                AirportAirlineSearchFragment fragment =
                        (AirportAirlineSearchFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT);
                fragment.search(query, AirportAirlineSearchType.BOTH);
            } else {
                Toast.makeText(this, "Empty query!", Toast.LENGTH_SHORT).show();
            }
        });

    }





    /* FlightMvpView implementation */
    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {

    }


    /* AirportAirlineSearchFragment.AirportAirlineSelectedListener */
    @Override
    public void onAirportAirlineSelected(AirportAirlineItem item) {
        Timber.d("Airport or airline selected: %s", item.toString());
        Toast.makeText(this, "Airport or airline selected", Toast.LENGTH_SHORT).show();
    }
}
