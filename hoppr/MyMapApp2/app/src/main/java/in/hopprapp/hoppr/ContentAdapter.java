package in.hopprapp.hoppr;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by root on 2/9/15.
 */
public class ContentAdapter extends RecyclerView.Adapter {

    String[] mDataset ;
    public ContentAdapter(String[] favoriteTVShows) {

        mDataset = favoriteTVShows;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            mTextView = itemView;
        }
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_card_layout , viewGroup , false);



        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
