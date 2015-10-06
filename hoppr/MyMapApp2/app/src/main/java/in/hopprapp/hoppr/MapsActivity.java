package in.hopprapp.hoppr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.views.MapView;

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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements ConnectionCallbacks, LocationListener , OnConnectionFailedListener, OnMapReadyCallback {
    GoogleApiClient mGoogleApiClient;
    double mLatitude;
    double mLongitude;
    boolean chauffeurIconClick = true;
    boolean thirteenIconClick = false;
    boolean twentysixIconClick = false;

    String source;
    String destination;
    TextView priceText;
    Dialog dialog;
    Marker destinationMarker;
    Marker sourceMarker;
    String email;
    MapView mapView;

    double routeGps[][] = {
            {
                    22.517565 , 88.3535298
            } ,

            {
                    22.5180676 , 88.3562067
            } ,
            {
                    22.5191525 , 88.3623774
            } ,
            {
                    22.5199136 , 88.365115
            } ,
            {
                    22.5196243 , 88.3762807
            } ,
            {
                    22.519588 , 88.3824576
            } ,
            {
                    22.519588 , 88.3824576
            } ,
            {
                    22.5167392 , 88.3884089
            } ,
            {
                    22.5136177 , 88.40015
            } ,
            {
                    22.5136177 , 88.40015
            } ,
            {
                    22.52594605 , 88.39560965
            } ,
            {
                    22.5393213 , 88.3976537
            } ,
            {
                    22.5491933 , 88.4009008
            } ,
            {
                    22.55582326 , 88.41180387
            } ,
            {
                    22.57119329 , 88.41995896
            } ,
            {
                    22.57082201 , 88.42628955
            } ,
            {
                    22.56902831 , 88.43067547
            } ,
            {
                    22.57408886 , 88.43310186
            }
    };



    int applicationState;

/*    Double busLatitude;
    Double busLongitude;*/

    List<Double> busLatitude = new ArrayList<Double>();
    List<Double> busLongitude = new ArrayList<Double>();

    List<String> imeiNumber = new ArrayList<String>();


    double[] latitude = {22.541447 , 22.540939 , 22.543107 , 22.539081 , 22.542995 ,
            22.549297 , 22.557005 , 22.572659 , 22.569063 , 22.575730
    };
    double[] longitude ={
            88.347646 , 88.358401 , 88.366181 , 88.378803 , 88.398169 ,
            88.401334 , 88.411966 , 88.420699 , 88.430205 , 88.434175
    };
    double[] distance;
    Location mLastLocation;
    DriverFragment mDriverFragment;
    ConfirmationFragment mConfirmationFragment;
    com.mapbox.mapboxsdk.geometry.LatLng position;
    LocationRequest mLocationRequest;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar actionBarToolBar;
    int minDistanceIndex = 0 ;

    LocationManager mLocationManager;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    
    private String url = "http://caravanconnect-50862.onmodulus.net";
    
    private Socket mSocket;

    private AutoCompleteTextView mAutoCompleteTextView;

    private AutoCompleteTextView dAutoCompleteTextView;

    private PlaceAutocompleteAdapter mAdapter;

    List<Marker> mMarkers = new ArrayList<Marker>();

    private PlaceAutocompleteAdapter dAdapter;






    private Emitter.Listener onPositionUpdate = new Emitter.Listener(){

        @Override
        public void call(final Object... args) {
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Double latitude = null;
                    Double longitude = null;
                    try{
                        latitude = Double.parseDouble(data.getString("latitude"));
                        longitude = Double.parseDouble(data.getString("longitude"));
                        
                    }catch (JSONException e){
                        return;
                    }finally {
                        handlePositionChange(latitude , longitude);
                    }
                }
            });
        }
    };






    private Emitter.Listener onConnectionError = new Emitter.Listener(){

        @Override
        public void call(Object... args) {

            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*Toast.makeText(getApplicationContext() , "Error Connecting to Network" ,
                            Toast.LENGTH_SHORT).show();*/
                }
            });

        }
    };


    private Emitter.Listener onBookingRequestSend = new Emitter.Listener(){

        @Override
        public void call(Object... args) {

        }
    };
    private Emitter.Listener acceptedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getApplicationContext(), "Your request is accepted", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    mDriverFragment = new DriverFragment();

                    Geocoder coder = new Geocoder(getApplicationContext());
                    /*List<Address> address;
                    EditText destinationEditText = (EditText) findViewById(R.id.destination_text_search);
                    String destination = destinationEditText.getText().toString();
                    Double destinationLatitude;
                    Double destinationLongitude;
                    try{
                        address = coder.getFromLocationName(destination , 5);
                        if(address != null){
                            Address location = address.get(0);
                            destinationLatitude = location.getLatitude();
                            destinationLongitude = location.getLongitude();

                            LatLng destinationPosition = new LatLng(destinationLatitude , destinationLongitude);

                            mMap.addMarker(new MarkerOptions().position(destinationPosition));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            });
        }
    };

    private Object mPlaceDetailsText;
    private TextView mPlaceDetailsAttribution;
    private Emitter.Listener onGpsPositionReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("ABCD" , "Receiving GPS update");
                    JSONObject makerPosition = (JSONObject) args[0];
                    try {

                        Log.i("ABCD" , makerPosition.toString());

                        Double latitude = makerPosition.getDouble("locationLatitude");
                        Double longitude = makerPosition.getDouble("locationLongitude");
                        String thisBusImeiNumber = makerPosition.getString("duty");

                        int index = findPositionOfBus(thisBusImeiNumber);

                        Double busActualLatitude;
                        Double busActualLongitude;

                        if(index == -1){
                            Log.i("ABCD", "New Bus");
                            busLatitude.add(latitude);
                            busLongitude.add(longitude);

                            busActualLatitude = latitude;
                            busActualLongitude = longitude;

                            imeiNumber.add(thisBusImeiNumber);

                            /*mMarkers.add(mMap.addMarker(new MarkerOptions().
                                    position(new LatLng(busActualLatitude , busActualLongitude)).
                                            title("Bus Here").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker_3))))  ;

*/

                        }

                        else {
                            Log.i("ABCD" , "OLD Bus");
                            busActualLatitude = latitude;
                            busActualLongitude = longitude;

                            busLatitude.set(index, busActualLatitude) ;
                            busLongitude.set(index, busActualLongitude);

                            Marker thisBusMarker = mMarkers.get(index);

/*
                            thisBusMarker.setPosition(new LatLng(busActualLatitude , busActualLongitude));
*/

                            mMarkers.set(index , thisBusMarker);

                        }




                        /*busLatitude = latitude;
                        busLongitude = longitude;

                        */

                        //Toast.makeText(getApplicationContext() , thisBusImeiNumber , Toast.LENGTH_SHORT).show();



                        /*Toast.makeText(getApplicationContext() , String.valueOf(latitude) + String.valueOf(longitude) ,
                                Toast.LENGTH_SHORT).show();

                        Log.i("abcd", String.valueOf(latitude) + String.valueOf(longitude));

                        mMap.clear();


                        Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(busLatitude , busLongitude)).
                        title("Bus Here"));*/


                    } catch (JSONException e) {

                    }
                }
            });


        }
    };

    private int findPositionOfBus(String thisBusImeiNumber) {

        for(int i = 0 ; i < imeiNumber.size() ; i++){
            if(thisBusImeiNumber.equals(imeiNumber.get(i))){
                return i;
            }

        }
        for(int i = 0 ; i < imeiNumber.size() ; i++){
            Log.i("ABCD" , imeiNumber.get(i));

        }
        return -1;
    }

    private void handlePositionChange(Double latitude, Double longitude) {

    }


    {
        try{
            mSocket = IO.socket(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    String firstName;
    String lastName;
    String id;
    String phoneNumber;
    String walletId;
    Spinner mSpinner;
    Spinner dSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager = (LocationManager)MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setMessage(MapsActivity.this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(MapsActivity.this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    MapsActivity.this.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(MapsActivity.this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }


        setContentView(R.layout.activity_maps);

        mapView = (com.mapbox.mapboxsdk.views.MapView) this.findViewById(R.id.mapview);
        mapView.getController().setZoom(16);
        mapView.getController().setCenter(new LatLng(45, 60));
        mapView.setUserLocationEnabled(true);
        mapView.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW);





        /*LocationManager lm = (LocationManager)MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setMessage("GPS Network Not Available");
            dialog.setPositiveButton(MapsActivity.this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    MapsActivity.this.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(MapsActivity.this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
*/

        TextView textView = (TextView) findViewById(R.id.profile_name);

        Intent intent = this.getIntent();

        id = intent.getStringExtra("id");

        phoneNumber = intent.getStringExtra("phoneNumber");

        firstName = intent.getStringExtra("firstName" );

        lastName = intent.getStringExtra("lastName");

        email = intent.getStringExtra("email");

        walletId = intent.getStringExtra("walletId");

        if( !(firstName.equals("")) || !(lastName.equals(""))){
            textView.setText(firstName + " " + lastName);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mSpinner = (Spinner) findViewById(R.id.spinnerSource);

        dSpinner = (Spinner) findViewById(R.id.destinationSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this ,
                R.array.places_all_source ,
                android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> dadapter = ArrayAdapter.createFromResource(this ,
                R.array.places_all_destination ,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        dSpinner.setAdapter(dadapter);



        
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionError);
        mSocket.on("receiveGpsData", onGpsPositionReceived);
        /*mSocket.on("PositionUpdate", onPositionUpdate);
        mSocket.on("driveraccepted", acceptedListener);
        mSocket.on("SendBookingRequest", onBookingRequestSend);*/
        mSocket.connect();
        JSONObject newJSONObject = new JSONObject();
        try {
            newJSONObject.put("routeNumber" , "AC4A");
            mSocket.emit("socketRoomInitialization" , newJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        applicationState = 0;


        buildGoogleApiClient();




        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.destinationEditText);




        // Retrieve the TextViews that will display details and attributions of the selected place.

        /*mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);*/



        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.

        mAutoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
       /* mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_KOLKATA , null);
*/
        mAutoCompleteTextView.setAdapter(mAdapter);


        dAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.routeEditText);

        dAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String source = mAutoCompleteTextView.getText().toString().trim();

                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() ,
                            InputMethodManager.HIDE_NOT_ALWAYS);

                if(source.equals("")){
                    applicationState = 2;
                }

                else {
                    applicationState = 4;
                    Intent allRouteActivityIntent = new Intent(getApplicationContext() , AllRouteActivity.class);
                    startActivity(allRouteActivityIntent);
                }

                return;
            }
        });

        /*dAdapter = new PlaceAutocompleteAdapter(this , android.R.layout.simple_list_item_1 , mGoogleApiClient

            , BOUNDS_KOLKATA , null);
*/
        dAutoCompleteTextView.setAdapter(dAdapter);

    }

    private void setAutoComplete() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("leaveRoom" , id);
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectionError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectionError);
        /*mSocket.off("PositionUpdate", onPositionUpdate);
        mSocket.off("SendBookingRequest", onBookingRequestSend);*/
    }



    protected synchronized void buildGoogleApiClient(){

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(status == ConnectionResult.SUCCESS) {


            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();

            createLocationRequest();
            updatePosition();



        }else if(status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("market://details?id=" + "com.google.android.gms")));
            }
            catch (Exception e){
                startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("https://play.google.com/store/apps/details?id=" + "com.google.android.gms")));
            }
        }

        else {



        }




    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(8000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    protected void onStop(){

        super.onStop();

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);



            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


            /*FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            RouteFragment routeFragment = new RouteFragment();
            fragmentTransaction.replace(R.id.fragment_container, routeFragment);
            fragmentTransaction.addToBackStack("tag").commit();*/

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully

                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            /*mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));*/

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            /*if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }*/



            places.release();
        }
    };

    @Override
    protected void onStart(){

        super.onStart();

        mGoogleApiClient.connect();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ABCD" , "OnResume");
        buildGoogleApiClient();
        setUpMapIfNeeded();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                source = parent.getItemAtPosition(position).toString();

                if (position != 0) {
                    if (sourceMarker != null) {

                        /*sourceMarker.remove();*/
                        /*sourceMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(routeGps[position - 1][0], routeGps[position - 1][1])));
*/
                        if (destinationMarker != null) {
                            LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                           /* builder1.include(sourceMarker.getPosition());
                            builder1.include(destinationMarker.getPosition());*/
                            LatLngBounds bounds = builder1.build();

                            int padding = 50;
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            mMap.animateCamera(cameraUpdate);
                        }

                    } else {

                        /*sourceMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(routeGps[position - 1][0], routeGps[position - 1][1])));
*/
                    }

                    if (destinationMarker != null) {
                        LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                        /*builder1.include(sourceMarker.getPosition());
                        builder1.include(destinationMarker.getPosition());*/
                        LatLngBounds bounds = builder1.build();

                        int padding = 50;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds , padding);

                        mMap.animateCamera(cameraUpdate);
                    }

                } else {
                    /*if (sourceMarker != null) {
                        sourceMarker.remove();
                    }*/
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        dSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destination = parent.getItemAtPosition(position).toString();

                if (position != 0) {
                    if (destinationMarker != null) {

                        /*destinationMarker.remove();*/
                        /*destinationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(routeGps[position - 1][0], routeGps[position - 1][1])));
*/
                        if (destinationMarker != null) {
                            LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                            /*builder1.include(sourceMarker.getPosition());
                            builder1.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder1.build();*/

                            int padding = 50;
                            /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            mMap.animateCamera(cameraUpdate);*/
                        }

                    } else {

                        /*destinationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(routeGps[position - 1][0], routeGps[position - 1][1])));
*/
                    }

                    if (sourceMarker != null) {
                        LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                        /*builder1.include(sourceMarker.getPosition());
                        builder1.include(destinationMarker.getPosition());
                        LatLngBounds bounds = builder1.build();*/

                        int padding = 50;
                        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds , padding);

                        mMap.animateCamera(cameraUpdate);*/
                    }

                } else {
                    /*if (destinationMarker != null) {
                        destinationMarker.remove();
                    }*/
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {

        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        /*if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }*/
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mLastLocation = getLastKnownLocation();
        if(mLastLocation != null){

            Log.i("ABCD" , "Location Not Null");


            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();

            position = new LatLng(mLatitude , mLongitude);

//            mMap.setMyLocationEnabled(true);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));



        }

        else {
            Log.i("ABCD" , "Location Null");
            Log.i("ABCD" , mGoogleApiClient.toString());
        }


    }

    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            //ALog.d("last known location, provider: %s, location: %s", provider,
               //     l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                //ALog.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.i("ABCD" , "OnConnected");
        mLastLocation = getLastKnownLocation();
        if(mLastLocation != null){
            Log.i("ABCD" , "Location Not Null");
            mLatitude = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));
            mLongitude = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
            position = new LatLng(mLatitude , mLongitude);
            /*
            Log.i("Adding Marker", mLatitude + " " + mLongitude);

            distance = new double[10];



            for(int i = 0 ; i < 10 ; i++){
                distance[i] = (latitude[i] - position.latitude)*(latitude[i] - position.latitude) +
                        (longitude[i] - position.longitude)*(longitude[i] - position.longitude);
            }
            double minDistance = distance[0];


            for(int i = 1 ; i < 10 ; i++){
                if(distance[i] < minDistance){
                    minDistance = distance[i];
                    minDistanceIndex = i ;
                }
            }

            route = new LatLng[10];
            for(int i=0 ; i< 10 ; i++){
                route[i] = new LatLng(latitude[i] , longitude[i]);
            }
            for(int i = 0 ; i < 10 ; i++  ){
                mMap.addMarker(new MarkerOptions().position(route[i]));
            }

            String address = null;
            try{
                Geocoder geocoder = new Geocoder( getApplicationContext() , Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(route[minDistanceIndex].latitude ,
                        route[minDistanceIndex].longitude , 1);

                if(addresses.size() > 0){
                    Address add = addresses.get(0);
                    address = add.getAddressLine(0);
                    address += add.getAddressLine(1);

                }

            }catch (Exception e){

            }

            EditText sourceLocation = (EditText) findViewById(R.id.editText);

            sourceLocation.setText(address);

            */
            /*mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));*/
            /*
            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {


                @Override
                public boolean onMarkerClick(Marker marker ) {
                    EditText destination = (EditText) findViewById(R.id.destination_text_search);
                    EditText source = (EditText) findViewById((R.id.editText));
                    String address = null;
                    dest = new LatLng(marker.getPosition().latitude , marker.getPosition().longitude);
                    try{
                        Geocoder geocoder = new Geocoder( getApplicationContext() , Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude ,
                                marker.getPosition().longitude , 1);

                        if(addresses.size() > 0){
                            Address add = addresses.get(0);
                            address = add.getAddressLine(0);
                            address += add.getAddressLine(1);

                        }
                    }catch (Exception e){

                    }

                    if (source.getText().equals("")) {
                        System.out.println(source.getText());
                        source.setText(address);


                        return true;
                    } else {
                        System.out.println("Printing destination in place of "+source.getText());
                        destination.setText(address);

                        return true;
                    }
                }
            });
            */

        }
        else{

            Log.i("ABCD" , "Location Null");

        }


    }

    private void updatePosition(){
        /*mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            mLatitude = Double.parseDouble(String.valueOf(mLastLocation.getLatitude()));
            mLongitude = Double.parseDouble(String.valueOf(mLastLocation.getLongitude()));
            position = new LatLng(mLatitude , mLongitude);
            *//*
            Log.i("Adding Marker", mLatitude + " " + mLongitude);

            distance = new double[10];



            for(int i = 0 ; i < 10 ; i++){
                distance[i] = (latitude[i] - position.latitude)*(latitude[i] - position.latitude) +
                        (longitude[i] - position.longitude)*(longitude[i] - position.longitude);
            }
            double minDistance = distance[0];


            for(int i = 1 ; i < 10 ; i++){
                if(distance[i] < minDistance){
                    minDistance = distance[i];
                    minDistanceIndex = i ;
                }
            }

            route = new LatLng[10];
            for(int i=0 ; i< 10 ; i++){
                route[i] = new LatLng(latitude[i] , longitude[i]);
            }
            for(int i = 0 ; i < 10 ; i++  ){
                mMap.addMarker(new MarkerOptions().position(route[i]));
            }

            String address = null;
            try{
                Geocoder geocoder = new Geocoder( getApplicationContext() , Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(route[minDistanceIndex].latitude ,
                        route[minDistanceIndex].longitude , 1);

                if(addresses.size() > 0){
                    Address add = addresses.get(0);
                    address = add.getAddressLine(0);
                    address += add.getAddressLine(1);

                }

            }catch (Exception e){

            }

            EditText sourceLocation = (EditText) findViewById(R.id.editText);

            sourceLocation.setText(address);

            *//*
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
            *//*
            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {


                @Override
                public boolean onMarkerClick(Marker marker ) {
                    EditText destination = (EditText) findViewById(R.id.destination_text_search);
                    EditText source = (EditText) findViewById((R.id.editText));
                    String address = null;
                    dest = new LatLng(marker.getPosition().latitude , marker.getPosition().longitude);
                    try{
                        Geocoder geocoder = new Geocoder( getApplicationContext() , Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude ,
                                marker.getPosition().longitude , 1);

                        if(addresses.size() > 0){
                            Address add = addresses.get(0);
                            address = add.getAddressLine(0);
                            address += add.getAddressLine(1);

                        }
                    }catch (Exception e){

                    }

                    if (source.getText().equals("")) {
                        System.out.println(source.getText());
                        source.setText(address);


                        return true;
                    } else {
                        System.out.println("Printing destination in place of "+source.getText());
                        destination.setText(address);

                        return true;
                    }
                }
            });
            *//*

        }
        else{

        }
*/
    }

    public void markerClickHandler(){

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        Log.i("ABCD" , "OnMapReady");

        updatePosition();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( (keyCode == KeyEvent.KEYCODE_BACK )){
            moveTaskToBack(true);
            return true;
        }
        else {

            return false;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void findHopprBuses(View view) {
       /* Intent busListIntent = new Intent(this , TicketActivity.class);
        busListIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        EditText destination = (EditText) findViewById(R.id.destination_text_search);


        busListIntent.putExtra("Location" , destination.getText().toString());
        startActivity(busListIntent);
        finish();*/

    }

    public void openDrawer(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void getProfileActivity(View view) {
        Intent profileActivity = new Intent(this , ProfileActivity.class);
        profileActivity.putExtra("phoneNumber" , phoneNumber);
        profileActivity.putExtra("firstName" , firstName);
        profileActivity.putExtra("lastName", lastName);
        profileActivity.putExtra("email" , email);
        startActivity(profileActivity);
    }


    public void getPaymentActivity(View view) {
        Intent paymentActivity = new Intent(this , PaymentActivity.class);
        paymentActivity.putExtra("userId" , id);
        paymentActivity.putExtra("walletId" , walletId);
        startActivity(paymentActivity);
    }

    public void getHistoryActivity(View view) {
        Intent historyActivity = new Intent(this , HistoryActivity.class);
        startActivity(historyActivity);
    }

    public void getHelpActivity(View view) {
        Intent helpActivity = new Intent(this , HelpActivity.class);
        startActivity(helpActivity);
    }

    public void getAboutActivity(View view) {
        Intent aboutActivity = new Intent(this , AboutActivity.class);
        startActivity(aboutActivity);
    }

    public void pushFragment(View view) throws JSONException {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mConfirmationFragment = new ConfirmationFragment();
        fragmentTransaction.replace(R.id.fragment_container, mConfirmationFragment);
        fragmentTransaction.addToBackStack("tag").commit();
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
       */ /*float[] result = new float[1];
        EditText textField = (EditText) findViewById(R.id.destination_text_search);
        EditText srctextField = (EditText)findViewById(R.id.editText);
        String message = textField.getText().toString().trim();
        if(TextUtils.isEmpty(message)){
            textField.requestFocus();
            return;
        }
        String srcmessage = srctextField.getText().toString().trim();
        if(TextUtils.isEmpty(srcmessage)){
            srctextField.requestFocus();
            return;
        }
        String[] messages = new String[2];
        messages[0] = srcmessage;
        messages[1] = message;
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("source" , messages[0]);
        jsonMessage.put("destination" , messages[1]);
        mSocket.emit("requestpickup" , jsonMessage);*/
        /*if(((EditText) findViewById(R.id.destination_text_search)).getText().equals("") ){
            Location.distanceBetween(route[minDistanceIndex].latitude, route[minDistanceIndex].longitude,
                    dest.latitude, dest.longitude, result);
            Bundle bundle = new Bundle();
            if(result[0] > 6000){

                bundle.putString("Price" , "40");

            }
            else {



                bundle.putString("Price" , "30");
            }

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mConfirmationFragment = new ConfirmationFragment();
            mConfirmationFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_container, mConfirmationFragment);
            fragmentTransaction.addToBackStack("tag").commit();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 400, null);
            priceText = (TextView) findViewById(R.id.price_container);

        } else {
            Toast.makeText(getApplicationContext() , "Enter the Destination" , Toast.LENGTH_SHORT).show();
        }
        */
    }

    public void destroyFragment(View view) {
        if(mConfirmationFragment != null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
            fragmentTransaction.remove(mConfirmationFragment).commit();

        }
        else {
            LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.confirmation_fragment);
            mLinearLayout.setVisibility(View.INVISIBLE);
        }
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 400, null);*/
    }

    public void pickupRequestMade(View view) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mview = inflater.inflate(R.layout.dialog_box_layout , null , false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(mview);
        dialog.show();

    }

    public void confirmClickListener(View view) throws JSONException {
        dialog.dismiss();
        pushFragment(view);
    }

    public void onCancelClickListener(View view) throws JSONException {
        dialog.dismiss();

    }

    public void cabIconClick(View view) {
        /*if(thirteenIconClick){
            chauffeurIconClick =false;
            thirteenIconClick = false;
            twentysixIconClick = false;
            //LinearLayout schedulingLinearLayout = (LinearLayout) findViewById(R.id.schedulingLinearView);
            //schedulingLinearLayout.setVisibility(View.INVISIBLE);
            //LinearLayout schedulingLinearLayout2 = (LinearLayout) findViewById(R.id.schedulingLinearView2);
            //schedulingLinearLayout2.setVisibility(View.INVISIBLE);
            ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
            imageView2.setVisibility(View.INVISIBLE);
            ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
            imageView3.setVisibility(View.INVISIBLE);
            ImageButton chauffeurImageView = (ImageButton) findViewById(R.id.imageButton2);
            chauffeurImageView.setImageResource(R.drawable.buslogo);
        }
        else {
            chauffeurIconClick = false;
            thirteenIconClick = true;
            twentysixIconClick = false;
            ImageButton chauffeurImage = (ImageButton) findViewById(R.id.imageButton2);
            chauffeurImage.setImageResource(R.drawable.driveractive);
            //LinearLayout schedulingLinearLayout = (LinearLayout) findViewById(R.id.schedulingLinearView);
            //schedulingLinearLayout.setVisibility(View.VISIBLE);
            ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
            imageView2.setVisibility(View.VISIBLE);
            ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
            imageView3.setVisibility(View.VISIBLE);

        }*/

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

        alertDialogBuilder.setTitle("Not Available");
        alertDialogBuilder.setMessage("This service is coming soon");
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    public void datePickerClick(View view) {
        DialogFragment newDatePickerFragment = new DatePickerFragment();
        newDatePickerFragment.show(getSupportFragmentManager() , "datePicker");

    }

    public void timePickerClick(View view) {
        DialogFragment newDialogFragment = new TimePickerFragment();
        newDialogFragment.show(getSupportFragmentManager() , "timePicker");
    }

    public void chauffeurImageClick(View view) {
        /*if(chauffeurIconClick){
            chauffeurIconClick =false;
            //LinearLayout schedulingLinearLayout = (LinearLayout) findViewById(R.id.schedulingLinearView);
            //schedulingLinearLayout.setVisibility(View.INVISIBLE);
            //LinearLayout schedulingLinearLayout2 = (LinearLayout) findViewById(R.id.schedulingLinearView2);
            //schedulingLinearLayout2.setVisibility(View.INVISIBLE);
            ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
            imageView2.setVisibility(View.INVISIBLE);
            ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
            imageView3.setVisibility(View.INVISIBLE);
            ImageButton chauffeurImageView = (ImageButton) findViewById(R.id.imageButton2);
            chauffeurImageView.setImageResource(R.drawable.buslogo);
        }
        else {
            chauffeurIconClick = true;
            ImageButton chauffeurImage = (ImageButton) findViewById(R.id.imageButton2);
            chauffeurImage.setImageResource(R.drawable.driveractive);
            //LinearLayout schedulingLinearLayout = (LinearLayout) findViewById(R.id.schedulingLinearView);
            //schedulingLinearLayout.setVisibility(View.VISIBLE);
            ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
            imageView2.setVisibility(View.VISIBLE);
            ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
            imageView3.setVisibility(View.VISIBLE);

        }
*/
    }

    public void allRouteLoader(View view) {
        Intent allRouteActivity = new Intent(this , AllRouteActivity.class);
        startActivity(allRouteActivity);
    }

    public void bookmarkSourceDestination(View view) {

        if(source.equals("Enter Source") || destination.equals("Enter Destination")){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this
                   );

            alertDialogBuilder.setTitle("Insufficient Information");
            alertDialogBuilder.setMessage("Please Enter Proper Source and Destination");
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

        else {
            new ExecuteBookmarkCall().execute();
        }

    }

    public void searchBySource(View view) {


        Intent confirmationIntent = new Intent(getApplicationContext() , ConfirmationActivity.class);
        if((source.equals("Enter Source") || destination.equals("Enter Destination"))|| (source.equals(destination))){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

            alertDialogBuilder.setTitle("Insufficient Information");
            alertDialogBuilder.setMessage("Please Enter Proper Source and Destination");
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        /*else {

            confirmationIntent.putExtra("source" , source);
            confirmationIntent.putExtra("destination" , destination);
            startActivity(confirmationIntent);

        }

*/      else {
            drawLoading();
            new GetAllRoutes().execute();
            }

        /*Button sourceButton = (Button) findViewById(R.id.source_find_button);
        sourceButton.setTextColor(Color.parseColor("#1aa79c"));
        Button routeButton = (Button) findViewById(R.id.route_search_button);
        routeButton.setTextColor(Color.parseColor("#000000"));
        Button nearbyButton = (Button) findViewById(R.id.nearby_search_button);
        nearbyButton.setTextColor(Color.parseColor("#000000"));


        FrameLayout mLinearLayout = (FrameLayout) findViewById(R.id.top_edit_text_container);
        mLinearLayout.setVisibility(View.VISIBLE);


        FrameLayout dLinearLayout = (FrameLayout) findViewById(R.id.second_edit_text_container);
        dLinearLayout.setVisibility(View.VISIBLE);

        AutoCompleteTextView kAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.destinationEditText);
        kAutoCompleteTextView.setHint("Enter Source");

        AutoCompleteTextView mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.routeEditText);
        mAutoCompleteTextView.setHint("Enter Destination");*/

    }

    private void drawLoading() {
        LinearLayout loader = (LinearLayout) findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
    }

    private void endLoading(){
        LinearLayout loader = (LinearLayout) findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);
    }

    public void searchByRoute(View view) {
/*
        Button sourceButton = (Button) findViewById(R.id.source_find_button);
        sourceButton.setTextColor(Color.parseColor("#000000"));
        Button routeButton = (Button) findViewById(R.id.route_search_button);
        routeButton.setTextColor(Color.parseColor("#1aa79c"));
        Button nearbyButton = (Button) findViewById(R.id.nearby_search_button);
        nearbyButton.setTextColor(Color.parseColor("#000000"));

        FrameLayout mLinearLayout = (FrameLayout) findViewById(R.id.top_edit_text_container);
        mLinearLayout.setVisibility(View.VISIBLE);



        FrameLayout dLinearLayout = (FrameLayout) findViewById(R.id.second_edit_text_container);
        dLinearLayout.setVisibility(View.INVISIBLE);

        AutoCompleteTextView mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.destinationEditText);
        mAutoCompleteTextView.setHint("Enter Bus Number");*/

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

        alertDialogBuilder.setTitle("Service Not Available");
        alertDialogBuilder.setMessage("This Service is not yet made available");
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void searchByNearby(View view) {
        /*Button sourceButton = (Button) findViewById(R.id.source_find_button);
        sourceButton.setTextColor(Color.parseColor("#000000"));
        Button routeButton = (Button) findViewById(R.id.route_search_button);
        routeButton.setTextColor(Color.parseColor("#000000"));
        Button nearbyButton = (Button) findViewById(R.id.nearby_search_button);
        nearbyButton.setTextColor(Color.parseColor("#1aa79c"));

        FrameLayout mLinearLayout = (FrameLayout) findViewById(R.id.top_edit_text_container);
        mLinearLayout.setVisibility(View.INVISIBLE);


        FrameLayout dLinearLayout = (FrameLayout) findViewById(R.id.second_edit_text_container);
        dLinearLayout.setVisibility(View.INVISIBLE);

        Intent allRouteActivityIntent = new Intent(getApplicationContext() , AllRouteActivity.class);

        startActivity(allRouteActivityIntent);*/

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);

        alertDialogBuilder.setTitle("Service Not Available");
        alertDialogBuilder.setMessage("This Service is not yet made available");
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void getRouteActivity(View view) {

        Intent intent = new Intent(getApplicationContext() , RouteActivity.class);
        startActivity(intent);

    }

    public void getBookmarkActivity(View view) {
        Intent intent = new Intent(getApplicationContext() , BookmarkActivity.class);
        intent.putExtra("id" , id);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        mapView.setCenter(new com.mapbox.mapboxsdk.geometry.LatLng(latitude , longitude));

    }

    private class GetAllRoutes extends AsyncTask<Void , Void , Integer> {

        JSONObject responseJSON;

        JSONArray dataJSON;

        String dutyId;

        JSONArray dutyStatus;

        int sourcePosition;

        int destinationPosition;

        int time;

        int seatsAvailable;

        String bus;

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){

                endLoading();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MapsActivity.this
                );

                alertDialogBuilder.setTitle("Buses Full");
                alertDialogBuilder.setMessage(Html.fromHtml("All buses from this route seems to be full"));
                alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            else if(integer == 2){
                endLoading();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MapsActivity.this
                );

                alertDialogBuilder.setTitle("Buses Full");
                alertDialogBuilder.setMessage(Html.fromHtml("All buses from this route seems to be full"));
                alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else if( integer == 3){
                endLoading();
                Intent confirmationIntent
                        = new Intent(getApplicationContext() , ConfirmationActivity.class);
                confirmationIntent.putExtra("source" , source);
                confirmationIntent.putExtra("destination" , destination);
                confirmationIntent.putExtra("busNumber" , bus);
                confirmationIntent.putExtra("destinationPosition" , destinationPosition);
                confirmationIntent.putExtra("sourcePosition" , sourcePosition);
                confirmationIntent.putExtra("dutyId" , dutyId);
                confirmationIntent.putExtra("userId" , id);
                confirmationIntent.putExtra("firstName" , firstName);
                confirmationIntent.putExtra("lastName" , lastName);
                confirmationIntent.putExtra("distance", 15.0);
                confirmationIntent.putExtra("startTime" , time);
                confirmationIntent.putExtra("seatsLeft" , seatsAvailable);
                confirmationIntent.putExtra("walletId" , walletId);
                startActivity(confirmationIntent);
            }
            else if(integer == 4){

                endLoading();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MapsActivity.this
                );

                alertDialogBuilder.setTitle("Buses Full");
                alertDialogBuilder.setMessage(Html.fromHtml("All buses from this route seems to be full"));
                alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0;

            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/FindRoutes");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("sourceStop" , source));
            nameValuePairs.add(new BasicNameValuePair("destinationStop" , destination));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for(String line = null ; (line = reader.readLine()) != null ;){
                    builder.append(line).append("\n");
                }
                responseJSON = new JSONObject(builder.toString());

                Log.i("ABCD" , responseJSON.toString());

                //String route = responseJSON.getJSONArray("data").getJSONArray(0).getJSONObject(0).getString("routeNumber");

                if(responseJSON.getJSONArray("data").length() == 0){
                    value = 4; //2 represents there is no one serving the duty
                }

                else if(responseJSON.getJSONArray("data").getJSONArray(0).length() == 0){
                    value = 2;
                }

                else if(responseJSON.getJSONArray("data").getJSONArray(0).getJSONObject(0).getString("busAssigned").equals("")){
                    value = 1; //1 corresponds to not Getting a bus
                }

                else {
                    bus = responseJSON.getJSONArray("data").getJSONArray(0).getJSONObject(0).getString("busAssigned");

                    dutyId = responseJSON.getJSONArray("data").getJSONArray(0).getJSONObject(0).getString("dutyId");
                    value = 3;//3 means everything is ok

                    time = responseJSON.getJSONArray("data").getJSONArray(0).getJSONObject(0).getInt("startTime");

                    dutyStatus = responseJSON.getJSONArray("data").getJSONArray(0).getJSONObject(0).getJSONArray(
                            "dutyStatus");

                    seatsAvailable = responseJSON.getJSONArray("data").getJSONArray(0).getJSONObject(0).getInt("seatsLeft");

                    for(int i = 0 ; i < dutyStatus.length() ; i++){
                        if(source.equals(dutyStatus.getJSONObject(i).getString("name"))){
                            sourcePosition = dutyStatus.getJSONObject(i).getInt("position");

                        }
                    }
                    for(int i = 0 ; i < dutyStatus.length() ; i++){
                        if(destination.equals(dutyStatus.getJSONObject(i).getString("name"))){
                            destinationPosition = dutyStatus.getJSONObject(i).getInt("position");

                        }
                    }

                }




                /*if(bus.equals("")){
                    value = 1; //1 corresponds to not Getting a bus

                    Log.i("ABCD" , bus);
                }*/
            } catch (JSONException e) {
                value = 1;
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                Log.i("ABCD" , e.toString());
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return value;
        }
    }

    private class ExecuteBookmarkCall extends AsyncTask<Void , Void , Integer> {

        JSONObject responseJSON;

        JSONArray favoritesArray;

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 1){
                Toast.makeText(getApplicationContext() , "Successfully Bookmarked" , Toast.LENGTH_SHORT).show();
            }
            else if(integer == -1){
                Toast.makeText(getApplicationContext() , "Bookmark Unsuccessful" , Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(getApplicationContext() , "Unrecognized Error" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int value = 0;

            HttpClient httpClient = new DefaultHttpClient();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            httpClient = new DefaultHttpClient(httpParams);

            HttpPost httpPost = new HttpPost((new Constants()).getServerApiAddress() + "api/v1/SaveRoute");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("sourceHop" , source));
            nameValuePairs.add(new BasicNameValuePair("destinationHop" , destination));
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


                Boolean success = responseJSON.getBoolean("success");



                if (success){
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
