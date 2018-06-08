package alarms.westga.edu.soundcloudalarms.model;

import android.support.annotation.NonNull;

/**
 * Created by allen on 4/21/2018.
 */
public class Alarm implements Comparable<Alarm> {

    private String title, time;
    private long timeInMillis;
    private int requestCode;

    public Alarm(String title, String time, long timeInMillis, int requestCode) {
        this.title = title;
        this.time = time;
        this.timeInMillis = timeInMillis;
        this.requestCode = requestCode;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTime() {
        return this.time;
    }

    public long getTimeInMillis() {
        return this.timeInMillis;
    }

    public int getRequestCode() {
        return this.requestCode;
    }

    @Override
    public int compareTo(@NonNull Alarm alarm) {
        long longTime = this.timeInMillis - alarm.getTimeInMillis();
        int intTime = (int) longTime;
        return intTime;
    }
}
