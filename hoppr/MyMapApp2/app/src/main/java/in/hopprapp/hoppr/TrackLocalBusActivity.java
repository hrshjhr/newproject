package in.hopprapp.hoppr;

import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TrackLocalBusActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnMapReadyCallback {
    GoogleApiClient mGoogleApiClient;
    double mLatitude;
    double mLongitude;

    List<Double> busLatitude = new ArrayList<Double>();
    List<Double> busLongitude = new ArrayList<Double>();

    List<String> imeiNumber = new ArrayList<String>();


    Location mLastLocation;
    DriverFragment mDriverFragment;
    ConfirmationFragment mConfirmationFragment;
    LatLng position;
    LocationRequest mLocationRequest;
    private DrawerLayout mDrawerLayout;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private String url = "http://caravanconnect-50862.onmodulus.net";

    private Socket mSocket;

    List<Marker> mMarkers = new ArrayList<Marker>();



    private Emitter.Listener onConnectionError = new Emitter.Listener(){

        @Override
        public void call(Object... args) {

            TrackLocalBusActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*Toast.makeText(getApplicationContext() , "Error Connecting to Network" ,
                            Toast.LENGTH_SHORT).show();*/
                }
            });

        }
    };

    private Emitter.Listener onGpsPositionReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            TrackLocalBusActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject makerPosition = (JSONObject) args[0];
                    try {

                        Double latitude = makerPosition.getDouble("locationLatitude");
                        Double longitude = makerPosition.getDouble("locationLongitude");
                        String thisBusImeiNumber = makerPosition.getString("IMEINumber");

                        int index = findPositionOfBus(thisBusImeiNumber);

                        Double busActualLatitude;
                        Double busActualLongitude;

                        if(index == -1){
                            busLatitude.add(latitude);
                            busLongitude.add(longitude);

                            busActualLatitude = latitude;
                            busActualLongitude = longitude;

                            imeiNumber.add(thisBusImeiNumber);

                            mMarkers.add(mMap.addMarker(new MarkerOptions().
                                            position(new LatLng(busActualLatitude , busActualLongitude)).
                                            title("Bus Here").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker_3))

                            ));

                            Log.i("AAG" , "Adding Marker");

                        }

                        else {
                            busActualLatitude = latitude;
                            busActualLongitude = longitude;

                            busLatitude.set(index, busActualLatitude) ;
                            busLongitude.set(index, busActualLongitude);

                            Marker thisBusMarker = mMarkers.get(index);

                            thisBusMarker.setPosition(new LatLng(busActualLatitude , busActualLongitude));

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
                        Log.i("abcd" , "JSON Parsing Exception");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.track_local_bus_activity);

        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectionError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionError);
        mSocket.on("receiveGpsData", onGpsPositionReceived);
        mSocket.connect();
        JSONObject newJSONObject = new JSONObject();
        try {
            newJSONObject.put("routeNumber" , "AC4A");
            mSocket.emit("socketRoomInitialization" , newJSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        buildGoogleApiClient();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectionError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectionError);
        /*mSocket.off("PositionUpdate", onPositionUpdate);
        mSocket.off("SendBookingRequest", onBookingRequestSend);*/
    }



    protected synchronized void buildGoogleApiClient(){

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    protected void onStop(){

        super.onStop();

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }





    @Override
    protected void onStart(){

        super.onStart();

        mGoogleApiClient.connect();

    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("abcd", res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
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
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
       /* mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            mLatitude = Integer.parseInt(String.valueOf(mLastLocation.getLatitude()));

            mLongitude = Integer.parseInt(String.valueOf(mLastLocation.getLongitude()));

        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("Marker"));*/
    }



    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
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
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
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
            Log.i("Marker", "No Position");
        }


    }

    public void markerClickHandler(){

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Location Device" , "Connection Suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Location Service", "Connection failed : ConnectionReult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }


    public void goBackToListView(View view) {
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
    }
}





