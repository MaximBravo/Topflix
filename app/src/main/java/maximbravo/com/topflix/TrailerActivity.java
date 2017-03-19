package maximbravo.com.topflix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.id.list;

/**
 * Created by wendy on 8/12/2016.
 */
public class TrailerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    public static YouTubePlayer youPlayer;
    private String video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_trailers);
        Intent intent = getIntent();
        final HashMap<String, String> trailers = (HashMap<String, String>)intent.getSerializableExtra("map");
//        for(String key : trailers.keySet()){
//            video = trailers.get(key);
//            break;
//        }
        youPlayer = null;
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(ApiKey.YOUTUBE_API_KEY, this);
        final ListView listview = (ListView) findViewById(R.id.trailers);

        final ArrayList<String> list = new ArrayList<>();
        for(String key : trailers.keySet()){
            list.add(key);
        }
        final SimpleArrayAdapter adapter = new SimpleArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String key = (String) parent.getItemAtPosition(position);
                video = trailers.get(key);
                initialize();
            }
        });
    }

    private void initialize(){
        if(youPlayer != null) {
            youPlayer.loadVideo(video);
        } else  {
            youTubeView.initialize(ApiKey.YOUTUBE_API_KEY, this);
        }
    }
    private class SimpleArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public SimpleArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        //if (!b) {
            youPlayer = youTubePlayer;
            //youTubePlayer.cueVideo("W0I3YgFLSy8"); // Plays https://www.youtube.com/watch?v=W0I3YgFLSy8

        //}
    }



    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = getString(R.string.player_error);
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
}