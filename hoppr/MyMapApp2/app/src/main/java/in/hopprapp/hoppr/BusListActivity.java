package in.hopprapp.hoppr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by root on 8/8/15.
 */
public class BusListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buslist_layout);


        TextView mTextView = (TextView) findViewById(R.id.available_hopprs_text);

        String destination = getIntent().getExtras().getString("Location");
        if(!destination.equals((""))){
            mTextView.setText("Hopprs To " + destination);

        }
        else {
            mTextView.setText("Hopprs Available");
        }
        String[] Bus = {"From Park Circus" , "From Exide", "From Sector-V", "From Park Circus" , "From New Town"};

        ListAdapter theAdapter = new ArrayAdapter<String>(this , R.layout.card_row_layout , R.id.dest_list_text ,Bus);

        ListView listView = (ListView) findViewById(R.id.hoppr_card);

        listView.setAdapter(theAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BusListActivity.this);

                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Confirm Booking");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

}
