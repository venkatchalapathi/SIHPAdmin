package com.example.sihpadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng latLng;
    GPSTracker gps;
    double cur_lattitude, cur_longitude;

    //=======================================
    String current_user;
    ArrayList<Admin> admins;
    LatLng latLang;

    ArrayList<ComPojo> tempList;
    FirebaseAuth auth;
    double lattutude;
    double longitude;
    //========================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maps);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(this, "" + currentFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
        current_user = currentFirebaseUser.getEmail();
        gps = new GPSTracker(this);
        if (gps.canGetLocation) {
            cur_lattitude = gps.getLatitude();
            cur_longitude = gps.getLongitude();
        }
         lattutude = getIntent().getDoubleExtra("lat", 0);
        longitude= getIntent().getDoubleExtra("lon", 0);

        latLng = new LatLng(lattutude,longitude);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    public void getData(){
        final ArrayList<Admin> list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SIHP/Admins");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String lat = ds.child("Lat").getValue(String.class);
                    String lon = ds.child("Longi").getValue(String.class);
                    double lattitude = Double.parseDouble(lat);
                    double longitude = Double.parseDouble(lon);
                    String email = ds.child("email").getValue(String.class);
                    String emp_id = ds.child("emp_id").getValue(String.class);
                    String mobile = ds.child("mobile").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);

                    Admin admin = new Admin();
                    admin.setLat(lattitude);
                    admin.setLongi(longitude);
                    admin.setEmail(email);
                    admin.setEmp_id(emp_id);
                    admin.setMobile(mobile);
                    admin.setName(name);

                    list.add(admin);
                }
                sendData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference("SIHP/Problems");

        final ArrayList<ComPojo> data = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ComPojo comPojo = ds.getValue(ComPojo.class);
                    data.add(comPojo);
                }
                checkProblems(data);
                Log.i("location problems:", "size " + data.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Log.i("maps",""+latLng);
    }
    private void checkProblems(ArrayList<ComPojo> data) {
        tempList = new ArrayList<>();

        LatLng center = new LatLng(lattutude,longitude);
        Log.i("center",""+center);
        for (int i=0;i<data.size();i++){
            LatLng dest = new LatLng(data.get(i).getLattitude(),data.get(i).getLongitude());
            if (isMarkerOutsideCircle(center,dest,20000)){
                Log.i("problems:","Not Matched ");
            }else {

                String problem = data.get(i).getProblem();
                double lati = data.get(i).getLattitude();
                double loni = data.get(i).getLongitude();
                ComPojo pojo = new ComPojo(problem,lati,loni);
                tempList.add(pojo);
                Log.i("problems:","Matched ");
            }
            Log.i("problems:","size "+tempList.size());
        }
        showProblems(tempList);
    }

    private void showProblems(ArrayList<ComPojo> list) {
        if (list.size() > 0) {
            //ArrayList<ComPojo> markersArray = new ArrayList<ComPojo>();

            for (int i = 0; i < list.size(); i++) {

                Log.i("act:", "la" + list.get(i).lattitude);
                Log.i("act:", "lo" + list.get(i).longitude);

                String problem = list.get(i).getProblem();
                Log.i("act:",problem);
                drawProblemsCircle(new LatLng(list.get(i).getLattitude(), list.get(i).getLongitude()),problem);
            }

        }
    }

    private void drawProblemsCircle(LatLng latLng, String problem) {
        int circle_size =10;
        int icon = R.drawable.arrow;
        int circle_color = getResources().getColor(R.color.colorPrimaryDark);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        switch (problem){
            case "Lonely Area":
                circle_size = 20;
                icon = R.drawable.lonely;
                circle_color = getResources().getColor(R.color.colorPrimary);
                break;
            case "Potholes":
                circle_size = 5;
                icon = R.drawable.potholes;
                circle_color = getResources().getColor(R.color.colorAccent);
                break;
            case "Spead Breakers":
                circle_size = 7;
                icon = R.drawable.arrow;
                circle_color = getResources().getColor(R.color.colorPrimaryDark);
                break;
            case "Danger":
                circle_size = 25;
                icon = R.drawable.danger;
                break;
        }
        circleOptions.radius(circle_size);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(circle_color);
        circleOptions.strokeWidth(2);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(problem)
                .icon(BitmapDescriptorFactory.fromResource(icon)));
        mMap.addCircle(circleOptions);
        Log.i("act:","circle added");
    }

    private void sendData(ArrayList<Admin> list) {
        admins = list;
        Log.i("logs:"," size "+list.size());
        for (int i=0;i<list.size();i++){
            Log.i("logs:",""+list.get(i).getEmail());
            if (current_user.equals(list.get(i).getEmail().trim())){

                double lat = list.get(i).getLat();
                double longi =list.get(i).getLongi();
                String name = list.get(i).getName();
                //latLang = new LatLng(lat,longi);

                Log.i("matched",lat+":"+longi+"   "+name);
                lattutude = lat;
                longitude = longi;
                latLng = new LatLng(lattutude,longitude);
                drawCircle(latLng);
                Log.i("get:",""+latLng);
                break;
            }
        }

    }
    private boolean isMarkerOutsideCircle(LatLng centerLatLng, LatLng draggedLatLng, double radius) {
        float[] distances = new float[1];
        Location.distanceBetween(centerLatLng.latitude,
                centerLatLng.longitude,
                draggedLatLng.latitude,
                draggedLatLng.longitude, distances);
        return radius < distances[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void drawCircle(LatLng latLng) {
        int circle_size = 20000;
        int circle_color = getResources().getColor(R.color.circle);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);

        circleOptions.radius(circle_size);
        circleOptions.strokeColor(Color.RED);
        //circleOptions.fillColor(circle_color);
        circleOptions.strokeWidth(5);
        mMap.addCircle(circleOptions);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.current_location_id:
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                updatePointer(latitude, longitude);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePointer(double latitude, double longitude) {
        mMap.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(sydney, 10);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
        mMap.animateCamera(cameraUpdateFactory);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getData();
        mMap = googleMap;
        latLng = new LatLng(lattutude,longitude);
        Log.i("get:1",""+latLng);
        // Add a marker in Sydney and move the camera
        updatePointer(cur_lattitude, cur_longitude);

       // drawCircle(latLng);
    }
}
