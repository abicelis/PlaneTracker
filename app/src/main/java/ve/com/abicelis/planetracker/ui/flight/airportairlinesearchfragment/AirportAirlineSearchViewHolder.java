package ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;

/**
 * Created by abicelis on 17/9/2017.
 */

public class AirportAirlineSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //DATA
    private AirportAirlineSearchAdapter mAdapter;
    private Activity mActivity;
    private AirportAirlineItem mCurrent;
    private int mPosition;

    //UI
    @BindView(R.id.list_item_airport_airline_container)
    RelativeLayout mContainer;
    @BindView(R.id.list_item_airport_airline_icon)
    ImageView mIcon;
    @BindView(R.id.list_item_airport_airline_title)
    TextView mTitle;
    @BindView(R.id.list_item_airport_airline_subtitle)
    TextView mSubTitle;


    public AirportAirlineSearchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(AirportAirlineSearchAdapter adapter, Activity activity, AirportAirlineItem current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        if(current instanceof Airport) {
            mIcon.setImageResource(R.drawable.ic_airport);
            Airport airport = (Airport) mCurrent;
            mTitle.setText(String.format(Locale.getDefault(), "%1$s, %2$s - %3$s", airport.getCity(), airport.getCountry(), airport.getIata()));
            mSubTitle.setVisibility(View.VISIBLE);
            mSubTitle.setText(airport.getName());

        } else if (current instanceof Airline) {
            mIcon.setImageResource(R.drawable.ic_plane);
            Airline airline = (Airline) mCurrent;
            mTitle.setText(String.format(Locale.getDefault(), "%1$s - %2$s", airline.getIcao(), airline.getName()));
            mSubTitle.setVisibility(View.GONE);

        }
    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.list_item_airport_airline_container:
                mAdapter.triggerItemSelected(mCurrent);
                break;
        }
    }
}