package maximbravo.com.topflix;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, List<Movie> movie) {
        super(context, 0, movie);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View gridViewItem = convertView;
        if (gridViewItem == null) {
            gridViewItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.movies_list_item, parent, false);
        }

        Movie currentMovie = getItem(position);
//
//        LinearLayout item = (LinearLayout) parent.findViewById(R.id.grid_item);
//        item.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ImageView imageView = (ImageView) gridViewItem.findViewById(R.id.thumbnail_image);
        if (currentMovie != null) {
            Picasso.with(getContext()).load(currentMovie.getImageUrl()).into(imageView);
        }
//        imageView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


//        TextView titleView = (TextView) gridViewItem.findViewById(R.id.title_text);
//        String title = currentMovie.getTitle();
//        titleView.setText(title);
 //       titleView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

//        TextView descriptionView = (TextView) gridViewItem.findViewById(R.id.description_text);
//        String description = currentMovie.getDescription();
//        descriptionView.setText(description);
 //       descriptionView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

//        TextView ratingView = (TextView) gridViewItem.findViewById(R.id.rating_text);
//        String rating = currentMovie.getRating();
//        ratingView.setText(rating);
//        ratingView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

//        TextView dateView = (TextView) gridViewItem.findViewById(R.id.date_text);
//        String date = currentMovie.getDate();
//        dateView.setText(date);
//        dateView.setLayoutParams(new GridView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        return gridViewItem;
    }
}
