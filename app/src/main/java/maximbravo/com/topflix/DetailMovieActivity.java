package maximbravo.com.topflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailMovieActivity extends AppCompatActivity {
    private HashMap<String, String> trailers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        int position = intent.getIntExtra(MoviesActivity.EXTRA_MESSAGE, -1);
        trailers = new HashMap<>();
        loadContent(position);

    }

    public void loadContent(int p){
        Movie currentMovie = MovieLoader.moviesList.get(p);

        ImageView detailImageView = (ImageView) findViewById(R.id.detail_image);
        Picasso.with(getApplicationContext()).load(currentMovie.getDetailImageUrl()).into(detailImageView);
//        imageView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        TextView detailTitleView = (TextView) findViewById(R.id.detail_title);
        String title = currentMovie.getTitle();
        detailTitleView.setText(title);
        //       titleView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView detailDescriptionView = (TextView) findViewById(R.id.detail_description);
        String description = currentMovie.getDescription();
        detailDescriptionView.setText(description);
        //       descriptionView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView reviewsView = (TextView) findViewById(R.id.reviews);
        ArrayList<String> reviews = currentMovie.getReviews();
        String extractedReviews = extractReviews(reviews);
        reviewsView.setText(extractedReviews);


        TextView detailRatingView = (TextView) findViewById(R.id.detail_rating);
        String rating = currentMovie.getRating();
        detailRatingView.setText(rating);
//        ratingView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView detailDateView = (TextView) findViewById(R.id.detail_date);
        String date = currentMovie.getDate();
        detailDateView.setText(date);

        trailers = currentMovie.getTrailers();
    }

    private String extractReviews(ArrayList<String> reviews) {
        String result = "";
        for(int i = 0; i < reviews.size(); i++){
            if(i != 0){
                result += "\n\n";
            }
            String currentReview = reviews.get(i);
            int divider = currentReview.indexOf("----");
            String author = currentReview.substring(0, divider);
            String content = currentReview.substring(divider+4, currentReview.length());
            result += author +"\n"+content;
        }
        return result;
    }

    public void launchTrailers(View view){
        Intent intent = new Intent(this, TrailerActivity.class);
        intent.putExtra("map", trailers);
        startActivity(intent);
    }
}
