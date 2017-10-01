package ve.com.abicelis.planetracker.ui.tracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.FlightProgressViewModel;
import ve.com.abicelis.planetracker.data.model.TimeFormat;
import ve.com.abicelis.planetracker.ui.customviews.AirplaneProgressView;
import ve.com.abicelis.planetracker.util.CalendarUtil;
import ve.com.abicelis.planetracker.util.TimezoneUtil;
import ve.com.abicelis.planetracker.util.ViewUtil;

/**
 * Created by abicelis on 30/9/2017.
 */

public class FlightFragment extends Fragment {

    //CONST
    public static final String ARGUMENT_FLIGHT_PROGRESS_VIEW_MODEL = "ARGUMENT_FLIGHT_PROGRESS_VIEW_MODEL";

    //DATA
    private FlightProgressViewModel mFlightViewModel;

    //UI
    @BindView(R.id.list_item_flight_container)
    CardView mContainer;
    @BindView(R.id.list_item_flight_airline)
    TextView mAirline;
    @BindView(R.id.list_item_flight_callsign)
    TextView mCallsign;
    @BindView(R.id.list_item_flight_date)
    TextView mDate;

    @BindView(R.id.list_item_flight_from_city)
    TextView mFromCity;
    @BindView(R.id.list_item_flight_from_airport_code)
    TextView mFromAirportCode;
    @BindView(R.id.list_item_flight_from_airport_name)
    TextView mFromAirportName;

    @BindView(R.id.list_item_flight_duration)
    TextView mFlightDuration;

    @BindView(R.id.list_item_flight_to_city)
    TextView mToCity;
    @BindView(R.id.list_item_flight_to_airport_code)
    TextView mToAirportCode;
    @BindView(R.id.list_item_flight_to_airport_name)
    TextView mToAirportName;

    @BindView(R.id.list_item_flight_depart)
    TextView mFlightDepart;
    @BindView(R.id.list_item_flight_depart_gmt)
    TextView mFlightDepartGmt;
    @BindView(R.id.list_item_flight_arrive)
    TextView mFlightArrive;
    @BindView(R.id.list_item_flight_arrive_gmt)
    TextView mFlightArriveGmt;

    @BindView(R.id.fragment_tracker_bottom_progress)
    AirplaneProgressView mProgressView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker_bottom, container, false);
        ButterKnife.bind(this, view);

        mFlightViewModel = (FlightProgressViewModel) getArguments().getSerializable(ARGUMENT_FLIGHT_PROGRESS_VIEW_MODEL);

        setUpFlightView();
        setUpAirplaneProgressView();

        return view;
    }

    private void setUpAirplaneProgressView() {
        mProgressView.setProgressModel(mFlightViewModel);
    }

    private void setUpFlightView() {
        int padding = (int)ViewUtil.convertDpToPixel(16);
        mContainer.setPadding(padding, 0, padding, padding);

        mAirline.setText(mFlightViewModel.getFlight().getAirline().getName());
        mCallsign.setText(mFlightViewModel.getFlight().getCallsign());
        mDate.setText(CalendarUtil.getCuteStringDateFromCalendar(mFlightViewModel.getFlight().getDeparture()));

        mFromCity.setText(mFlightViewModel.getFlight().getOrigin().getCity());
        mFromAirportCode.setText(mFlightViewModel.getFlight().getOrigin().getIata());
        mFromAirportName.setText(mFlightViewModel.getFlight().getOrigin().getName());

        if(mFlightViewModel.getFlight().getDeparture() != null && mFlightViewModel.getFlight().getArrival() != null)
            mFlightDuration.setText(CalendarUtil.getCuteTimeStringBetweenCalendars(mFlightViewModel.getFlight().getDeparture(), mFlightViewModel.getFlight().getArrival()));

        mToCity.setText(mFlightViewModel.getFlight().getDestination().getCity());
        mToAirportCode.setText(mFlightViewModel.getFlight().getDestination().getIata());
        mToAirportName.setText(mFlightViewModel.getFlight().getDestination().getName());

        TimeFormat timeFormat = new SharedPreferenceHelper().getTimeFormat();
        mFlightDepart.setText(timeFormat.formatCalendar(mFlightViewModel.getFlight().getDeparture()));
        mFlightDepartGmt.setText(TimezoneUtil.getTimezoneStringFromTimezoneOffset(mFlightViewModel.getFlight().getOrigin().getTimezoneOffset()));
        mFlightArrive.setText(timeFormat.formatCalendar(mFlightViewModel.getFlight().getArrival()));
        mFlightArriveGmt.setText(TimezoneUtil.getTimezoneStringFromTimezoneOffset(mFlightViewModel.getFlight().getDestination().getTimezoneOffset()));
    }
}
