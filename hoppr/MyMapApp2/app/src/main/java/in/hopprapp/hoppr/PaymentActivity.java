package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
 * Created by root on 14/8/15.
 */
public class PaymentActivity extends Activity {
    String userId;
    String walletId;
    String code;
    Double money;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity_layout);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        walletId = intent.getStringExtra("walletId");
        new GetWallet().execute();
    }

    public void addMoney(View view) {
        EditText rechargeCodeEditText = (EditText) findViewById(R.id.recharge_text);
        if(rechargeCodeEditText.getText().toString().trim().length() != 8){
            Toast.makeText(getApplicationContext() , "Enter Valid Text" , Toast.LENGTH_SHORT).show();
        }
        else {
            code = rechargeCodeEditText.getText().toString().trim();
            new AddRechargeCode().execute();
        }
    }

    private class AddRechargeCode extends AsyncTask<Void , Void , Integer> {
        JSONObject responseJSON;
        JSONObject data;
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                TextView moneyTextView = (TextView) findViewById(R.id.money);
                try {
                    moneyTextView.setText("Rs. " + data.getJSONObject("cash").getString("totalAmount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext() , "Enter Valid Sequence" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0;
            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/AddMoneyToWallet");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("userId" , userId));
            nameValuePairs.add(new BasicNameValuePair("walletId" , walletId));
            nameValuePairs.add(new BasicNameValuePair("uCode" , code));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                responseJSON = new JSONObject(builder.toString());

                Boolean success = responseJSON.getBoolean("success");

                if(success){
                    data = responseJSON.getJSONObject("data");
                    value = 1;
                }
                else {
                    value = -1;
                }

                Log.i("ABCD", responseJSON.toString());
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

    private class GetWallet extends AsyncTask<Void , Void , Integer>{

        JSONObject responseJSON;


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                try {
                    money = responseJSON.getJSONObject("data").getJSONObject("cash").getDouble("totalAmount");
                    TextView textView = (TextView) findViewById(R.id.money);
                    textView.setText("Rs. " + String.valueOf(money));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext() , "Cannot Read Wallet" , Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext() , "Server unable to get your request" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0;
            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/GetMyWallet");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("walletId" , walletId));
            nameValuePairs.add(new BasicNameValuePair("userId" , userId));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                responseJSON = new JSONObject(builder.toString());

                Boolean success = responseJSON.getBoolean("success");

                if(success){
                    value = 1;
                }
                else {
                    value = -1;
                }

                Log.i("ABCD", responseJSON.toString());


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
