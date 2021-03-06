package maximbravo.com.topflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    private int favoriteId = 0;
    private int currentMoviePosition = -1;
    public void loadContent(int p){
        Movie currentMovie = MovieLoader.moviesList.get(p);
        currentMoviePosition = p;
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

        favoriteId = currentMovie.getmMovieId();

        isFav = currentMovie.isFav();

        updateFavText();
        trailers = currentMovie.getTrailers();
    }

    private void updateFavText() {
        Button favButton = (Button) findViewById(R.id.fav_button);
        if(isFav){
            favButton.setText("Remove");
        } else {
            favButton.setText("Add");
        }
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
    private boolean isFav;
    public void addToFavorites(View v){
        if(isFav){
            isFav = false;
        } else {
            isFav = true;
        }
        updateFavText();
        //MoviesActivity.updateFavorites();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isFav){
            if(!MoviesActivity.favorites.contains(favoriteId)) {
                MoviesActivity.addOrRemove = 0;
                MoviesActivity.favId = favoriteId;
            }
        }else{
            for(int i = 0; i < MoviesActivity.favorites.size(); i++) {
                if(MoviesActivity.favorites.get(i).equals(favoriteId)) {
                    MoviesActivity.addOrRemove = 1;
                    MoviesActivity.favId = favoriteId;
                }
            }
        }


        MoviesActivity.updateFavs(this);
//        Movie currentMovie = MovieLoader.moviesList.get(currentMoviePosition);
//        currentMovie.setFav(!isFav);
//        MovieLoader.moviesList.set(currentMoviePosition, currentMovie);
    }
}
