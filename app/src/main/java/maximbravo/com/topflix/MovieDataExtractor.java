package maximbravo.com.topflix;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.R.attr.id;
import static android.R.attr.key;
import static android.R.attr.name;

/**
 * Created by wendy on 8/9/2016.
 */
public class MovieDataExtractor {

    private static final String LOG_TAG = MovieDataExtractor.class.getSimpleName();

    private Context thisContext;
    public MovieDataExtractor(Context context) {
        thisContext = context;
    }

//    public List<Movie> fetchMovieData() {
//        return extractFeatureFromJson();
//    }

    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public List<Movie> extractFeatureFromJson() {

        String movieJSON = "";
        String sortingOrder = "top_rated";
        if (PreferenceManager.getDefaultSharedPreferences(thisContext).getBoolean("popular", true)) {
            sortingOrder = "popular";
        }
        List<Movie> movies = new ArrayList<>();
        if(PreferenceManager.getDefaultSharedPreferences(thisContext).getBoolean("showFavorites", false)) {
            // load favorites
            movies = getFavoriteMovies();
        } else {
            try {
                movieJSON = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + sortingOrder + "?api_key=" + MoviesActivity.apiKey));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(movieJSON)) {
                return null;
            }
        }



        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            JSONArray results = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentMovie = results.getJSONObject(i);

                String baseURL = "http://image.tmdb.org/t/p/";
                String size = "w500";
                String detailSize = "w780";
                String end = currentMovie.getString("poster_path");
                String backdropEnd = currentMovie.getString("backdrop_path");
                String url = baseURL + size + end;

                String detailUrl = baseURL + detailSize + end;
                //String title = currentMovie.getString("original_title");
                String title = currentMovie.getString("original_title");

                String description = "    " + currentMovie.getString("overview");
                String rating = "" + currentMovie.getDouble("vote_average");
                String date = getPrettyDate(currentMovie.getString("release_date"));
                //String date = getCurrentDate();
                String id = currentMovie.getString("id");
                String trailerJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + MoviesActivity.apiKey));
                HashMap<String, String> trailers = extractTrailers(trailerJson);
                //String trailer = currentMovie.getString("overview");
                String reviewJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + MoviesActivity.apiKey));
                ArrayList<String> reviews = extractReviews(reviewJson);

                Movie movie = new Movie(url, title, description, rating, date, detailUrl, trailers, reviews, Integer.parseInt(id), false);
                if(MoviesActivity.favorites.contains(id)){
                    movie.setFav(true);
                }
                movies.add(movie);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem getting trailer json", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private List<Movie> getFavoriteMovies() {
        List<Movie> result = new ArrayList<>();
        for(int i = 0; i < MoviesActivity.favorites.size(); i++) {
            int idMovie = MoviesActivity.favorites.get(i);
            String currentMovieJson = "";
            try {
                currentMovieJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + idMovie + "?api_key=" + MoviesActivity.apiKey));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!currentMovieJson.equals("")){

                JSONObject currentMovie = null;
                try {
                    currentMovie = new JSONObject(currentMovieJson);
                    String baseURL = "http://image.tmdb.org/t/p/";
                    String size = "w500";
                    String detailSize = "w780";
                    String end = currentMovie.getString("poster_path");
                    String backdropEnd = currentMovie.getString("backdrop_path");
                    String url = baseURL + size + end;

                    String detailUrl = baseURL + detailSize + end;
                    //String title = currentMovie.getString("original_title");
                    String title = currentMovie.getString("original_title");

                    String description = "    " + currentMovie.getString("overview");
                    String rating = "" + currentMovie.getDouble("vote_average");
                    String date = getPrettyDate(currentMovie.getString("release_date"));
                    //String date = getCurrentDate();
                    String id = currentMovie.getString("id");
                    String trailerJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + MoviesActivity.apiKey));
                    HashMap<String, String> trailers = extractTrailers(trailerJson);
                    //String trailer = currentMovie.getString("overview");
                    String reviewJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + MoviesActivity.apiKey));
                    ArrayList<String> reviews = extractReviews(reviewJson);
                    Movie movie = new Movie(url, title, description, rating, date, detailUrl, trailers, reviews, Integer.parseInt(id), true);

                    result.add(movie);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
        return result;
    }

    private HashMap<String, String> extractTrailers(String trailerJson) {
        HashMap<String, String> trailerList = new HashMap<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(trailerJson);
            JSONArray results = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentMovie = results.getJSONObject(i);
                String name = currentMovie.getString("name");
                String key = currentMovie.getString("key");
                trailerList.put(name, key);
            }
        } catch (JSONException e){
            Log.e("MoveDataExtracter", "Problem extracting trailers");
        }
        return trailerList;
    }

    private ArrayList<String> extractReviews(String reviewJson) {
        ArrayList<String> reviewList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(reviewJson);
            JSONArray results = baseJsonResponse.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentReview = results.getJSONObject(i);
                String author = currentReview.getString("author");
                String content = currentReview.getString("content");
                reviewList.add(i, author+"----"+content);
            }
        } catch (JSONException e){
            Log.e("MoveDataExtracter", "Problem extracting trailers");
        }
        return reviewList;
    }

    public String getPrettyDate(String uD){
        if(uD.length() == 0){
            return "Pretty date could not be retrieved";
        }
        String uglyDate = uD;
        String[] datePieced = uglyDate.split("-");
        String month;
        String year;
        String day;
        if(datePieced[1].substring(0, 1).equals("0")){
            day = datePieced[1].substring(1,2);
        } else {
            day = datePieced[1];
        }
        if(datePieced[2].substring(0, 1).equals("0")){
            month = datePieced[2].substring(1,2);
        } else {
            month = datePieced[2];
        }
        year = datePieced[0];
        String prettyDate = day + "/" + month + "/" + year;
        return prettyDate;
    }

    public String getCurrentDate() {
        long dates = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        String dateString = sdf.format(dates);
        String date = dateString;

        String month = date.substring(7, 9);
        String day = date.substring(4, 6);
        String year = date.substring(11, 15);

        if(day.substring(0, 1).equals("0")){
            day = day.substring(1,2);
        }
        if(month.substring(0, 1).equals("0")){
            month = month.substring(1,2);
        }
        String retDate = day + "/" + month + "/" + year;
        return retDate;
    }

}
