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
import java.util.List;

/**
 * Created by wendy on 8/9/2016.
 */
public class MovieDataExtracter {

    private static final String LOG_TAG = MovieDataExtracter.class.getSimpleName();

    private static Context thisContext;
    public MovieDataExtracter(Context context) {
        thisContext = context;
    }

    public static List<Movie> fetchMovieData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Movie> movies = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsEvent}s
        return movies;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
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

    private static String readFromStream(InputStream inputStream) throws IOException {
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

    private static ArrayList<String> ids = new ArrayList<String>();
    private static List<Movie> extractFeatureFromJson(String mJ) {

        String movieJSON = null;
        boolean setting;
        if (PreferenceManager.getDefaultSharedPreferences(thisContext).getBoolean("popular", true)) {
            setting = true;
        } else {
            setting = false;
        }
        if(setting) {
            try {
                movieJSON = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/popular?api_key=" + MoviesActivity.apiKey));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                movieJSON = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/top_rated?api_key=" + MoviesActivity.apiKey));
            } catch (IOException e) {
                e.printStackTrace();

            }
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
                String title = currentNews.getString("original_title");
                String description = "    " + currentNews.getString("overview");
                String rating = "" + currentNews.getDouble("vote_average");
                String date = getPrettyDate(currentNews.getString("release_date"));
                //String date = getCurrentDate();
                String id = currentNews.getString("id");
                ids.add(id);
                //String trailer

                Movie movie = new Movie(url, title, description, rating, date, detailUrl);

                movies.add(movie);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return movies;
    }

    public static ArrayList<String> getTrailers(int position){
        String trailerJson = null;

        try {
            trailerJson = makeHttpRequest(createUrl("http://api.themoviedb.org/3/movie/" + ids.get(position) + "/videos?api_key=" + MoviesActivity.apiKey));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(trailerJson)) {
            return null;
        }

        List<String> trailers = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(trailerJson);

            JSONArray results = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentTrailer = results.getJSONObject(i);

                String key = currentTrailer.getString("key");
                //String trailer


                trailers.add(key);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the trailer JSON results", e);
        }
        return (ArrayList<String>) trailers;
    }

    public static String getPrettyDate(String uD){
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

    public static String getCurrentDate() {
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
