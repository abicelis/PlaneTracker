package ve.com.abicelis.planetracker.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;

/**
 * Created by abicelis on 17/9/2017.
 */

public class FlightHeaderViewHolder extends RecyclerView.ViewHolder {

    //UI
    @BindView(R.id.list_item_flight_header_title)
    TextView mHeaderTitle;

    @BindView(R.id.list_item_flight_header_layover)
    TextView mHeaderLayover;


    public FlightHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(String headerTitle, String layoverFormat, String headerLayover) {
        mHeaderTitle.setText(headerTitle);
        mHeaderLayover.setText(String.format(Locale.getDefault(), layoverFormat, headerLayover));
    }
}