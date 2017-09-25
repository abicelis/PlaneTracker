package ve.com.abicelis.planetracker.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.changeimage.ChangeImageActivity;
import ve.com.abicelis.planetracker.ui.tripdetail.TripDetailActivity;
import ve.com.abicelis.planetracker.util.ImageUtil;

/**
 * Created by abicelis on 4/9/2017.
 */

public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //DATA
    private TripAdapter mAdapter;
    private Activity mActivity;
    private Trip mCurrent;
    private int mPosition;

    //UI
    @BindView(R.id.list_item_trip_container)
    RelativeLayout mContainer;
    @BindView(R.id.list_item_trip_image)
    ImageView mImage;
    @BindView(R.id.list_item_trip_no_image)
    TextView mNoImage;
    @BindView(R.id.list_item_trip_name)
    TextView mName;
    @BindView(R.id.list_item_trip_info)
    TextView mInfo;
    @BindView(R.id.list_item_trip_menu)
    ImageView mMenu;


    public TripViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(TripAdapter adapter, Activity activity, Trip current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        if(mCurrent.getImage() != null && mCurrent.getImage().length != 0) {
            mImage.setImageBitmap(ImageUtil.getBitmap(mCurrent.getImage()));
            mNoImage.setVisibility(View.GONE);
        } else {
            mImage.setImageResource(R.drawable.error);
            mNoImage.setVisibility(View.VISIBLE);
        }

        mName.setText(mCurrent.getName());
        mInfo.setText(mCurrent.getInfo(activity));
    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
        mMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.list_item_trip_container:
                Intent tripDetailIntent = new Intent(mActivity, TripDetailActivity.class);
                tripDetailIntent.putExtra(Constants.EXTRA_ACTIVITY_TRIP_DETAIL_TRIP_ID, mCurrent.getId());
                mActivity.startActivity(tripDetailIntent);
                break;

            case R.id.list_item_trip_menu:
                PopupMenu popupMenu = new PopupMenu(mActivity, mMenu, Gravity.END);
                popupMenu.getMenuInflater().inflate(R.menu.menu_list_item_trip, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_list_item_delete:
                                Toast.makeText(mActivity, "delete", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_list_item_change_image:
                                Intent intent = new Intent(mActivity, ChangeImageActivity.class);
                                intent.putExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_NAME, mCurrent.getName());
                                intent.putExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_ID, mCurrent.getId());
                                mActivity.startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;


        }
    }
}