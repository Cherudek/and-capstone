package com.example.gregorio.capstone;

import static com.google.android.gms.location.places.Places.getPlaceDetectionClient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import pojos.Example;
import retrofit.RetrofitMaps;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapFragment extends Fragment {

  public static final String LOG_TAG = MapFragment.class.getSimpleName();
  private GoogleMap mMap;
  private MapView mapView;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
  private final int MY_LOCATION_REQUEST_CODE = 101;
  private static final int GOOGLE_API_CLIENT_ID = 0;
  private GoogleApiClient mGoogleApiClient;
  private PlaceDetectionClient mPlaceDetectionClient;
  // The entry point to the Fused Location Provider API to get location in Android.
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private OnMarkerClickListener onMarkerClickListener;
  // A default location (London, Uk) and default zoom to use when location permission is
  // not granted.
  private final LatLng mDefaultLocation = new LatLng(51.508530, -0.076132);
  private final LatLng mNBH = new LatLng(51.5189618, -0.1450063);
  private static final int DEFAULT_ZOOM = 15;
  private Double latitude;
  private Double longitude;
  private String apiKey;
  private SearchView searchEditText;
  private Task<Location> location;

  public MapFragment(){
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    TextView textView = rootView.findViewById(R.id.maptv);
    textView.setText("MAP FRAGMENT");
    textView.setVisibility(View.INVISIBLE);
    searchEditText = rootView.findViewById(R.id.editText);
    apiKey = getString(com.example.gregorio.capstone.R.string.google_maps_key);
    mapView = rootView.findViewById(R.id.map);
    mapView.onCreate(savedInstanceState);
    mapView.onResume(); // needed to get the map to display immediately

    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
    } catch (Exception e) {
      e.printStackTrace();
    }

    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // Check Permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
          // No explanation needed; request the permission
          ActivityCompat.requestPermissions(getActivity(),
              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              MY_LOCATION_REQUEST_CODE);
          // MY_LOCATION_REQUEST_CODE is an
          // app-defined int constant. The callback method gets the
          // result of the request.
        } else {
          // If the Location Permission id Granted Enable Location on the Map
          mMap.setMyLocationEnabled(true);
          mMap.setOnMarkerClickListener(onMarkerClickListener);
          // Set Up Markers on the Map
          if(mMap!=null) {
            // Tower of London Marker
            Marker towerOfLondon = mMap.addMarker(new MarkerOptions().position(mDefaultLocation)
                .title("Tower Of London"));
            // New Broadcasting House Marker
            Marker NewBH = mMap.addMarker(new MarkerOptions()
                .position(mNBH)
                .title("New Broadcasting House")
                .snippet("New BH is cool")
                .icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.bbc_marker)));
          }
        }
      }
    });

    // Construct a PlaceDetectionClient.
    mPlaceDetectionClient = getPlaceDetectionClient(getContext());
    // Construct a FusedLocationProviderClient.
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    // Set up the API client for Places API
    mGoogleApiClient = new GoogleApiClient.Builder(getContext())
        .addApi(Places.GEO_DATA_API)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();

    location = LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation();
    Location location2 = location.getResult();
    latitude = location2.getLatitude();
    longitude = location2.getLongitude();

    // @OnMarkerClickListener added to the map
    onMarkerClickListener = new OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        Toast toast = Toast
            .makeText(getContext(), "You Clicked on " + title, Toast.LENGTH_SHORT);
        toast.show();
        return false;
      }
    };

    searchEditText.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        buildRetrofitAndGetResponse(searchEditText.getQuery().toString());
        Log.i(LOG_TAG, "The Search Query is: " + searchEditText.getQuery().toString());
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        Log.i(LOG_TAG, "New Query");
        return false;
      }
    });

    return rootView;
  }

  private void buildRetrofitAndGetResponse(String type) {

    String url = "https://maps.googleapis.com/maps/";

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    RetrofitMaps service = retrofit.create(RetrofitMaps.class);

    String locationString = location.toString();
    Log.i(LOG_TAG, "Your Current Location is: " + locationString);

    Call<Example> call = service
        .getNearbyPlaces(type, latitude + "," + longitude, DEFAULT_ZOOM, apiKey);

    call.enqueue(new Callback<Example>() {
      @Override
      public void onResponse(Call<Example> call, Response<Example> response) {
        try {
          mMap.clear();
          // This loop will go through all the results and add marker on each location.
          for (int i = 0; i < response.body().getResults().size(); i++) {
            Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
            Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
            String placeName = response.body().getResults().get(i).getName();
            String vicinity = response.body().getResults().get(i).getVicinity();
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            // Position of Marker on Map
            markerOptions.position(latLng);
            // Adding Title to the Marker
            markerOptions.title(placeName + " : " + vicinity);
            // Adding Marker to the Camera.
            Marker m = mMap.addMarker(markerOptions);
            // Adding colour to the marker
            markerOptions
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            // move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
          }
        } catch (Exception e) {
          Log.d(LOG_TAG, "Retrofit: There is an error Fetching Your Query ");
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(Call<Example> call, Throwable t) {
        Log.d("nFailure", t.toString());

      }
    });
  }

}
