package ve.com.abicelis.planetracker.ui.common;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;

/**
 * Created by abicelis on 17/9/2017.
 */

public class FlightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //DATA
    private List<FlightViewModel> mFlights = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity mActivity;
    private String mLayoverFormat;


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
            case HEADER_LAYOVER:
                return new FlightHeaderViewHolder(mInflater.inflate(R.layout.list_item_flight_header, parent, false));
            case FLIGHT:
                return new FlightViewHolder(mInflater.inflate(R.layout.list_item_flight, parent, false));
        }
        throw new InvalidParameterException("Wrong Trip Adapter viewType!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FlightViewModel current = mFlights.get(position);
        switch (current.getFlightViewModelType()) {
            case HEADER_LAYOVER:
                FlightHeaderViewHolder fh = (FlightHeaderViewHolder)holder;
                fh.setData(current.getHeaderTitle(), mLayoverFormat, current.getHeaderLayover());
                break;
            case FLIGHT:
                FlightViewHolder f = (FlightViewHolder)holder;
                f.setData(this, mActivity, current.getFlight(), position);
                break;
        }

    }

    public List<FlightViewModel> getItems() {
        return mFlights;
    }

    @Override
    public int getItemCount() {
        return mFlights.size();
    }
}
