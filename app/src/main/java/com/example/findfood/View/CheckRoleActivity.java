package com.example.findfood.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findfood.CallBack.UserCallBack;
import com.example.findfood.Databases.DatabaseUser;
import com.example.findfood.MainActivity;
import com.example.findfood.R;
import com.example.findfood.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class CheckRoleActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseUser databaseUser;
    com.airbnb.lottie.LottieAnimationView warning;
    TextView banned;
    ImageView logo;
    String trangThai;
    String check = "active";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_role);
        warning = findViewById(R.id.animation_view);
        banned = findViewById(R.id.tv_banned);
        logo = findViewById(R.id.image_owl);
        mAuth = FirebaseAuth.getInstance();

        checkPermissionApp();
    }

    public void checkTrangthai() {
        String idUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseUser = new DatabaseUser(getApplicationContext());
        databaseUser.getAll(new UserCallBack() {
            @Override
            public void onSuccess(ArrayList<User> lists) {
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getToken() != null && lists.get(i).getToken().equalsIgnoreCase(idUser)) {
                        trangThai = lists.get(i).getTrangThai();
                    }
                }
                if (trangThai.equalsIgnoreCase(check)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    Log.d("thien","da chạy checkRole");
                } else {
                    logo.setVisibility(View.GONE);
                    warning.setVisibility(View.VISIBLE);
                    banned.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void checkPermissionApp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            checkTrangthai();
        } else {
            String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            requestPermissions(permission, 50);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 50) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkTrangthai();
            } else {
                checkTrangthai();
            }
        }
    }

}