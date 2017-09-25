package ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.AirportAirlineHeader;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.AirportAirlineSearchType;
import ve.com.abicelis.planetracker.data.model.AppThemeType;
import ve.com.abicelis.planetracker.ui.base.BaseDialogFragment;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 20/9/2017.
 */

public class AirportAirlineSearchFragment extends BaseDialogFragment implements AirportAirlineSearchMvpView {

    //DATA
    @Inject
    AirportAirlineSearchPresenter mPresenter;
    AirportAirlineSelectedListener mListener;
    private static final String EXTRA_SEARCH_TYPE = "EXTRA_SEARCH_TYPE";

    //UI
    @BindView(R.id.fragment_airport_airline_search_container)
    FrameLayout mContainer;

    @BindView(R.id.fragment_airport_airline_search_no_items_container)
    RelativeLayout mNoItemsContainer;
    @BindView(R.id.fragment_airport_airline_search_no_items_message)
    TextView mNoItemsMessage;
    @BindView(R.id.fragment_airport_airline_search_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.fragment_airport_airline_search_recyclerview)
    RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private AirportAirlineSearchAdapter mAdapter;

    public static AirportAirlineSearchFragment getInstance(@NonNull AirportAirlineSearchType searchType) {
        AirportAirlineSearchFragment frag = new AirportAirlineSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_SEARCH_TYPE, searchType);
        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);
        mPresenter.setSearchType((AirportAirlineSearchType) getArguments().getSerializable(EXTRA_SEARCH_TYPE));
        String query = mListener.getInitialQueryOrShowRecents();
        search(query);
//        if (query == null) {
//            mPresenter.getRecents();
//        } else {
//            mPresenter.search(query);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_airport_airline_search, container, false);
        ButterKnife.bind(this, view);
        setUpRecyclerView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Make sure the container activity has implements
        // AirportAirlineSelectedListener. If not, throw an exception
        try {
            mListener = (AirportAirlineSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AirportAirlineSelectedListener");
        }
    }

    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new AirportAirlineSearchAdapter(getActivity());
        mAdapter.setItemSelectedListener(item -> {
            //Relay event to activity
            mListener.onAirportAirlineSelected(item);
        });

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), mLayoutManager.getOrientation());

        int divider = (new SharedPreferenceHelper().getAppThemeType() == AppThemeType.LIGHT ? R.drawable.item_decoration : R.drawable.item_decoration_dark);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), divider));
        mRecycler.addItemDecoration(itemDecoration);

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);

        mSwipeRefresh.setEnabled(false);
        mSwipeRefresh.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
    }


    /**
     * Trigger a search of AirportAirlineSearchType, with a specific string query.
     * Note: {@code query} can be null, in which case the list will be cleared.
     */
    public void search(@Nullable String query) {
        if (query == null)
            //clearItems();
            mPresenter.getRecents();
        else
            mPresenter.search(query);
    }


    /* AirportAirlineSearchMvpView implementation */


    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);

    }

    @Override
    public void showLoading() {
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void clearItems() {
        mSwipeRefresh.setRefreshing(false);
        mAdapter.getItems().clear();
        mAdapter.notifyDataSetChanged();
        mNoItemsMessage.setText(R.string.notice_no_airports_or_airlines_found);
        mNoItemsContainer.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
    }

    @Override
    public void showItems(List<AirportAirlineItem> items, AirportAirlineSearchType type) {
        mSwipeRefresh.setRefreshing(false);
        mAdapter.getItems().clear();
        mAdapter.getItems().addAll(items);
        mAdapter.notifyDataSetChanged();

        if(mAdapter.getItems().size() == 0) {
            switch (type) {
                case BOTH:
                    mNoItemsMessage.setText(R.string.notice_no_airports_or_airlines_found);
                    break;
                case AIRPORT:
                    mNoItemsMessage.setText(R.string.notice_no_airports_found);
                    break;
                case AIRLINE:
                    mNoItemsMessage.setText(R.string.notice_no_airlines_found);
                    break;
            }
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);

        } else {
            mNoItemsContainer.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showRecents(List<AirportAirlineItem> items, AirportAirlineSearchType type) {
        mSwipeRefresh.setRefreshing(false);

        if(items.size() == 0) {
            switch (type) {
                case BOTH:
                    mNoItemsMessage.setText(R.string.airport_airline_search_airports_airlines);
                    break;
                case AIRPORT:
                    mNoItemsMessage.setText(R.string.airport_airline_search_airports);
                    break;
                case AIRLINE:
                    mNoItemsMessage.setText(R.string.airport_airline_search_airlines);
                    break;
            }
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mAdapter.getItems().clear();
            mAdapter.getItems().add(new AirportAirlineHeader(getString(R.string.airport_airline_recents)));
            mAdapter.getItems().addAll(items);
            mAdapter.notifyDataSetChanged();
            mNoItemsContainer.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }


    public interface AirportAirlineSelectedListener {
        void onAirportAirlineSelected(AirportAirlineItem item);
        @Nullable String getInitialQueryOrShowRecents();
    }
}
