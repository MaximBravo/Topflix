package maximbravo.com.topflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by wendy on 8/10/2016.
 */
public class DetailMovieActivity extends AppCompatActivity {
    private int position;
    private HashMap<String, String> trailers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        position = intent.getIntExtra(MoviesActivity.EXTRA_MESSAGE, -1);
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

        TextView detailRatingView = (TextView) findViewById(R.id.detail_rating);
        String rating = currentMovie.getRating();
        detailRatingView.setText(rating);
//        ratingView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView detailDateView = (TextView) findViewById(R.id.detail_date);
        String date = currentMovie.getDate();
        detailDateView.setText(date);

        trailers = currentMovie.getTrailers();
    }

    public void launchTrailers(View view){
        Intent intent = new Intent(this, TrailerActivity.class);
        intent.putExtra("map", trailers);
        startActivity(intent);
    }
}
