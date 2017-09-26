package ve.com.abicelis.planetracker.ui.flight.flightresultsfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;
import ve.com.abicelis.planetracker.ui.common.flight.FlightAdapter;

/**
 * Created by abicelis on 22/9/2017.
 */

public class FlightResultsFragment extends Fragment {

    //DATA
    FlightSelectedListener mListener;

    //UI
    @BindView(R.id.fragment_flight_results_container)
    FrameLayout mContainer;
    @BindView(R.id.fragment_flight_results_recyclerview)
    RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private FlightAdapter mFlightAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight_results, container, false);
        ButterKnife.bind(this, view);

        setUpRecyclerView();

        //Get flights from activity
        setFlights(mListener.getFlights());

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Make sure the container activity has implements
        // AirportAirlineSelectedListener. If not, throw an exception
        try {
            mListener = (FlightSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FlightSelectedListener");
        }
    }

    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mFlightAdapter = new FlightAdapter(getActivity());
        //mFlightAdapter.useSmallViews(true);
        mFlightAdapter.setFlightClickedListener(new FlightAdapter.FlightClickedListener() {
            @Override
            public void onFlightClicked(Flight flight) {
                //Relay event to activity
                mListener.onFlightSelected(flight);
            }

            @Override
            public void onFlightLongClicked(Flight flight) {
                //Relay event to activity
                mListener.onFlightSelected(flight);
            }
        });

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mFlightAdapter);
    }

    public void setFlights(List<Flight> flights) {
        List<FlightViewModel> items = new ArrayList<>();
        for (Flight f : flights) {
            items.add(new FlightViewModel(f));

            mFlightAdapter.getItems().clear();
            mFlightAdapter.getItems().addAll(items);
        }
    }



    public interface FlightSelectedListener {
        void onFlightSelected(Flight flight);
        List<Flight> getFlights();

    }
}
