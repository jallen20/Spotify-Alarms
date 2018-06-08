package alarms.westga.edu.soundcloudalarms.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

import alarms.westga.edu.soundcloudalarms.MainActivity;
import alarms.westga.edu.soundcloudalarms.model.Alarm;

/**
 * Created by allen on 4/23/2018.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private String title;
    private int hour, minute;
    private Calendar calendar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Bundle bundle = getArguments();
        this.title = bundle.getString("title");
        this.hour = bundle.getInt("hour");
        this.minute = bundle.getInt("minute");
        this.calendar = Calendar.getInstance();


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        long timeInMillis = this.getTimeInMillis(year, month, day, hour, minute);
        String amPM = ((this.hour < 12) ? "AM" : "PM");
        String strHour = ((this.hour > 12) ? Integer.toString(this.hour - 12) : Integer.toString(this.hour));
        String time = strHour + ":" + Integer.toString(this.minute) + " " + amPM;
        Random random = new Random();
        int requestCode = random.nextInt(10000);
        Alarm alarm = new Alarm(this.title, time, timeInMillis, requestCode);

        MainActivity activity = (MainActivity) this.getActivity();
        activity.addItem(alarm);
        Toast.makeText(getContext(), "Alarm added.", Toast.LENGTH_LONG).show();
    }

    private long getTimeInMillis(int year, int month, int day, int hour, int minute) {
        this.calendar.set(year, month, day, hour, minute);
        return this.calendar.getTimeInMillis();
    }

}
