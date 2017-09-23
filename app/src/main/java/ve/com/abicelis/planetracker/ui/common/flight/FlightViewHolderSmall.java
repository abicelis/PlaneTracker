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

public class FlightViewHolderSmall extends RecyclerView.ViewHolder implements View.OnClickListener {

    //DATA
    private FlightAdapter mAdapter;
    private Activity mActivity;
    private Flight mCurrent;
    private int mPosition;
    private TimeFormat mTimeFormat;

    //UI
    @BindView(R.id.list_item_flight_small_container)
    CardView mContainer;
    @BindView(R.id.list_item_flight_small_airline)
    TextView mAirline;
    @BindView(R.id.list_item_flight_small_callsign)
    TextView mCallsign;


    @BindView(R.id.list_item_flight_small_duration)
    TextView mFlightDuration;

    @BindView(R.id.list_item_flight_small_depart)
    TextView mFlightDepart;
    @BindView(R.id.list_item_flight_small_depart_gmt)
    TextView mFlightDepartGmt;
    @BindView(R.id.list_item_flight_small_arrive)
    TextView mFlightArrive;
    @BindView(R.id.list_item_flight_small_arrive_gmt)
    TextView mFlightArriveGmt;


    public FlightViewHolderSmall(View itemView) {
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

        if(mCurrent.getDeparture() != null && mCurrent.getArrival() != null)
            mFlightDuration.setText(CalendarUtil.getCuteTimeStringBetweenCalendars(mCurrent.getDeparture(), mCurrent.getArrival()));

        mFlightDepart.setText(mTimeFormat.formatCalendar(mCurrent.getDeparture()));
        mFlightDepartGmt.setText(TimezoneUtil.getTimezoneStringFromTimezoneOffset(mCurrent.getOrigin().getTimezoneOffset()));
        mFlightArrive.setText(mTimeFormat.formatCalendar(mCurrent.getArrival()));
        mFlightArriveGmt.setText(TimezoneUtil.getTimezoneStringFromTimezoneOffset(mCurrent.getDestination().getTimezoneOffset()));

    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.list_item_flight_small_container:
                mAdapter.triggerFlightClicked(mCurrent);
                break;
        }
    }
}