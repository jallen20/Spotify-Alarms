package alarms.westga.edu.soundcloudalarms.controller;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import alarms.westga.edu.soundcloudalarms.MainActivity;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by allen on 4/30/2018.
 */

public class MusicController {

    private static final String SEARCH_URL = "https://api.spotify.com/v1/search";

    public static void playSong(String alarmTitle) {
        SongRequester requester = new SongRequester();
        requester.execute(alarmTitle);
    }

    static class SongRequester extends AsyncTask<String, Integer, JSONObject> {

        private Config config;

        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(SEARCH_URL).newBuilder();
            urlBuilder.addQueryParameter("q", strings[0]);
            urlBuilder.addQueryParameter("type", "track");
            urlBuilder.addQueryParameter("market", "US");
            urlBuilder.addQueryParameter("limit", "20");
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + MainActivity.ACCESS_TOKEN)
                    .url(url)
                    .build();
            OkHttpClient client = new OkHttpClient();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                Log.v("BACK", "FAILED");
            }

            JSONObject json = null;
            try {
                json = new JSONObject(response.body().string());
            } catch (IOException e) {
                Log.v("TASK", e.getMessage());
            } catch (JSONException e) {
                Log.v("TASK", e.getMessage());
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.v("POST", jsonObject.toString());
            try {
                JSONObject json = jsonObject.getJSONObject("tracks");
                JSONArray jsonArray = json.getJSONArray("items");
                if (jsonArray.length() > 1) {
                    int max = jsonArray.length();
                    int min = 0;
                    Random random = new Random();
                    int randomIndex = random.nextInt(max - min) + min;
                    JSONObject randomObject = jsonArray.getJSONObject(randomIndex);
                    JSONObject album = randomObject.getJSONObject("album");
                    String name = album.getString("name");
                    MainActivity.setName(name);
                    Log.v("NAME", name + randomIndex);
                    final String uri = album.getString("uri");
                    this.config = new Config(MainActivity.getConext(), MainActivity.ACCESS_TOKEN, MainActivity.CLIENT_ID);
                    Spotify.getPlayer(config, this, new SpotifyPlayer.InitializationObserver() {
                        @Override
                        public void onInitialized(SpotifyPlayer spotifyPlayer) {
                            spotifyPlayer.login(MainActivity.ACCESS_TOKEN);
                            spotifyPlayer.playUri(null, uri, 0, 0);

                        }
                        @Override
                        public void onError(Throwable throwable) {

                        }
                    });
                } else {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    MediaPlayer mp = MediaPlayer.create(MainActivity.getConext(), notification);
                    for (int i = 0; i < 61; i++) {
                        mp.start();
                        Thread.sleep(1000);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
