package in.hopprapp.hoppr;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 14/8/15.
 */
public class ConfirmationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.chauffeur_suboption_layout, null);
        Bundle bundle = this.getArguments();


        return v;
    }
}
