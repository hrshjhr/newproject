package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * Created by root on 6/8/15.
 */
public class LoginActivity extends Activity {
    String username;
    String password;
    String firstName ;
    String lastName ;
    String emailId;
    protected String[] mNavigationTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

    }

    private void drawLoader(){
        LinearLayout loader = (LinearLayout) findViewById(R.id.loader_log_in);
        loader.setVisibility(View.VISIBLE);
    }

    private void endLoader(){
        LinearLayout loader = (LinearLayout) findViewById(R.id.loader_log_in);
        loader.setVisibility(View.INVISIBLE);
    }


    public void signUpClickHandler(View view) {

        EditText userNameEditText = (EditText) findViewById(R.id.Email);
        EditText passwordEditText = (EditText) findViewById(R.id.Password);
        EditText firstNameText = (EditText) findViewById(R.id.FirstName);
        EditText lastNameText = (EditText) findViewById(R.id.LastName);
        EditText emailEditText = (EditText) findViewById(R.id.Email_text);
        emailId  = emailEditText.getText().toString();

        username = userNameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        firstName = firstNameText.getText().toString().trim();
        lastName = lastNameText.getText().toString().trim();

        if(username.length() != 10){
            Toast.makeText(getApplicationContext() , "Wrong Phone Number Format" , Toast.LENGTH_SHORT).show();
        }

        else if((password.length()<6) || (password.length()>30)){
            Toast.makeText(getApplicationContext() , "Password should be between 6 to 30" , Toast.LENGTH_SHORT).show();
        }

        else if(firstName.equals("") || lastName.equals("")){
            Toast.makeText(getApplicationContext() , "First/Last Name cant be empty" , Toast.LENGTH_SHORT).show();
        }
        else if(emailId.equals("")){
            Toast.makeText(getApplicationContext() , "Email cant be empty" , Toast.LENGTH_SHORT).show();
        }

        else {

            drawLoader();

            new SignUpRequest().execute();

        }



        /*Intent mapViewIntent = new Intent(this , MapsActivity.class);
        startActivity(mapViewIntent);
        finish();*/
    }

    public class SignUpRequest extends AsyncTask<Void , Void , Integer>{

        JSONObject responseJSON;

        JSONObject successJSON;

        Boolean success;

        JSONObject identityJSON;

        @Override
        protected Integer doInBackground(Void... params) {

            int value = 0 ;



            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/SignUp");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("phoneNumber" , "+91" + username));
            nameValuePairs.add(new BasicNameValuePair("password" , password));
            nameValuePairs.add(new BasicNameValuePair("firstName" , firstName));
            nameValuePairs.add(new BasicNameValuePair("lastName" , lastName));
            nameValuePairs.add(new BasicNameValuePair("emailId" , emailId));



            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for(String line = null ; (line = reader.readLine()) != null ;){
                    builder.append(line).append("\n");
                }
                responseJSON = new JSONObject(builder.toString());





                success = responseJSON.getBoolean("success");



                if(success){

                   /* HttpPost loginHttpPost = new HttpPost("http://caravan-50606.onmodulus.net/api/v1/VerifyOTP");

                    loginHttpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    httpResponse = httpClient.execute(loginHttpPost);

                    BufferedReader newReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                    StringBuilder stringBuilder = new StringBuilder();
                    for(String line = null ; (line = newReader.readLine()) != null ;){
                        stringBuilder.append(line).append("\n");
                    }
                    successJSON = new JSONObject(stringBuilder.toString()).getJSONObject("success");*/

                    value = 1;



                }

                else if(responseJSON.getInt("errorCode") == 201){
                    value = 5;
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

                    mapIntent.putExtra("phoneNumber" , username);

                    mapIntent.putExtra("email" , email);

                    mapIntent.putExtra("firstName" , validation.getString("firstName"));

                    mapIntent.putExtra("lastName" , validation.getString("lastName"));

                    mapIntent.putExtra("id", id);


                    startActivity(mapIntent);


                    value = -1;
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


            return value;


        }

        @Override
        protected void onPostExecute(Integer aVoid) {

            if(aVoid == 1){

                try {
                    Intent mapIntent = new Intent(getApplicationContext() , VerifyOTP.class);

                    mapIntent.putExtra("phoneNumber" , username);

                    mapIntent.putExtra("id" , responseJSON.getString("userId"));

                    mapIntent.putExtra("email" , emailId);

                    mapIntent.putExtra("firstName" , firstName);

                    mapIntent.putExtra("password" , password);

                    mapIntent.putExtra("lastName" , lastName);


                    startActivity(mapIntent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if (aVoid == -1){
                endLoader();
                Toast.makeText( getApplicationContext() , "Can't Sign Up" , Toast.LENGTH_SHORT).show();
            }
            else if(aVoid == 5){
                endLoader();
                Toast.makeText( getApplicationContext() , "User Already Exists" , Toast.LENGTH_SHORT).show();
            }
            else {
                endLoader();
                Toast.makeText( getApplicationContext() , "Unrecognized Error" , Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);

        }
    }



}
