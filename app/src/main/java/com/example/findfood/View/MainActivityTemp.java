package com.example.findfood.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.example.findfood.Temp.AdapterTemp.CategoryAdapterTemp;
import com.example.findfood.DangNhapActivity;
import com.example.findfood.Databases.DatabaseFood;
import com.example.findfood.HelperClasses.FoodAdapter;
import com.example.findfood.R;
import com.example.findfood.model.Food;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivityTemp extends AppCompatActivity implements LocationListener {

    SliderView sliderView;
    RecyclerView rcvhome, rcvmonan;
    TextView txtslogan, txtDiachi;
    TextView edtsearch;
    CircleImageView anhdaidien;

    LocationManager locationManager;
    String country, locality, state, noi;

    CategoryAdapterTemp categoryAdapterTemp;
    FoodAdapter foodAdapter;
    ArrayList<Food> foodArrayList;
    DatabaseFood databaseFood;
    String kinhDo = "";
    String viDo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);

        sliderView = findViewById(R.id.imageSlider);
        rcvhome = findViewById(R.id.rcvhome);
        rcvmonan = findViewById(R.id.rcvmonan);
        txtslogan = findViewById(R.id.txtslogan);
        txtDiachi = findViewById(R.id.txtdiachi);
        edtsearch = findViewById(R.id.edtsearch);
        anhdaidien = findViewById(R.id.anhdaidien);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();
        LoadAvatar();

        anhdaidien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DangNhapActivity.class));
            }
        });

        edtsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SearchActivity.class));
            }
        });

    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    public void LoadAvatar() {
        Picasso.get().load("https://vnn-imgs-a1.vgcloud.vn/image1.ictnews.vn/_Files/2020/03/17/trend-avatar-1.jpg").into(anhdaidien);
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            country = addresses.get(0).getCountryName();
            locality = addresses.get(0).getAddressLine(0);
//            locality =addresses.get(0).getFeatureName();
            state = addresses.get(0).getAdminArea();
            noi = locality;
            txtDiachi.setText(noi);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}