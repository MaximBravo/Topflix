package maximbravo.com.topflix;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import maximbravo.com.topflix.data.FavoritesContract;

import static android.R.attr.defaultValue;
import static android.support.v7.widget.AppCompatDrawableManager.get;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    public static String apiKey = ApiKey.apiKey;
//    private static String TMDB_REQUEST_URL =
//            "http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey;

    private static final int MOVIE_LOADER_ID = 1;

    private MovieAdapter mAdapter;

    private TextView mEmptyStateTextView;
    public static ArrayList<Integer> favorites = new ArrayList<>();
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

//        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
//        String favoritesString = sharedPref.getString("favorites", "null");
//        if(favoritesString.equals("null")){
//            favorites = new ArrayList<>();
//            updateFavorites();
//        } else {
//            favorites = decodeFavorites(favoritesString);
//        }
        GridView moviesGridView = (GridView) findViewById(R.id.grid_view);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        moviesGridView.setEmptyView(mEmptyStateTextView);

        mAdapter = new MovieAdapter(this, new ArrayList<Movie>());

        moviesGridView.setAdapter(mAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //TODO: add detail activity intent
                Intent intent = new Intent(getApplicationContext(), DetailMovieActivity.class);
                //Intent intent = new Intent(getApplicationContext(), TrailerActivity.class);
                intent.putExtra(EXTRA_MESSAGE, position);
                startActivity(intent);
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        favorites.clear();

        Cursor cursor = getContentResolver().query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID},
                null,
                null,
                null);
        if(cursor != null) {
            while (cursor.moveToNext()) {
                int data = cursor.getInt(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID));
                favorites.add(data);
            }
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
        }


    }

    public static void addToDb(Activity activity, int movieId){
        ContentValues vals = new ContentValues();
        vals.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, movieId);
        activity.getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, vals);
        addOrRemove = -1;
        favId = -1;
    }
    public static void removeFromDb(Activity activity, int movieId) {
        String selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {""+movieId};
        activity.getContentResolver().delete(FavoritesContract.FavoritesEntry.CONTENT_URI, selection, selectionArgs);
        addOrRemove = -1;
        favId = -1;
    }
    public static int addOrRemove = -2;
    public static int favId;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if(addOrRemove == 0){
//            addToDb(favId);
//        } else if(addOrRemove == 1){
//            removeFromDb(favId);
//        }
        //updateFavorites();
    }

    public static void updateFavs(Activity activity){
        if(addOrRemove == 0){
            addToDb(activity, favId);
        } else if(addOrRemove == 1){
            removeFromDb(activity, favId);
        }
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }

    public void updateFavorites(){
        SharedPreferences sharedPref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("favorites", encodeFavorites());
        editor.commit();
    }
    public String encodeFavorites(){
        String result = "";
        for(int i = 0; i < favorites.size(); i++){
            result+= favorites.get(i) + ",";
        }
        return result;
    }
    public ArrayList<Integer> decodeFavorites(String favString){
        ArrayList<Integer> result = new ArrayList<>();
        String[] favs = favString.split(",");
        for(int i = 0; i < favs.length; i++){
            String current = favs[i];
            if(current.length()!= 0){
                result.add(i, Integer.parseInt(current));
            }
        }
        return result;
    }
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
//        updateFavorites();
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_movies);

        mAdapter.clear();

        if (movies != null && !movies.isEmpty()) {
            mAdapter.addAll(movies);
        }
        GridView movieGridView = (GridView) findViewById(R.id.grid_view);

        movieGridView.setEmptyView(findViewById(R.id.empty_view));
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}


