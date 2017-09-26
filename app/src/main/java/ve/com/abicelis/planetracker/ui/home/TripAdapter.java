package ve.com.abicelis.planetracker.ui.home;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.data.model.TripViewModel;

/**
 * Created by abicelis on 4/9/2017.
 */

public class TripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //DATA
    private List<TripViewModel> mTrips = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity mActivity;
    private TripDeletedListener mListener;


    public TripAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getItemViewType(int position) {
        return mTrips.get(position).getTripViewModelType().ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TripViewModel.TripViewModelType type = TripViewModel.TripViewModelType.values()[viewType];

        switch (type) {
            case HEADER_CURRENT:
            case HEADER_UPCOMING:
            case HEADER_PAST:
                return new TripHeaderViewHolder(mInflater.inflate(R.layout.list_item_trip_header, parent, false));
            case TRIP:
                return new TripViewHolder(mInflater.inflate(R.layout.list_item_trip, parent, false));
        }
        throw new InvalidParameterException("Wrong Trip Adapter viewType!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TripViewModel current = mTrips.get(position);
        switch (current.getTripViewModelType()) {
            case HEADER_CURRENT:
                TripHeaderViewHolder thvhc = (TripHeaderViewHolder)holder;
                thvhc.setData(mActivity.getString(R.string.activity_home_trip_item_current));
                break;
            case HEADER_UPCOMING:
                TripHeaderViewHolder thvhu = (TripHeaderViewHolder)holder;
                thvhu.setData(mActivity.getString(R.string.activity_home_trip_item_upcoming));
                break;
            case HEADER_PAST:
                TripHeaderViewHolder thvhp = (TripHeaderViewHolder)holder;
                thvhp.setData(mActivity.getString(R.string.activity_home_trip_item_past));
                break;
            case TRIP:
                TripViewHolder tvh = (TripViewHolder)holder;
                tvh.setData(this, mActivity, current.getTrip(), position);
                tvh.setListeners();
                break;
        }

    }

    public List<TripViewModel> getItems() {
        return mTrips;
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }





    void triggerTripDeleted(Trip trip) {
        if(mListener != null)
            mListener.onTripDeleted(trip);
    }

    void setTripDeletedListener(TripDeletedListener listener) {
        mListener = listener;
    }

    public interface TripDeletedListener {
        void onTripDeleted(Trip trip);
    }
}
