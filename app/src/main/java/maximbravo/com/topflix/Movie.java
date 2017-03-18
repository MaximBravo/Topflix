package maximbravo.com.topflix;

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

    //Detail Image url
    private String mDetailImageUrl;

    public HashMap<String, String> getTrailers() {
        return mTrailers;
    }

    private HashMap<String, String> mTrailers;

    public Movie(String i, String t, String ds, String r, String d, String di, HashMap<String, String> trail){
        mImageUrl = i;
        mTitle = t;
        mDescription = ds;
        mRating = r;
        mDate = d;
        mDetailImageUrl = di;
        mTrailers = trail;
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

}
