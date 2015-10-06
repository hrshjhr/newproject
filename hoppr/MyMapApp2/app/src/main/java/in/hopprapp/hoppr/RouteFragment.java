package in.hopprapp.hoppr;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by root on 1/9/15.
 */
public class RouteFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.route_fragment_layout, null);
        Bundle bundle = this.getArguments();
        String destination = bundle.getString("destination");

        TextView destinationTextView = (TextView) v.findViewById(R.id.destination);

        destinationTextView.setText(destination);


        return v;
    }
}
