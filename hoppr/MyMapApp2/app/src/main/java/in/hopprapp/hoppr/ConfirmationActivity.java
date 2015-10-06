package in.hopprapp.hoppr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 8/8/15.
 */
public class ConfirmationActivity extends Activity {

    String source;
    String destination;
    String dutyId;
    String bus;
    String userId;
    int sourcePosition;
    int destinationPosition;
    String firstName;
    String walletId;
    String lastName;
    double distance;

    int time ;

    String id;

    int ticketAmount = 30;

    int seatsLeft;

    int ticketQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_layout);

        source = getIntent().getStringExtra("source");
        destination = getIntent().getStringExtra("destination");
        bus = getIntent().getStringExtra("busNumber");
        dutyId = getIntent().getStringExtra("dutyId");
        sourcePosition = getIntent().getIntExtra("sourcePosition", 0);
        destinationPosition = getIntent().getIntExtra("destinationPosition", 0);
        id = getIntent().getStringExtra("userId");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        distance = getIntent().getDoubleExtra("distance", 0.0);
        walletId = getIntent().getStringExtra("walletId");
        time = getIntent().getIntExtra("startTime" , 0);
        seatsLeft = getIntent().getIntExtra("seatsLeft" , 0);
        TextView busNumberTextView = (TextView) findViewById(R.id.bus_number);
        TextView busNameTextView = (TextView) findViewById(R.id.bus_name);
        TextView startTimeTextView  = (TextView) findViewById(R.id.startTime);
        TextView seatsLeftText  = (TextView) findViewById(R.id.availalable_seats);
        seatsLeftText.setText("Available Seats :" +  String.valueOf(seatsLeft));

        busNameTextView.setText("Bus Name : Force Traveller");
        busNumberTextView.setText("Bus Number : " + bus);
        startTimeTextView.setText("Start Time :" + String.valueOf(time/100) + ":" +String.valueOf(time%100) + " hours");

        TextView detailsTextView = (TextView) findViewById(R.id.hoppr_confirmation_text);
        detailsTextView.setText(source + " to " + destination);

        Spinner dSpinner = (Spinner) findViewById(R.id.spinner_ticket_number);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this ,
                R.array.number ,
                android.R.layout.simple_spinner_item);

        dSpinner.setAdapter(adapter);

        dSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ticketAmount = 30 * Integer.parseInt(parent.getItemAtPosition(position).toString());
                TextView ticketAmountTextView = (TextView) findViewById(R.id.ticket_amount);
                ticketAmountTextView.setText("Ticket Value : " + String.valueOf(ticketAmount));

                ticketQuantity = position + 1 ;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void busConfirmClickListener(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmationActivity.this);

        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("Confirm Booking");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new BookBus().execute();

                /*Intent ticketIntent = new Intent(getApplicationContext() , TicketActivity.class);
                startActivity(ticketIntent);*/

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

    public void backIconPress(View view) {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }

    private class BookBus extends AsyncTask<Void , Void , Integer>{
        JSONObject responseJSON;

        JSONObject successJSON;

        Boolean success;

        JSONObject identityJSON;

        JSONObject data;

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                Intent ticketActivity = new Intent(getApplication() , TicketActivity.class);

                try {
                    ticketActivity.putExtra("userName" , data.getString("userName"));
                    ticketActivity.putExtra("sourceStop" , data.getString("sourceStop"));
                    ticketActivity.putExtra("destinationStop" , data.getString("destinationStop"));
                    ticketActivity.putExtra("totalAmount" , data.getInt("totalAmount"));
                    ticketActivity.putExtra("ticketPrice" , data.getInt("ticketPrice"));
                    ticketActivity.putExtra("quantity" , data.getInt("quantity"));
                    ticketActivity.putExtra("ticketId" , data.getString("_id"));
                    ticketActivity.putExtra("walletId" , walletId);
                    startActivity(ticketActivity);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext() , "Error Requesting Data" , Toast.LENGTH_LONG).show();
                }
            }
            else if(integer == -1){
                Toast.makeText(getApplicationContext() , "Seats Not Available" , Toast.LENGTH_LONG).show();
            }

            else {
                Toast.makeText(getApplicationContext() , "Unrecognizable Error" , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0 ;



            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            Log.i("ABCD" , walletId);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/BookSeats");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("dutyId" , dutyId));
            nameValuePairs.add(new BasicNameValuePair("userId" , id));
            nameValuePairs.add(new BasicNameValuePair("sourceStop" , source));
            nameValuePairs.add(new BasicNameValuePair("destinationStop" , destination));
            nameValuePairs.add(new BasicNameValuePair("sourceStopPos" , Integer.toString(sourcePosition)));
            nameValuePairs.add(new BasicNameValuePair("destinationStopPos" , Integer.toString(destinationPosition)));
            nameValuePairs.add(new BasicNameValuePair("totalAmount" , Integer.toString(ticketAmount)));
            nameValuePairs.add(new BasicNameValuePair("currency" , "INR"));
            nameValuePairs.add(new BasicNameValuePair("walletId" , walletId));
            nameValuePairs.add(new BasicNameValuePair("ticketQuantity" , Integer.toString(ticketQuantity)));
            nameValuePairs.add(new BasicNameValuePair("ticketPrice" , Integer.toString(ticketAmount/ticketQuantity)));
            nameValuePairs.add(new BasicNameValuePair("userName" , firstName + " " + lastName));
            nameValuePairs.add(new BasicNameValuePair("distance" , Double.toString(distance)));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                responseJSON = new JSONObject(builder.toString());

                Log.i("ABCD" , responseJSON.toString());

                Boolean success = responseJSON.getBoolean("success");

                if(success){
                    data = responseJSON.getJSONObject("data");
                    value = 1;
                }

                else {
                    value = -1;
                }

                //data = responseJSON.getJSONObject("data");



            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return value;
        }
    }
}
