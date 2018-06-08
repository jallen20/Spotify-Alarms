package alarms.westga.edu.soundcloudalarms.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import alarms.westga.edu.soundcloudalarms.MainActivity;
import alarms.westga.edu.soundcloudalarms.model.Alarm;


/**
 * Created by allen on 4/26/2018.
 */

public class AlarmController extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Gson gson = new Gson();
        String json = intent.getStringExtra("current_alarm");
        Alarm current = gson.fromJson(json, Alarm.class);
        Log.v(getClass().getSimpleName(), "Alarm Trigger: " + current.getTitle());
        Log.v(getClass().getSimpleName(), "TOKEN: " + MainActivity.ACCESS_TOKEN);
        MusicController.playSong(current.getTitle());
    }


}
