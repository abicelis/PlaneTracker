package ve.com.abicelis.planetracker.ui.flight.dateselectfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;

/**
 * Created by abicelis on 22/9/2017.
 */

public class DateSelectFragment extends Fragment {

    //DATA
    DateSelectedListener mListener;

    //UI
    @BindView(R.id.fragment_date_select_calendar)
    MaterialCalendarView mCalendar;
    @BindView(R.id.fragment_date_select_searching_container)
    FrameLayout mSearchingContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_select, container, false);
        ButterKnife.bind(this, view);

        mCalendar.setOnDateChangedListener((widget, date, selected) -> {
            mListener.onDateSelected(date.getCalendar());
        });
        mCalendar.setDateTextAppearance(R.style.CustomDayTextAppearance);

        Calendar initialDate = mListener.getInitialDateOrNone();
        if(initialDate != null)
            mCalendar.setDateSelected(CalendarDay.from(initialDate), true);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Make sure the container activity has implements
        // AirportAirlineSelectedListener. If not, throw an exception
        try {
            mListener = (DateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DateSelectedListener");
        }
    }

    public void showSearchingOverlay(){
        mSearchingContainer.setVisibility(View.VISIBLE);
    }
    public void hideSearchingOverlay(){

        mSearchingContainer.setVisibility(View.GONE);
    }


    public interface DateSelectedListener {
        void onDateSelected(Calendar calendar);
        @Nullable Calendar getInitialDateOrNone();
    }

}
