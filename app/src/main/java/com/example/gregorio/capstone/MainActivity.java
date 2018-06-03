package com.example.gregorio.capstone;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

      MapFragment mapFragment = new MapFragment();
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment)
          .commit();

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

//    // Check if the Google Play Services are available or not
//    GoogleMapsApi googleMapsApi = new GoogleMapsApi();
//    googleMapsApi.CheckGooglePlayServices(this, this);
//    googleMapsApi.GoogleApiClient(this);

  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    Fragment fragment = null;
    Class fragmentClass = null;
    if (id == R.id.nav_map) {
      fragmentClass = MapFragment.class;
    } else if (id == R.id.nav_food) {
      fragmentClass = FoodFragment.class;
    } else if (id == R.id.nav_bars) {
      fragmentClass = BarsFragment.class;
    } else if (id == R.id.nav_clubs) {
      fragmentClass = ClubsFragment.class;
    } else if (id == R.id.nav_clubs) {
      fragmentClass = ClubsFragment.class;
    } else if (id == R.id.nav_favourites) {
      fragmentClass = FavouritesFragment.class;
    } else if (id == R.id.nav_sights) {
      fragmentClass = SightsFragment.class;
    } else if (id == R.id.nav_send) {

    }
    try {
      fragment = (Fragment) fragmentClass.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

}
