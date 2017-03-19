package maximbravo.com.topflix;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    public static List<Movie> moviesList;
    public MovieLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Movie> loadInBackground() {
        MovieDataExtractor movieDataExtractor = new MovieDataExtractor(getContext());
        List<Movie> movies = movieDataExtractor.extractFeatureFromJson();
        moviesList = movies;
        return moviesList;
    }
}