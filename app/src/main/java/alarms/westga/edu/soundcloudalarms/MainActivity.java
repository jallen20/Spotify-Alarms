package alarms.westga.edu.soundcloudalarms;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import alarms.westga.edu.soundcloudalarms.adapter.AlarmAdapter;
import alarms.westga.edu.soundcloudalarms.model.Alarm;
import alarms.westga.edu.soundcloudalarms.view.TimePickerFragment;

public class MainActivity extends FragmentActivity {

    private List<Alarm> alarms;
    private boolean loggedIn;
    private RecyclerView rv;
    private AlarmAdapter adapter;
    private static Context context;
    private Player spotifyPlayer;

    private static final String     REDIRECT_URI    = "http://localhost:8888/callback/";
    public static final String      CLIENT_ID       = "88b9baf70f7245f9aa86e88a91c0716f";
    private static final String[]   SCOPES          = {"user-read-private", "streaming"};
    private static final int        REQUEST_CODE    = 1919;

    public static String ACCESS_TOKEN;
    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.alarms = new ArrayList<Alarm>();
        this.loggedIn = false;
        this.initRecyclerView();
        context = getApplicationContext();
        textView = findViewById(R.id.nowPlaying);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAlarm();
            }
        });

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(SCOPES);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    public static Context getConext() {
        return context;
    }

    private void initRecyclerView() {
        this.rv = (RecyclerView) findViewById(R.id.rv);
        this.adapter = new AlarmAdapter(this, this.alarms);
        this.rv.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rv.setLayoutManager(layoutManager);
        this.rv.setItemAnimator(new DefaultItemAnimator());
    }


    public void newAlarm() {
        final EditText input = new EditText(this);
        input.setHint("Title");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input);

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                DialogFragment timeFragment = new TimePickerFragment();
                Bundle bundle = new Bundle();
                String text = (String) input.getText().toString();
                if (!text.isEmpty()) {
                    bundle.putString("title", text);
                    timeFragment.setArguments(bundle);
                    timeFragment.show(getSupportFragmentManager(), "Time");
                } else {
                    Snackbar.make(findViewById(R.id.main), R.string.null_title, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addItem(Alarm alarm) {
        this.alarms.add(alarm);
        this.adapter.notifyItemInserted(this.alarms.size() - 1);
        this.adapter.notifyDataSetChanged();
    }

    public static void setName(String name) {
        textView.setText(name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                this.loggedIn = true;
                ACCESS_TOKEN = response.getAccessToken();
                Log.v("MAIN", response.getAccessToken());
                Config config = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(config, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        Log.v("INIT", "" + (spotifyPlayer == null));
                        MainActivity.this.spotifyPlayer = spotifyPlayer;
                        MainActivity.this.spotifyPlayer.login(ACCESS_TOKEN);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.v("MAINACT", "ERROR INIT PLAYER");

                    }
                });

            }
        }
    }

}
