package in.hopprapp.hoppr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by root on 21/9/15.
 */
public class SignInActivity extends Activity {

    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity_layout);
    }

    public void loginClickHandler(View view) {


        EditText userNameEditText = (EditText) findViewById(R.id.Email);
        EditText passwordEditText = (EditText) findViewById(R.id.Password);

        username = userNameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        if(username.equals("") || password.equals("")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    SignInActivity.this
            );

            alertDialogBuilder.setTitle("Empty Fields");
            alertDialogBuilder.setMessage(Html.fromHtml("Username/Password cannot be empty"));
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if(password.trim().length() < 6){
            Toast.makeText(getApplicationContext() , "Password cannot be less than 6 letters" , Toast.LENGTH_SHORT).show();
        }

        else {

            showLoader();

        }

        new LoginRequest().execute();

        View view1 = this.getCurrentFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);


    }

    private void showLoader() {
        LinearLayout loader = (LinearLayout) findViewById(R.id.loader_sign_in);
        loader.setVisibility(View.VISIBLE);
    }

    private void endLoader() {
        LinearLayout loader = (LinearLayout) findViewById(R.id.loader_sign_in);
        loader.setVisibility(View.INVISIBLE);
    }

    public void forgotPasswordClickHandler(View view) {
        Toast.makeText(getApplicationContext() , "Enter the Newly Generated Password" , Toast.LENGTH_SHORT).show();
        new ForgotPassword().execute();
    }


    public class LoginRequest extends AsyncTask<Void , Void , Integer> {

        JSONObject responseJSON;

        JSONObject successJSON;

        JSONObject identityJSON;

        @Override
        protected Integer doInBackground(Void... params) {

            int value = 0;

            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/SignIn");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("phoneNumber" , "+91" + username));
            nameValuePairs.add(new BasicNameValuePair("password" , password));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for(String line = null ; (line = reader.readLine()) != null ;){
                    builder.append(line).append("\n");
                }
                responseJSON = new JSONObject(builder.toString());

                Boolean success = responseJSON.getBoolean("success");



                //successJSON = responseJSON.getJSONObject("success");

                //identityJSON = responseJSON.getJSONObject("data").getJSONObject("identity");


                if(responseJSON.has("data")){
                    value = 1;

                    identityJSON = responseJSON.getJSONObject("data").getJSONObject("identity");

                    SharedPreferences prefs = getSharedPreferences("Hoppr App" , Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("phoneNumber1" , username);
                    editor.putString("password1" , password );
                    editor.apply();

                }
                else if(!success){
                   value = 2;
                }
                else{
                    value = -1;
                    String id = responseJSON.getString("userId");

                    HttpClient httpClient2 = new DefaultHttpClient();
                    final HttpParams httpParams2 = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams2, 30000);
                    httpClient2 = new DefaultHttpClient(httpParams2);
                    HttpPost httpPost2 = new HttpPost(Constants.getServerApiAddress() + "api/v1/ResendOTP");

                    List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
                    nameValuePairs2.add(new BasicNameValuePair("userId" , id));

                    httpPost2.setEntity(new UrlEncodedFormEntity(nameValuePairs2));
                    HttpResponse httpResponse2 = httpClient2.execute(httpPost2);

                    Log.i("ABCD", "Success ResendOTP");

                    JSONObject validation = responseJSON.getJSONObject("validation");

                    String email = validation.getString("emailId");

                    Intent mapIntent = new Intent(getApplicationContext() , VerifyOTP.class);

                    mapIntent.putExtra("phoneNumber" , username);

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
            } catch (NullPointerException e){

            }


            return value;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {

            if(aVoid == 1){

                try {
                    Intent mapIntent = new Intent(getApplicationContext() , MapsActivity.class);

                    mapIntent.putExtra("phoneNumber" , identityJSON.getString("phoneNumber"));

                    mapIntent.putExtra("id" , responseJSON.getJSONObject("data").getString("_id"));

                    mapIntent.putExtra("firstName" , identityJSON.getString("firstName"));

                    mapIntent.putExtra("email" , identityJSON.getString("emailId"));

                    mapIntent.putExtra("lastName" , identityJSON.getString("lastName"));

                    mapIntent.putExtra("walletId" , responseJSON.getJSONObject("data").getJSONObject("wallet").getString("walletId"));

                    Log.i("ABCD" , responseJSON.getJSONObject("data").getJSONObject("wallet").getString("walletId") );

                    startActivity(mapIntent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            else if (aVoid == 2){

                endLoader();
                Toast.makeText(getApplicationContext(), "Wrong Phone Number / Password", Toast.LENGTH_SHORT).show();
            }
            else if(aVoid == 0) {
                endLoader();
                Toast.makeText( getApplicationContext() , "Unrecognized Error" , Toast.LENGTH_SHORT).show();
            }
            else {
                endLoader();
                Toast.makeText( getApplicationContext() , "Please Confirm OTP" , Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);


        }
    }

    private class ForgotPassword extends AsyncTask<Void , Void , Integer> {
        JSONObject responseJSON;

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                Toast.makeText(getApplicationContext() , "Please Enter The New Password Generated" , Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext() , "Error Reaching Server" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/SignIn");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("phoneNumber" , "+91" + username));
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
