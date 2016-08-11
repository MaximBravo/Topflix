package maximbravo.com.topflix;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by wendy on 8/9/2016.
 */
public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String mUrl;

    public static List<Movie> moviesList;
    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
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
        if (mUrl == null) {
            return null;
        }

        MovieDataExtracter movieDataExtracter = new MovieDataExtracter(getContext());
        List<Movie> movies = movieDataExtracter.fetchMovieData(mUrl);
        moviesList = movies;
        return movies;
    }
}