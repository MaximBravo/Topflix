package maximbravo.com.topflix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.HashMap;

/**
 * Created by wendy on 8/12/2016.
 */
public class TrailerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    private String testVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailers);
        Intent intent = getIntent();
        HashMap<String, String> trailers = (HashMap<String, String>)intent.getSerializableExtra("map");
        for(String key : trailers.keySet()){
            testVideo = trailers.get(key);
            break;
        }


        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(ApiKey.YOUTUBE_API_KEY, this);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            //youTubePlayer.cueVideo("W0I3YgFLSy8"); // Plays https://www.youtube.com/watch?v=W0I3YgFLSy8
            youTubePlayer.cueVideo(testVideo);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = getString(R.string.player_error);
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
}