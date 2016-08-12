package maximbravo.com.topflix;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

/**
 * Created by wendy on 8/12/2016.
 */
public class TrailerItemActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private YouTubePlayerView youTubeView;
    private YouTubePlayer youTubePlayer1;
    private ArrayList<String> trailers;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trailers_list_item);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(ApiKey.YOUTUBE_API_KEY, this);
        Intent intent = getIntent();
        int position = intent.getIntExtra(MoviesActivity.EXTRA_MESSAGE, -1);
        Integer[] thePosition = {position};
        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute(thePosition);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer1 = youTubePlayer;

            youTubePlayer.cueVideo("fhWaJi1Hsfo");//MovieDataExtracter.getTrailers(0));

    }

    public YouTubePlayer getPlayer(){
        return youTubePlayer1;
    }

    public YouTubePlayerView getYouTubeView(){
        return youTubeView;
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
    private class BookAsyncTask extends AsyncTask<Integer, Void, Void> {


        @Override
        protected Void doInBackground(Integer... position) {
            trailers = MovieDataExtracter.getTrailers(position[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}
