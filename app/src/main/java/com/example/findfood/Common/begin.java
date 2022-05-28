package com.example.findfood.Common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SharedMemory;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findfood.Common.intro;
import com.example.findfood.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class begin extends AppCompatActivity {

    private static int SPLASH_TIME = 3000;

    ImageView backgroud_image;
    TextView taoBoi, txtVersion;

    Animation slideanim, bottomanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        backgroud_image = findViewById(R.id.backgroud_image);
        taoBoi = findViewById(R.id.taoBoi);
        txtVersion = findViewById(R.id.txtVersion);

        slideanim = AnimationUtils.loadAnimation(this,R.anim.slide_anim);
        bottomanim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        backgroud_image.setAnimation(slideanim);
        taoBoi.setAnimation(bottomanim);
        txtVersion.setAnimation(bottomanim);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            txtVersion.setText( getString(R.string.phienban) + " " +packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(begin.this, intro.class);
                startActivity(intent);
                Log.d("thien","da chạy begin");
                finish();
            }
        },SPLASH_TIME);
    }
}