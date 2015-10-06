package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
 * Created by root on 13/9/15.
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_layout);

        new GetLoginStatus().execute();
    }

    private class GetLoginStatus extends AsyncTask<Void,Void,Void>{

        String phone;
        String password;
        String firstName;
        String lastName;


        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences settings = getSharedPreferences("Hoppr App", 0);
            phone = settings.getString("phoneNumber1", "");
            password = settings.getString("password1" , "");
            firstName = settings.getString("firstName" , "");
            lastName = settings.getString("lastName" , "");

            if(phone.equals("") || password.equals("")){
                Intent loginIntent = new Intent(getApplicationContext() , SignInSignUpActivity.class);
                startActivity(loginIntent);
            }

            else {
                HttpClient httpClient = new DefaultHttpClient();
                final HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
                httpClient = new DefaultHttpClient(httpParams);
                HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/SignIn");
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("phoneNumber" , "+91" + phone));
                nameValuePairs.add(new BasicNameValuePair("password", password));


                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                    StringBuilder builder = new StringBuilder();
                    for(String line = null ; (line = reader.readLine()) != null ;){
                        builder.append(line).append("\n");
                    }

                    JSONObject responseJSON = new JSONObject(builder.toString());



                    //JSONObject successJSON = responseJSON.getJSONObject("success");

                    if(responseJSON.has("data")){

                        String id = responseJSON.getJSONObject("data").getString("_id");

                        JSONObject identityJSON = responseJSON.getJSONObject("data").getJSONObject("identity");

                        String phone = identityJSON.getString("phoneNumber");

                        String email = "";

                        if(identityJSON.has("emailId")){
                            email = identityJSON.getString("emailId");
                        }



                        HttpClient httpClient3 = new DefaultHttpClient();
                        final HttpParams httpParams3 = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams3, 30000);
                        httpClient3 = new DefaultHttpClient(httpParams3);
                        HttpPost httpPost3 = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/GetActiveTickets");
                        List<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>(2);
                        nameValuePairs3.add(new BasicNameValuePair("userId" , id));

                        httpPost3.setEntity(new UrlEncodedFormEntity(nameValuePairs3));
                        HttpResponse httpResponse3 = httpClient3.execute(httpPost3);
                        BufferedReader reader3 = new BufferedReader(new InputStreamReader(httpResponse3.getEntity().getContent()));
                        StringBuilder builder3 = new StringBuilder();
                        for(String line = null ; (line = reader3.readLine()) != null ;){
                            builder3.append(line).append("\n");
                        }

                        JSONObject ticketJSON = new JSONObject(builder3.toString());

                        Log.i("ABCD" , ticketJSON.toString());

                        Log.i("ABCD" , "Getting Response");

                        Boolean success = ticketJSON.getBoolean("success");

                        if(success && (ticketJSON.has("data"))){

                            JSONObject ticketData = ticketJSON.getJSONObject("data");

                            Log.i("ABCD", ticketData.toString());

                            Intent mapIntent = new Intent(getApplicationContext() , TicketActivity.class);

                            mapIntent.putExtra("phoneNumber" , phone);

                            mapIntent.putExtra("ticketId" , ticketData.getString("_id"));

                            mapIntent.putExtra("email" , email);

                            mapIntent.putExtra("userName" , identityJSON.getString("firstName") + identityJSON.getString("lastName"));

                            mapIntent.putExtra("sourceStop" , ticketData.getString("sourceStop"));

                            mapIntent.putExtra("destinationStop" , ticketData.getString("destinationStop"));

                            mapIntent.putExtra("ticketPrice" , ticketData.getInt("ticketPrice"));

                            mapIntent.putExtra("totalAmount" , ticketData.getInt("totalAmount"));

                            mapIntent.putExtra("quantity" , ticketData.getInt("quantity"));

                            mapIntent.putExtra("walletId" , responseJSON.getJSONObject("data").getJSONObject("wallet").getString("walletId"));

                            mapIntent.putExtra("id" , id);

                            Log.i("ABCD" , responseJSON.getJSONObject("data").getJSONObject("wallet").getString("walletId") );


                            startActivity(mapIntent);

                        }
                        else {

                            Intent mapIntent = new Intent(getApplicationContext() , MapsActivity.class);

                            mapIntent.putExtra("phoneNumber" , phone);

                            mapIntent.putExtra("email" , email);

                            mapIntent.putExtra("firstName" , identityJSON.getString("firstName"));

                            mapIntent.putExtra("lastName" , identityJSON.getString("lastName"));

                            mapIntent.putExtra("walletId" , responseJSON.getJSONObject("data").getJSONObject("wallet").getString("walletId"));

                            mapIntent.putExtra("id" , id);

                            Log.i("ABCD" , responseJSON.getJSONObject("data").getJSONObject("wallet").getString("walletId") );


                            startActivity(mapIntent);

                        }




                    }

                    else {
                        String id = responseJSON.getString("userId");

                        HttpClient httpClient2 = new DefaultHttpClient();
                        final HttpParams httpParams2 = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams2, 30000);
                        httpClient2 = new DefaultHttpClient(httpParams2);
                        HttpPost httpPost2 = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/ResendOTP");

                        List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
                        nameValuePairs2.add(new BasicNameValuePair("userId" , id));

                        httpPost2.setEntity(new UrlEncodedFormEntity(nameValuePairs2));
                        HttpResponse httpResponse2 = httpClient2.execute(httpPost2);



                        JSONObject validation = responseJSON.getJSONObject("validation");

                        String email = validation.getString("emailId");

                        Intent mapIntent = new Intent(getApplicationContext() , VerifyOTP.class);

                        mapIntent.putExtra("phoneNumber" , phone);

                        mapIntent.putExtra("password" , password);

                        mapIntent.putExtra("email" , email);

                        mapIntent.putExtra("firstName" , validation.getString("firstName"));

                        mapIntent.putExtra("lastName" , validation.getString("lastName"));

                        mapIntent.putExtra("id", id);


                        startActivity(mapIntent);


                    }



                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {

                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }





            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}

