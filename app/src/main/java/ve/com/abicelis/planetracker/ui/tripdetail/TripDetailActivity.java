package ve.com.abicelis.planetracker.ui.tripdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.common.FlightAdapter;
import ve.com.abicelis.planetracker.util.ImageUtil;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 18/9/2017.
 */

public class TripDetailActivity extends BaseActivity implements TripDetailMvpView, AppBarLayout.OnOffsetChangedListener{

    //DATA
    private static final float PERCENTAGE_TO_SHOW_BOTTOM_FAB = 0.6f;
    private static final float PERCENTAGE_TO_SHOW_SUBTITLE = 0.05f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private boolean mIsTheSubtitleVisible = true;


    @Inject
    TripDetailPresenter mPresenter;

    //UI
    @BindView(R.id.activity_trip_detail_appbar)
    AppBarLayout mAppBar;
    @BindView(R.id.activity_trip_detail_subtitle)
    TextView mSubtitle;
    @BindView(R.id.activity_trip_detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_trip_detail_container)
    CoordinatorLayout mContainer;
    @BindView(R.id.activity_trip_detail_image)
    ImageView mImage;

    @BindView(R.id.activity_trip_detail_no_items_container)
    RelativeLayout mNoItemsContainer;
    @BindView(R.id.activity_trip_detail_recycler)
    RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private FlightAdapter mFlightAdapter;

    @BindView(R.id.activity_trip_detail_fab_map)
    FloatingActionButton mFabMap;
    @BindView(R.id.activity_trip_detail_fab_map_bottom)
    FloatingActionButton mFabMapBottom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);

        initViews();


        long tripId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_TRIP_DETAIL_TRIP_ID, -1);
        if(tripId != -1) {
            mPresenter.getTrip(tripId);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            };
            showMessage(Message.ERROR_LOADING_TRIP, callback);
        }

    }

    private void initViews() {

        //Hook into appbar movement to hide/show views
        mAppBar.addOnOffsetChangedListener(this);

        mFabMap.setOnClickListener(view -> {
            handleGoToMap();
        });
        mFabMapBottom.setOnClickListener(view -> {
            handleGoToMap();
        });

        setUpToolbar();
        setUpRecyclerView();

    }


    private void setUpToolbar() {
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mFlightAdapter = new FlightAdapter(this);

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mFlightAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_trip_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_trip_detail_share:
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_trip_detail_edit:
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_trip_detail_delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage >= PERCENTAGE_TO_SHOW_BOTTOM_FAB)
            mFabMapBottom.show();
        else
            mFabMapBottom.hide();

        if (percentage <= PERCENTAGE_TO_SHOW_SUBTITLE) {
            if(!mIsTheSubtitleVisible) {
                startAlphaAnimation(mSubtitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheSubtitleVisible = true;
            }

        } else {
            if (mIsTheSubtitleVisible) {
                startAlphaAnimation(mSubtitle, ALPHA_ANIMATIONS_DURATION, View.GONE);
                mIsTheSubtitleVisible = false;
            }
        }

    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }


    private void handleGoToMap(){
        Toast.makeText(this, "GOING TO MAP", Toast.LENGTH_SHORT).show();
        //Intent goToMapIntent = new Intent();
    }


    /* TripDetailMvpView implementation */

    @Override
    public void showTrip(Trip trip, List<FlightViewModel> flights) {
        mToolbar.setTitle(trip.getName());
        mSubtitle.setText(trip.getInfo(this));

        if(trip.getImage() != null)
            mImage.setImageBitmap(ImageUtil.getBitmap(trip.getImage()));
        else
            mImage.setImageResource(R.drawable.error);

        mFlightAdapter.getItems().clear();
        mFlightAdapter.getItems().addAll(flights);
        mFlightAdapter.notifyDataSetChanged();

        mNoItemsContainer.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoFlights() {
        mNoItemsContainer.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);
    }


}
