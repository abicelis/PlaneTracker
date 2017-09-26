package ve.com.abicelis.planetracker.ui.common.flight;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.model.FlightHeader;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;
import ve.com.abicelis.planetracker.ui.flight.FlightActivity;
import ve.com.abicelis.planetracker.ui.tripdetail.TripDetailActivity;
import ve.com.abicelis.planetracker.util.AnimationUtil;

/**
 * Created by abicelis on 17/9/2017.
 */

public class FlightHeaderErrorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //DATA
    private Activity mActivity;
    private FlightAdapter mAdapter;
    private FlightViewModel mCurrent;
    private int mPosition;


    //UI
    @BindView(R.id.list_item_flight_header_error_add_flight_container)
    FrameLayout mAddFlightContainer;
    @BindView(R.id.list_item_flight_header_error_add_flight)
    Button mAddFlight;
    @BindView(R.id.list_item_flight_header_error_content)
    LinearLayout mHeaderContent;
    @BindView(R.id.list_item_flight_header_error_title)
    TextView mHeaderTitle;


    public FlightHeaderErrorViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(Activity activity, FlightAdapter adapter, FlightViewModel current, int position) {
        mActivity = activity;
        mAdapter = adapter;
        mCurrent = current;
        mPosition = position;

        if(mAdapter.isInEditMode()) {
            if(mCurrent.getHeader().getState() == FlightHeader.State.HEADER)
                AnimationUtil.startAlphaAnimation(mAddFlightContainer, Constants.ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
            else
                mAddFlightContainer.setVisibility(View.VISIBLE);

            mCurrent.getHeader().setState(FlightHeader.State.EDIT);
        } else {
            if(mCurrent.getHeader().getState() == FlightHeader.State.EDIT)
                AnimationUtil.startAlphaAnimation(mAddFlightContainer, Constants.ALPHA_ANIMATIONS_DURATION, View.GONE);
            else
                mAddFlightContainer.setVisibility(View.GONE);

            mCurrent.getHeader().setState(FlightHeader.State.HEADER);

            switch (mCurrent.getFlightViewModelType()) {
                case HEADER_ERROR_DEPARTURE_BEFORE_ARRIVAL:
                    mHeaderTitle.setText(mActivity.getText(R.string.activity_trip_header_error_departure_before_arrival));
                    break;

                case HEADER_ERROR_DIFFERENT_ORIGIN_DESTINATION:
                    mHeaderTitle.setText(mActivity.getText(R.string.activity_trip_header_error_different_origin_destination));
                    break;
            }
        }
    }

    public void setListeners() {
        mAddFlight.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.list_item_flight_header_error_add_flight:
                if(mActivity instanceof TripDetailActivity)
                    ((TripDetailActivity)mActivity).editModeToggled();

                Intent addFlightIntent = new Intent(mActivity, FlightActivity.class);
                addFlightIntent.putExtra(Constants.EXTRA_ACTIVITY_FLIGHT_TRIP_ID, mAdapter.getTripId());
                addFlightIntent.putExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION, mPosition/2);
                mActivity.startActivity(addFlightIntent);
                break;
        }
    }
}