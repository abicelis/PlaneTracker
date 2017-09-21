package ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.FlightHeader;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;
import ve.com.abicelis.planetracker.ui.common.flight.FlightHeaderEditOnlyViewHolder;
import ve.com.abicelis.planetracker.ui.common.flight.FlightHeaderViewHolder;
import ve.com.abicelis.planetracker.ui.common.flight.FlightViewHolder;

/**
 * Created by abicelis on 17/9/2017.
 */

public class AirportAirlineSearchAdapter extends RecyclerView.Adapter<AirportAirlineSearchViewHolder> {

    //DATA
    private List<AirportAirlineItem> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity mActivity;
    private ItemSelectedListener mListener;


    public AirportAirlineSearchAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public AirportAirlineSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AirportAirlineSearchViewHolder(mInflater.inflate(R.layout.list_item_airport_airline, parent, false));
    }

    @Override
    public void onBindViewHolder(AirportAirlineSearchViewHolder holder, int position) {
        holder.setData(this, mActivity, mItems.get(position), position);
        holder.setListeners();
    }

    public List<AirportAirlineItem> getItems() {
        return mItems;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }




    void triggerItemSelected(AirportAirlineItem item) {
        if (mListener != null)
            mListener.onItemSelected(item);
    }

    void setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        mListener = itemSelectedListener;
    }

    public interface ItemSelectedListener{
        void onItemSelected(AirportAirlineItem item);
    }
}
