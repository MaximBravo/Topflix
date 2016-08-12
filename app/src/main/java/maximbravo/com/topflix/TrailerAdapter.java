package maximbravo.com.topflix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.youtube.player.YouTubePlayer;

import java.util.List;

/**
 * Created by wendy on 8/12/2016.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Context context, List<Trailer> trailer) {
        super(context, 0, trailer);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listViewItem = convertView;
        if (listViewItem == null) {
            listViewItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailers_list_item, parent, false);
        }

        Trailer currentTrailer = getItem(position);

        TrailerItemActivity trailerItemActivity = new TrailerItemActivity();

        YouTubePlayer youTubePlayer = trailerItemActivity.getPlayer();
        if(youTubePlayer != null) {
            youTubePlayer.cueVideo(currentTrailer.getLink());
        }


        return listViewItem;
    }
}
