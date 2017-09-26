package ve.com.abicelis.planetracker.ui.common.flight;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.FlightHeader;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;

/**
 * Created by abicelis on 17/9/2017.
 */

public class FlightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //DATA
    private List<FlightViewModel> mFlights = new ArrayList<>();
    private long mTripId;
    private FlightClickedListener mListener;
    private LayoutInflater mInflater;
    private Activity mActivity;
    private String mLayoverFormat;
    private boolean mIsInEditMode;
    private boolean mUseSmallViews;


    public FlightAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mLayoverFormat = mActivity.getString(R.string.flight_view_model_layover_format);
    }

    @Override
    public int getItemViewType(int position) {
        return mFlights.get(position).getFlightViewModelType().ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FlightViewModel.FlightViewModelType type = FlightViewModel.FlightViewModelType.values()[viewType];

        switch (type) {
            case HEADER_ERROR_DEPARTURE_BEFORE_ARRIVAL:
            case HEADER_ERROR_DIFFERENT_ORIGIN_DESTINATION:
                return new FlightHeaderErrorViewHolder(mInflater.inflate(R.layout.list_item_flight_header_error, parent, false));
            case HEADER_EDIT_ONLY:
                return new FlightHeaderEditOnlyViewHolder(mInflater.inflate(R.layout.list_item_flight_header_edit_only, parent, false));
            case HEADER_LAYOVER:
                return new FlightHeaderViewHolder(mInflater.inflate(R.layout.list_item_flight_header, parent, false));
            case FLIGHT:
                if(mUseSmallViews)
                    return new FlightViewHolderSmall(mInflater.inflate(R.layout.list_item_flight_small, parent, false));
                else
                    return new FlightViewHolder(mInflater.inflate(R.layout.list_item_flight, parent, false));
        }
        throw new InvalidParameterException("Wrong Trip Adapter viewType!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FlightViewModel current = mFlights.get(position);
        switch (current.getFlightViewModelType()) {
            case HEADER_ERROR_DEPARTURE_BEFORE_ARRIVAL:
            case HEADER_ERROR_DIFFERENT_ORIGIN_DESTINATION:
                FlightHeaderErrorViewHolder fev = (FlightHeaderErrorViewHolder)holder;
                fev.setData(mActivity, this, current, position);
                fev.setListeners();
                break;

            case HEADER_EDIT_ONLY:
                FlightHeaderEditOnlyViewHolder fe = (FlightHeaderEditOnlyViewHolder)holder;
                fe.setData(mActivity, this, current.getHeader(), position);
                fe.setListeners();
                break;

            case HEADER_LAYOVER:
                FlightHeaderViewHolder fh = (FlightHeaderViewHolder)holder;
                fh.setData(mActivity, this, current.getHeader(), mLayoverFormat, position);
                fh.setListeners();
                break;

            case FLIGHT:
                if(mUseSmallViews) {
                    FlightViewHolderSmall f = (FlightViewHolderSmall) holder;
                    f.setData(this, mActivity, current.getFlight(), position);
                    f.setListeners();
                } else {
                    FlightViewHolder f = (FlightViewHolder) holder;
                    f.setData(this, mActivity, current.getFlight(), position);
                    f.setListeners();
                }
                break;
        }

    }

    public boolean isInEditMode() {
        return mIsInEditMode;
    }
    public void toggleEditMode(boolean toggle) {
        if(toggle) {
            mIsInEditMode = true;
            doToggleItems(FlightHeader.State.EDIT);
        } else {
            mIsInEditMode = false;
            doToggleItems(FlightHeader.State.HEADER);
        }
    }

    private void doToggleItems(FlightHeader.State state) {
        for (int i=0; i<getItemCount(); i++) {
            FlightViewModel f = mFlights.get(i);
            if (f.getFlightViewModelType() != FlightViewModel.FlightViewModelType.FLIGHT) {
                f.getHeader().setState(state);
                notifyItemChanged(i);
            }
        }
    }

    public void useSmallViews(boolean useSmallViews) {
        mUseSmallViews = useSmallViews;
    }

    public List<FlightViewModel> getItems() {
        return mFlights;
    }

    @Override
    public int getItemCount() {
        return mFlights.size();
    }

    public void setTripId(long tripId) {
        mTripId = tripId;
    }
    public long getTripId() {
        return mTripId;
    }


    public void triggerFlightClicked(Flight flight) {
        if (mListener != null)
            mListener.onFlightClicked(flight);
    }
    public void triggerFlightLongClicked(Flight flight) {
        if (mListener != null)
            mListener.onFlightLongClicked(flight);
    }

    public void setFlightClickedListener(FlightClickedListener listener) {
        mListener = listener;
    }

    public interface FlightClickedListener {
        void onFlightClicked(Flight flight);
        void onFlightLongClicked(Flight flight);
    }
}
