package ve.com.abicelis.planetracker.ui.common.flight;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.TimeFormat;
import ve.com.abicelis.planetracker.util.CalendarUtil;
import ve.com.abicelis.planetracker.util.TimezoneUtil;

/**
 * Created by abicelis on 17/9/2017.
 */

public class FlightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    //DATA
    private FlightAdapter mAdapter;
    private Activity mActivity;
    private Flight mCurrent;
    private int mPosition;
    private TimeFormat mTimeFormat;

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


    public FlightViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mTimeFormat = new SharedPreferenceHelper().getTimeFormat();
    }

    public void setData(FlightAdapter adapter, Activity activity, Flight current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;


        mAirline.setText(mCurrent.getAirline().getName());
        mCallsign.setText(mCurrent.getCallsign());
        mDate.setText(CalendarUtil.getCuteStringDateFromCalendar(mCurrent.getDeparture()));

        mFromCity.setText(mCurrent.getOrigin().getCity());
        mFromAirportCode.setText(mCurrent.getOrigin().getIata());
        mFromAirportName.setText(mCurrent.getOrigin().getName());

        if(mCurrent.getDeparture() != null && mCurrent.getArrival() != null)
            mFlightDuration.setText(CalendarUtil.getCuteTimeStringBetweenCalendars(mCurrent.getDeparture(), mCurrent.getArrival()));

        mToCity.setText(mCurrent.getDestination().getCity());
        mToAirportCode.setText(mCurrent.getDestination().getIata());
        mToAirportName.setText(mCurrent.getDestination().getName());

        mFlightDepart.setText(mTimeFormat.formatCalendar(mCurrent.getDeparture()));
        mFlightDepartGmt.setText(TimezoneUtil.getTimezoneStringFromTimezoneOffset(mCurrent.getOrigin().getTimezoneOffset()));
        mFlightArrive.setText(mTimeFormat.formatCalendar(mCurrent.getArrival()));
        mFlightArriveGmt.setText(TimezoneUtil.getTimezoneStringFromTimezoneOffset(mCurrent.getDestination().getTimezoneOffset()));

    }

    public void setListeners() {

        mContainer.setOnClickListener(this);
        mContainer.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.list_item_flight_container:
                mAdapter.triggerFlightClicked(mCurrent);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.list_item_flight_container:
                mAdapter.triggerFlightLongClicked(mCurrent);
                return true;
        }
        return false;
    }
}