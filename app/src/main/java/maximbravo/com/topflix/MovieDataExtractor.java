package maximbravo.com.topflix;

import android.content.Context;
import android.preference.PreferenceManager;
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
import java.util.HashMap;
import java.util.List;

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

        String movieJSON = null;
        String sortingOrder = "top_rated";
        if (PreferenceManager.getDefaultSharedPreferences(thisContext).getBoolean("popular", true)) {
            sortingOrder = "popular";
        }
        try {
            movieJSON = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/"+ sortingOrder+ "?api_key=" + MoviesActivity.apiKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            JSONArray results = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentNews = results.getJSONObject(i);

                String baseURL = "http://image.tmdb.org/t/p/";
                String size = "w500";
                String detailSize = "w780";
                String end = currentNews.getString("poster_path");
                String backdropEnd = currentNews.getString("backdrop_path");
                String url = baseURL + size + end;

                String detailUrl = baseURL + detailSize + end;
                //String title = currentNews.getString("original_title");
                String title = currentNews.getString("original_title");

                String description = "    " + currentNews.getString("overview");
                String rating = "" + currentNews.getDouble("vote_average");
                String date = getPrettyDate(currentNews.getString("release_date"));
                //String date = getCurrentDate();
                String id = currentNews.getString("id");
                String trailerJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + MoviesActivity.apiKey));
                HashMap<String, String> trailers = extractTrailers(trailerJson);
                //String trailer = currentNews.getString("overview");
                String reviewJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + MoviesActivity.apiKey));
                ArrayList<String> reviews = extractReviews(reviewJson);
                Movie movie = new Movie(url, title, description, rating, date, detailUrl, trailers, reviews);

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
