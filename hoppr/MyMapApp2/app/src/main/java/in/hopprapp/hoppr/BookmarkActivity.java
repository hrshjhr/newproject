package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

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
import org.json.JSONArray;
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
public class BookmarkActivity extends Activity {

    String id;

    List<String> route = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_activity_layout);

        Intent intent = this.getIntent();

        id = intent.getStringExtra("id");

        new GetSavedRoutes().execute();
    }

    private class GetSavedRoutes extends AsyncTask<Void , Void , Integer> {
        JSONObject responseJSON;
        JSONArray dataArray;
        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0;
            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants().getServerApiAddress())+"api/v1/GetSavedRoutes");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("userId" , id));

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

                    dataArray = responseJSON.getJSONObject("data").getJSONArray("favouriteRoutes");

                    for(int i =0 ; i < dataArray.length() ; i++){

                        route.add(dataArray.getJSONObject(i).getString("sourceHopstop") + " to " + dataArray.getJSONObject(i).getString("destinationHopstop")  );

                    }

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

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            ListView listView = (ListView) findViewById(R.id.bookmarkListView);
            String[] routeString = new String[route.size()];
            routeString = route.toArray(routeString);
            ListAdapter theAdapter = new ArrayAdapter<String>(getApplicationContext() , R.layout.route_list_card , R.id.card_text , routeString);

            listView.setAdapter(theAdapter);

            endLoader();
        }
    }

    private void endLoader() {
        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.loader_bookmark);
        mLinearLayout.setVisibility(View.INVISIBLE);
    }
}
