package alarms.westga.edu.soundcloudalarms.view;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by allen on 4/23/2018.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String title;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.title = bundle.getString("title");
            Log.v(getContext().getPackageName(), bundle.getString("title"));
        } else {
            Log.v(getContext().getPackageName(), "NULL");
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                false);

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);
        bundle.putString("title", this.title);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

    }
}
