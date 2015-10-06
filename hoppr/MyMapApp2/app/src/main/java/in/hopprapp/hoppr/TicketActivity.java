package in.hopprapp.hoppr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
 * Created by root on 17/8/15.
 */
public class TicketActivity extends Activity {

    String userName;
    String sourceStop;
    String destinationStop;
    int totalAmount;
    int ticketPrice;
    int quantity;
    String ticketId;
    String walletId;
    String dutyId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TicketActivity.this);

        alertDialogBuilder.setTitle("Are You Sure You Want To Cancel?");
        alertDialogBuilder.setMessage("Cancellation charge of Rs. 5 is applicable");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new CancelRequest().execute();

                /*Intent ticketIntent = new Intent(getApplicationContext() , TicketActivity.class);
                startActivity(ticketIntent);*/

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new getTicketUpdate().execute();
        setContentView(R.layout.ticket_activity_layout);

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        sourceStop = intent.getStringExtra("sourceStop");
        destinationStop = intent.getStringExtra("destinationStop");
        totalAmount = intent.getIntExtra("totalAmount", 0);
        ticketPrice = intent.getIntExtra("ticketPrice", 0);
        quantity = intent.getIntExtra("quantity", 0);
        ticketId = intent.getStringExtra("ticketId");
        walletId = intent.getStringExtra("walletId");
        dutyId = intent.getStringExtra("dutyId");
        TextView textViewName = (TextView) findViewById(R.id.textView2);
        textViewName.setText("User Name : " + userName);
        TextView textViewSource = (TextView) findViewById(R.id.textView5);
        textViewSource.setText("Source : " + sourceStop);
        TextView textViewDestination = (TextView) findViewById(R.id.textView3);
        textViewDestination.setText("Destination : " + destinationStop);
        TextView textViewTotal = (TextView) findViewById(R.id.textView4);
        textViewTotal.setText("Total Price : " + String.valueOf(totalAmount));
        TextView textViewPrice = (TextView) findViewById(R.id.textView6);
        textViewPrice.setText("Ticket Price : " + String.valueOf(ticketPrice));
        TextView textViewQuantity = (TextView) findViewById(R.id.textView7);
        textViewQuantity.setText("Number of Tickets : " + quantity);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TicketActivity.this);

            alertDialogBuilder.setTitle("Are You Sure You Want To Cancel?");
            alertDialogBuilder.setMessage("Cancellation charge of Rs. 5 is applicable");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    new CancelRequest().execute();

                /*Intent ticketIntent = new Intent(getApplicationContext() , TicketActivity.class);
                startActivity(ticketIntent);*/

                }
            });
            return true;
        }
        else {
            return onKeyDown(keyCode , event);
        }
    }

    public void trackBus(View view) {
        Intent trackLocalBusIntent = new Intent(getApplicationContext() , TrackLocalBusActivity.class);
        startActivity(trackLocalBusIntent);
    }

    public void reportToUs(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("plain/text");
        sendIntent.setData(Uri.parse("care@hopprapp.in"));
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"care@hopprapp.in"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT , "Report Problem");
        startActivity(sendIntent);
    }

    public void onCancelClickHandler(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TicketActivity.this);

        alertDialogBuilder.setTitle("Are You Sure You Want To Cancel?");
        alertDialogBuilder.setMessage("Cancellation charge of Rs. 5 is applicable");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new CancelRequest().execute();

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

    private class getTicketUpdate extends AsyncTask<Void , Void , Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class CancelRequest extends AsyncTask<Void , Void , Integer> {

        JSONObject responseJSON;
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                finish();
            }
            else {
                Toast.makeText(getApplicationContext() , "Cannot Cancel Ticket" , Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0;

            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);

            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/CancelTickets");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("ticketId", ticketId));
            nameValuePairs.add(new BasicNameValuePair("walletId", walletId));
            nameValuePairs.add(new BasicNameValuePair("dutyId", dutyId));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }

                responseJSON = new JSONObject(builder.toString());

                Log.i("ABCD", responseJSON.toString());

                Boolean success = responseJSON.getBoolean("success");

                if(success){
                    value = 1;
                }

                else {
                    value = -1;
                }




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
