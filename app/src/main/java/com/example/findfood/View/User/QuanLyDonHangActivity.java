package com.example.findfood.View.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findfood.DangNhapActivity;
import com.example.findfood.GiaoDichActivity;
import com.example.findfood.R;
import com.example.findfood.ThanhToanActivity;
import com.example.findfood.View.CartActivity;
import com.example.findfood.View.FavoriteActivity;
import com.example.findfood.View.HistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class QuanLyDonHangActivity extends AppCompatActivity {

    public static ImageView backqldh;
    TextView txtTrangThaiDonHang,txtDonHang,txtGioHang, txtHistory, txtQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_don_hang);

        backqldh = findViewById(R.id.backqldh);
        txtQRCode = findViewById(R.id.txtQRCode);
        txtDonHang = findViewById(R.id.txtDonHang);
        txtHistory = findViewById(R.id.txtHistory);
        txtGioHang = findViewById(R.id.txtGioHang);
        txtTrangThaiDonHang = findViewById(R.id.txtTrangThaiDonHang);


        backqldh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TrangCaNhan.class);
                startActivity(intent);
            }
        });

        txtQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    startActivity(new Intent(getApplicationContext(),testQRCode.class));
                } else {
                    // No user is signed in
                    startActivity(new Intent(getApplicationContext(),DangNhapActivity.class));
                }
            }
        });

        txtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),GiaoDichActivity.class));
            }
        });

        txtTrangThaiDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                } else {
                    // No user is signed in
                    startActivity(new Intent(getApplicationContext(),DangNhapActivity.class));
                }
            }
        });
        txtDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iDonHang = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(iDonHang);
            }
        });
        txtGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGioHang = new Intent(getApplicationContext(), ThanhToanActivity.class);
                startActivity(iGioHang);
            }
        });
    }
}