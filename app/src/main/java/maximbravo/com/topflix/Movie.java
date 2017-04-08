package maximbravo.com.topflix;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wendy on 8/9/2016.
 */
public class Movie {
    //Image url for thumnail
    private String mImageUrl;

    //Title of Movie
    private String mTitle;

    //Description of Movie
    private String mDescription;

    //Rating of Movie
    private String mRating;

    //Release date of Movie
    private String mDate;

    private int mMovieId;

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }

    private ArrayList<String> reviews;
    //Detail Image url
    private String mDetailImageUrl;

    private boolean fav;
    public HashMap<String, String> getTrailers() {
        return mTrailers;
    }

    private HashMap<String, String> mTrailers;

    public Movie(String i, String t, String ds, String r, String d, String di, HashMap<String, String> trail, ArrayList<String> rev, int id, boolean f){
        mImageUrl = i;
        mTitle = t;
        mDescription = ds;
        mRating = r;
        mDate = d;
        mDetailImageUrl = di;
        mTrailers = trail;
        reviews = rev;
        mMovieId = id;
        fav = f;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getRating() {
        return mRating;
    }
    public String getDate() {
        return mDate;
    }
    public String getDetailImageUrl() { return mDetailImageUrl; }

    public int getmMovieId() {
        return mMovieId;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }
}
