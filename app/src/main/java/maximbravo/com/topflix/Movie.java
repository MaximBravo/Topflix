package maximbravo.com.topflix;

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

    public Movie(String i, String t, String ds, String r, String d, String di){
        mImageUrl = i;
        mTitle = t;
        mDescription = ds;
        mRating = r;
        mDate = d;
        mDetailImageUrl = di;
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
