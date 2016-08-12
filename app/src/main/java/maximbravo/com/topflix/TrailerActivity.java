package maximbravo.com.topflix;

/**
 * Created by wendy on 8/12/2016.
 */
public class TrailerActivity {//extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
   /* public static ArrayList<Trailer> trailers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailers);

        Intent intent = getIntent();
        int position = intent.getIntExtra(MoviesActivity.EXTRA_MESSAGE, -1);
        Integer[] thePosition = {position};
        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute(thePosition);


    }
    public static YouTubePlayer theOnlyYoutubePlayer;
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        theOnlyYoutubePlayer = youTubePlayer;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public void updateTrailers(){
        ListView trailersListView = (ListView) findViewById(R.id.trailers);
        TrailerAdapter mAdapter = new TrailerAdapter(this, trailers);
        trailersListView.setAdapter(mAdapter);
    }

    private class BookAsyncTask extends AsyncTask<Integer, Void, Void> {


        @Override
        protected Void doInBackground(Integer... position) {
            trailers = MovieDataExtracter.getTrailers(position[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateTrailers();
        }
    }
    */
}




//public class TrailerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
//
//    private static final int RECOVERY_REQUEST = 1;
//    private YouTubePlayerView youTubeView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_movie_trailers);
//
//        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
//        youTubeView.initialize(ApiKey.YOUTUBE_API_KEY, this);
//    }
//
//
//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//        if (!b) {
//            youTubePlayer.cueVideo("W0I3YgFLSy8"); // Plays https://www.youtube.com/watch?v=W0I3YgFLSy8
//        }
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//        if (youTubeInitializationResult.isUserRecoverableError()) {
//            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
//        } else {
//            String error = getString(R.string.player_error);
//            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//        }
//    }
//}