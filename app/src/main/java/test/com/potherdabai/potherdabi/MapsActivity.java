package test.com.potherdabai.potherdabi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.com.potherdabai.potherdabi.Model.MyPlaces;
import test.com.potherdabai.potherdabi.Model.Results;
import test.com.potherdabai.potherdabi.Remote.IGoogleApiService;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
        {

    private GoogleMap mMap;
    //play services
    private GoogleApiClient mgoogleAiClient;
    private double latitude, longitude;
    private Location lastLocation;
    private Marker mMarker;

    private static final int MY_PERMISSION_REQUEST_CODE = 1000;
    private static final int PLAY_SERVICES_RES_REQUEST = 7001;
    private LocationRequest mlocationRequest;

    IGoogleApiService iGoogleApiService;
    MyPlaces curretnPlace;
//new Location

//    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
   private LocationRequest locationRequest;


    // private Location lastLocation;


    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;


    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //init services
        iGoogleApiService = Common.getGoogleApiService();
        //reqest Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermisson();
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.hospital:
                        nearByPlace("hospital");
                        break;
                    case R.id.restaurant:
                        nearByPlace("restaurant");
                        break;
                    case R.id.school:
                        nearByPlace("school");
                        break;
                    case R.id.market:
                        nearByPlace("market");
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        //init location
//        buildLocationCallback();
//        buildLocationRequest();



    }

//    private void buildLocationCallback() {
//        locationCallback = new LocationCallback(){
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                lastLocation = locationResult.getLastLocation();
//
//                if(mMarker!=null){
//                    mMarker.remove();
//                }
//                latitude = lastLocation.getLatitude();
//                longitude = lastLocation.getLongitude();
//
//                LatLng latLng = new LatLng(latitude,longitude);
//                MarkerOptions markerOptions = new MarkerOptions()
//                        .position(latLng)
//                        .title("Your Position")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                mMarker = mMap.addMarker(markerOptions);
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                //mMap.addMarker(new MarkerOptions().position(latLng));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
//            }
//        };
//    }

//    private void buildLocationRequest() {
//        mlocationRequest = new LocationRequest();
//        mlocationRequest.setInterval(1000);
//        mlocationRequest.setFastestInterval(1000);
//        mlocationRequest.setSmallestDisplacement(10f);
//        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }

    private void nearByPlace(final String placeType) {
        mMap.clear();
        String url = getUrl(latitude,longitude,placeType);
        iGoogleApiService .getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        curretnPlace = response.body();
                        if(response.isSuccessful()){
                            for (int i=0; i<response.body().getResults().length;i++){
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lan = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat,lan);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                if(placeType=="hospital"){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }
                                else if(placeType=="market"){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }

                                else if(placeType=="restaurant"){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }
                                else if(placeType=="school"){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.
                                            defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                markerOptions.snippet(String.valueOf(i));//assign index for marker
                                mMap.addMarker(markerOptions);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });

    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+ latitude +","+ longitude);
        googlePlaceUrl.append("&radius="+10000);
        googlePlaceUrl.append("&type="+placeType);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+getResources().getString(R.string.browse_key));
        Log.d("geturl",googlePlaceUrl.toString());
        return googlePlaceUrl.toString();

    }

    private boolean checkLocationPermisson() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_REQUEST_CODE);
            }

            else {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },MY_PERMISSION_REQUEST_CODE);
            }
            return false;
        }

        else
            return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        buildGoogleApiClient();
//        mMap.setMyLocationEnabled(true);
//    }
//    protected synchronized void buildGoogleApiClient(){
//        mgoogleAiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        mgoogleAiClient.connect();

        //init google play services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }

        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        //make event click on marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //when user select marker , just get result for place and assign to static variable
                Common.currentResult = curretnPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                //start new activity
                startActivity(new Intent(MapsActivity.this,ViewPlace.class));
                return true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if (mgoogleAiClient==null)
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);

                    }
                }
                else
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();


            }break;
        }
    }

            private synchronized void buildGoogleApiClient() {

        mgoogleAiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mgoogleAiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setSmallestDisplacement(10f);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleAiClient,mlocationRequest,this);
        }

        }


            @Override
    public void onConnectionSuspended(int i) {

        mgoogleAiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation =location;
        if(mMarker!=null){
            mMarker.remove();
        }
        latitude = lastLocation.getLatitude();
        longitude = lastLocation.getLongitude();

        LatLng latLng = new LatLng(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Your Position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));


        if (mgoogleAiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleAiClient,this);
        }

    }


}
