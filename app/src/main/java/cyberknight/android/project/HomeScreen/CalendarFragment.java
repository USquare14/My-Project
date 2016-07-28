package cyberknight.android.project.HomeScreen;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.Toast;

import cyberknight.android.project.R;

/**
 * Created by umang on 1/7/16.
 */
public class CalendarFragment extends DialogFragment {

    private CalendarView calendarView;
    private Activity parentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar,container,false);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        calendarView = (CalendarView) v.findViewById(R.id.calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                ((RecordScreenUpdater)parentActivity).setDateTo(NewRecord.getSringFormatForDate(year,month+1,dayOfMonth));
                RecordScreenUpdater recordScreenUpdater = (RecordScreenUpdater) getTargetFragment();
                recordScreenUpdater.setDateTo(NewRecord.getSringFormatForDate(year,month+1,dayOfMonth));
                recordScreenUpdater.updateScreenRecords();
                getDialog().dismiss();
            }
        });

        return v;
    }
}
