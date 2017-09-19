package ve.com.abicelis.planetracker.ui.common;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.model.FlightHeader;
import ve.com.abicelis.planetracker.util.AnimationUtil;

/**
 * Created by abicelis on 17/9/2017.
 */

public class FlightHeaderEditOnlyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //DATA
    private Activity mActivity;
    private FlightAdapter mAdapter;
    private FlightHeader mCurrent;
    private int mPosition;


    //UI
    @BindView(R.id.list_item_flight_header_edit_only_container)
    RelativeLayout mContainer;
    @BindView(R.id.list_item_flight_header_edit_only_add_flight)
    Button mAddFlight;

    public FlightHeaderEditOnlyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(Activity activity, FlightAdapter adapter, FlightHeader current, int position) {
        mActivity = activity;
        mAdapter = adapter;
        mCurrent = current;
        mPosition = position;

        if(mAdapter.isInEditMode()) {
            if(mCurrent.getState() == FlightHeader.State.HEADER)
                AnimationUtil.startAlphaAnimation(mContainer, Constants.ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
             else
                mContainer.setVisibility(View.VISIBLE);

            mAddFlight.setVisibility(View.VISIBLE);
            mCurrent.setState(FlightHeader.State.EDIT);
        } else {
            if(mCurrent.getState() == FlightHeader.State.EDIT)
                AnimationUtil.startAlphaAnimation(mContainer, Constants.ALPHA_ANIMATIONS_DURATION, View.GONE);
            else
                mContainer.setVisibility(View.GONE);

            mAddFlight.setVisibility(View.GONE);
            mCurrent.setState(FlightHeader.State.HEADER);
        }
    }

    public void setListeners() {
        mAddFlight.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.list_item_flight_header_add_flight:
                Toast.makeText(mActivity, "Adding a flight on pos" + mPosition, Toast.LENGTH_SHORT).show();
        }
    }
}