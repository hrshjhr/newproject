package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by root on 1/9/15.
 */
public class AllRouteActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_route_activity);

        String[] favoriteTVShows = {"Pushing Daisies", "Better Off Ted",
                "Twin Peaks", "Freaks and Geeks", "Orphan Black", "Walking Dead",
                "Breaking Bad", "The 400", "Alphas", "Life on Mars"};

        ListAdapter theAdapter = new ArrayAdapter<String>(this, R.layout.route_card_layout,
                R.id.textViewRoute, favoriteTVShows);

        // We point the ListAdapter to our custom adapter
        // ListAdapter theAdapter = new MyAdapter(this, favoriteTVShows);

        // Get the ListView so we can work with it
        ListView theListView = (ListView) findViewById(R.id.list_view_all_routes);

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent trackIntent = new Intent(getApplicationContext() , TrackLocalBusActivity.class);
                startActivity(trackIntent);
            }
        });

        // Connect the ListView with the Adapter that acts as a bridge between it and the array
        theListView.setAdapter(theAdapter);


    }
}
