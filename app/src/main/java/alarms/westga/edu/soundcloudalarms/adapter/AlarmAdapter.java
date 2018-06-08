package alarms.westga.edu.soundcloudalarms.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import alarms.westga.edu.soundcloudalarms.R;
import alarms.westga.edu.soundcloudalarms.controller.AlarmController;
import alarms.westga.edu.soundcloudalarms.model.Alarm;
import okhttp3.internal.platform.Platform;

/**
 * Created by allen on 4/21/2018.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Alarm> alarms;
    private Context context;

    public AlarmAdapter(Context context, List<Alarm> alarms) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.alarms = alarms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.layoutInflater.inflate(R.layout.alarm_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Alarm alarm = this.alarms.get(position);
        holder.setText(alarm, position);
    }

    @Override
    public int getItemCount() {
        return this.alarms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private TextView title, time, nowPlaying, day;
        private int position;
        private Alarm current;
        private Switch onSwitch;
        private AlarmManager manager;


        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.time = itemView.findViewById(R.id.time);
            this.nowPlaying = itemView.findViewById(R.id.nowPlaying);
            this.onSwitch = (Switch) itemView.findViewById(R.id.onSwitch);
            this.onSwitch.setOnCheckedChangeListener(this);
        }

        public void setText(Alarm alarm, int position) {
            this.title.setText(alarm.getTitle());
            this.time.setText(alarm.getTime());
            this.current = alarm;
            this.position = position;
        }

        private void setAlarm(long timeInMillis) {
            this.manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Gson gson = new Gson();
            String json = gson.toJson(this.current, Alarm.class);
            Intent intent = new Intent(context, AlarmController.class);
            intent.putExtra("current_alarm", json);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, this.current.getRequestCode() , intent, PendingIntent.FLAG_ONE_SHOT);
            this.manager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis - 10000, pendingIntent);
        }

        private void cancel() {
            Intent intent = new Intent(context, AlarmController.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, this.current.getRequestCode(), intent, PendingIntent.FLAG_ONE_SHOT);
            this.manager.cancel(pendingIntent);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (compoundButton.isChecked()) {
                Toast.makeText(context, "ON ", Toast.LENGTH_SHORT).show();
                this.setAlarm(this.current.getTimeInMillis());
            }
            if (!compoundButton.isChecked()) {
                this.cancel();
                Toast.makeText(context, "OFF ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
