package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
 * Created by root on 23/9/15.
 */
public class VerifyOTP extends Activity {
    String OTP;
    String email;
    String id;
    String phoneNumber;
    String firstName;
    String lastName;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp_layout);
        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");
        phoneNumber = intent.getStringExtra("phoneNumber");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");

    }

    public void verifyOTP(View view) {
        EditText editText = (EditText) findViewById(R.id.otp);
        if(editText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext() , "Enter OTP" , Toast.LENGTH_SHORT).show();
        }
        else {
            OTP = editText.getText().toString();
            new ExecuteOTPVerification().execute();
        }
    }

    public void resendOTP(View view) {
        new ResendOTP().execute();
    }

    private class ExecuteOTPVerification extends AsyncTask<Void , Void , Integer> {
        JSONObject responseJSON;
        Boolean success;
        JSONObject successJSON;

        String walletId;


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){

                Intent intent = new Intent(getApplicationContext() , MapsActivity.class);
                intent.putExtra("firstName" , firstName);
                intent.putExtra("lastName" , lastName);
                intent.putExtra("email" , email);
                try {
                    intent.putExtra("id" , responseJSON.getString("userId"));
                } catch (JSONException e) {
                    intent.putExtra("id" , "");
                }
                intent.putExtra("phoneNumber" , phoneNumber);

                intent.putExtra("walletId" , walletId);

                startActivity(intent);

            }
            else if(integer == -1){
                Toast.makeText( getApplicationContext() , "Server is Reporting Error" , Toast.LENGTH_SHORT).show();
            }
            else if(integer == 3){
                Toast.makeText( getApplicationContext() , "Wrong OTP" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0 ;

            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/VerifyOTP");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("userId" , id));
            nameValuePairs.add(new BasicNameValuePair("OTP" , OTP));
            nameValuePairs.add(new BasicNameValuePair("phoneNumber", "+91" + phoneNumber));
            nameValuePairs.add(new BasicNameValuePair("firstName" , firstName));
            nameValuePairs.add(new BasicNameValuePair("lastName" , lastName));
            nameValuePairs.add(new BasicNameValuePair("password" , password));



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

                success = responseJSON.getBoolean("success");

                if(success){



                    HttpPost loginHttpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/SignIn");

                    loginHttpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    httpResponse = httpClient.execute(loginHttpPost);

                    BufferedReader newReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                    StringBuilder stringBuilder = new StringBuilder();
                    for(String line = null ; (line = newReader.readLine()) != null ;){
                        stringBuilder.append(line).append("\n");
                    }

                    successJSON = new JSONObject(stringBuilder.toString());



                    Log.i("ABCD" , successJSON.toString());

                    if(successJSON.has("validation")){
                        value = 3;


                    }




                    else if(successJSON.has("data")){
                        value = 1;
                        walletId = successJSON.getJSONObject("data").getJSONObject("wallet").getString("walletId");
                    }


                    //Log.i("ABCD" , successJSON.toString());

                    SharedPreferences prefs = getSharedPreferences("Hoppr App" , Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("phoneNumber" , phoneNumber);
                    editor.putString("password" , password );
                    editor.putString("firstName" , firstName);
                    editor.putString("lastName" , lastName);
                    editor.apply();

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

    private class ResendOTP extends AsyncTask<Void , Void , Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0 ;

            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/SignIn");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("userId" , id));
            nameValuePairs.add(new BasicNameValuePair("password" , password));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for(String line = null ; (line = reader.readLine()) != null ;){
                    builder.append(line).append("\n");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return value;
        }
    }
}
