package ve.com.abicelis.planetracker.ui.flight.dateselectfragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 22/9/2017.
 */

public class DateSelectFragment extends Fragment {

    //DATA
    DateSelectedListener mListener;

    //UI
    @BindView(R.id.fragment_date_select_container)
    RelativeLayout mContainer;
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
        mCalendar.setShowOtherDates(MaterialCalendarView.SHOW_DECORATED_DISABLED);
        mCalendar.addDecorator(new TodayDecorator(Color.RED, CalendarDay.today()));

        Calendar minCal = CalendarUtil.getNewInstanceZeroedCalendar();
        minCal.add(Calendar.MONTH, Constants.CONST_FRAGMENT_DATE_SELECT_MIN_DATE_MONTH_DIFFERENCE);
        Calendar maxCal = CalendarUtil.getNewInstanceZeroedCalendar();
        maxCal.add(Calendar.MONTH, Constants.CONST_FRAGMENT_DATE_SELECT_MAX_DATE_MONTH_DIFFERENCE);
        mCalendar.state().edit().setMinimumDate(minCal).setMaximumDate(maxCal).commit();

        Calendar initialDate = mListener.getInitialDateOrNone();
        if(initialDate != null)
            mCalendar.setDateSelected(CalendarDay.from(initialDate), true);
        else
            Toast.makeText(getActivity(), Message.NOTICE_ENTER_FLIGHT_DATE.getFriendlyName(getContext()), Toast.LENGTH_SHORT).show();


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


    public class TodayDecorator implements DayViewDecorator {

        private final int color;
        private final CalendarDay today;

        public TodayDecorator(int color, CalendarDay today) {
            this.color = color;
            this.today = today;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(today);
        }

        @Override
        public void decorate(DayViewFacade view) {
            //view.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.material_calendar_today_background));
            view.addSpan(new DotSpan(10, color));
        }
    }

}
