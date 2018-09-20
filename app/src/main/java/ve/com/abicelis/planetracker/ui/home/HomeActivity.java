package ve.com.abicelis.planetracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.TripViewModel;
import ve.com.abicelis.planetracker.ui.about.AboutActivity;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.flight.FlightActivity;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 4/9/2017.
 */

public class HomeActivity extends BaseActivity implements HomeMvpView {

    @Inject
    HomePresenter mHomePresenter;

    @BindView(R.id.activity_home_container)
    CoordinatorLayout mContainer;

    @BindView(R.id.activity_home_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_home_search_view)
    MaterialSearchView mSearchView;

    @BindView(R.id.activity_home_no_items_container)
    RelativeLayout mNoItemsContainer;
    @BindView(R.id.activity_home_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.activity_home_recyclerview)
    RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private TripAdapter mTripAdapter;

    @BindView(R.id.activity_home_fab_add_trip)
    FloatingActionButton mAddTrip;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mHomePresenter.attachView(this);

        setUpToolbar();
        setUpRecyclerView();
        setupSearchView();
        mHomePresenter.refreshTripList(null);

        mAddTrip.setOnClickListener((view) -> {
            Intent createNewTripIntent = new Intent(this, FlightActivity.class);
            startActivity(createNewTripIntent);

//                Toast.makeText(HomeActivity.this, "Inserting fake trip", Toast.LENGTH_SHORT).show();
//                mHomePresenter.insertFakeTrip();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHomePresenter.refreshTripList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_home, menu);

        MenuItem item = menu.findItem(R.id.menu_home_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
//            case R.id.menu_home_settings:
//                Toast.makeText(this, "Under construction", Toast.LENGTH_SHORT).show();
//                break;

            case R.id.menu_home_theme:
                new SharedPreferenceHelper().toggleAppThemeType();
                recreate();
                break;

            case R.id.menu_home_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        //Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setLogo(R.drawable.ic_plane);
    }

    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTripAdapter = new TripAdapter(this);
        mTripAdapter.setTripDeletedListener(trip -> {
            mHomePresenter.deleteTrip(trip);
        });

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mTripAdapter);

        mSwipeRefresh.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         mHomePresenter.refreshTripList(null);
                                                     }
                                                 }
        );
    }

    private void setupSearchView() {
        mSearchView.setVoiceSearch(true);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mHomePresenter.refreshTripList(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.trim();
                if(newText.length() > 0)
                    mHomePresenter.refreshTripList(newText);
                if(newText.length() == 0)
                    mHomePresenter.refreshTripList(null);
                return true;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                mAddTrip.hide();
            }

            @Override
            public void onSearchViewClosed() {
                mAddTrip.show();
                mHomePresenter.refreshTripList(null);
            }
        });
    }


    /* HomeMvpView implementation */

    @Override
    public void showLoading() {
        mSwipeRefresh.setRefreshing(true);
    }


    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);
    }

    @Override
    public void showTrips(List<TripViewModel> trips) {
        mSwipeRefresh.setRefreshing(false);
        mTripAdapter.getItems().clear();
        mTripAdapter.getItems().addAll(trips);
        mTripAdapter.notifyDataSetChanged();

        if(mTripAdapter.getItems().size() == 0) {
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mNoItemsContainer.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }
}
