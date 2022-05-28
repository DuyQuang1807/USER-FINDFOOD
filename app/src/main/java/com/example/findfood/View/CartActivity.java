package com.example.findfood.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.findfood.CallBack.FoodCallBack;
import com.example.findfood.Databases.DatabaseFood;
import com.example.findfood.Databases.DatabaseHDCT;
import com.example.findfood.Databases.DatabaseStore;
import com.example.findfood.Databases.DatabaseUser;
import com.example.findfood.HelperClasses.CartAdapter;
import com.example.findfood.R;
import com.example.findfood.ThanhToanActivity;
import com.example.findfood.View.User.QuanLyDonHangActivity;
import com.example.findfood.View.User.TrangCaNhan;
import com.example.findfood.local.LocalStorage;
import com.example.findfood.model.Food;
import com.example.findfood.model.HDCT;
import com.example.findfood.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    RecyclerView rcvcart;
    CartAdapter cartAdapter = null;
    ArrayList<HDCT> hdctArrayList;
    ArrayList<Order> test;
    LocalStorage localStorage;
    Gson gson;
    TextView titletoolbar, txtaddress, txttientong, noti;
    ImageView back;
    LinearLayout linearbackground;
    DatabaseHDCT databaseHDCT;
    DatabaseUser databaseUser;
    DatabaseStore databaseStore;
    double tongtien = 0;
    LinearLayout linearLayout;
    ScrollView scrollView;
    ArrayList<Order> orderArrayList;
    Button btnDatHang;
    String idFood = "";
    String idFoodDatHang = "";
    int soLuongMonDat = 0;
    int soLuongMonDaDatMoi = 0;
    int soLuongDauTien = 0;
    String idCuaFood = "";
    String tenCuaFood = "";
    double giaCuaFood = 0;
    String anhCuaFood = "";
    String diaChiCuaFood = "";
    String moTaCuaFood = "";
    String statusCuaFood = "";
    String maTheLoaiCuaFood = "";
    String idCuaHangCuaFood = "";
    String idDanhMucCuaFood = "";
    String idSanPhamCuaFood = "";
    double khuyenMaiCuaFood = 0;
    String trangThaiCuaFood = "";
    String tokenStoreCuaFood = "";
    int soLuongConLai = 0;
    int soLuongCuaFood = 0;
    String dung = "dung";
    String dungSauKhiUpdate = "dung";
    String chay = "chay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        back = findViewById(R.id.back);
        rcvcart = findViewById(R.id.rcvcart);
        noti = findViewById(R.id.noti);
        btnDatHang = findViewById(R.id.btnDatHang);

        localStorage = new LocalStorage(getApplicationContext());

        linearbackground = findViewById(R.id.linearbackground);
        noti.setVisibility(View.GONE);

        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###,###");
        gson = new Gson();
        hdctArrayList = new ArrayList<>();
        orderArrayList = new ArrayList<>();
        orderArrayList = getCartList();
        if (orderArrayList.size() != 0) {
            cartAdapter = new CartAdapter(orderArrayList, getApplicationContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            rcvcart.setLayoutManager(linearLayoutManager);
            rcvcart.setAdapter(cartAdapter);

        } else {
            linearbackground.setBackgroundResource(R.drawable.empty_cart);
            noti.setVisibility(View.VISIBLE);
            btnDatHang.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QuanLyDonHangActivity.class);
                startActivity(i);
            }
        });
        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseFood databaseFood = new DatabaseFood(getApplicationContext());
                databaseFood.getAllSoLuongMon(new FoodCallBack() {
                    @Override
                    public void onSuccess(ArrayList<Food> lists) {
                        for (int j = 0; j < orderArrayList.size(); j++) {
                            Order order = orderArrayList.get(j);
                            idFood = order.getFood().getIdfood();
                            soLuongMonDat = order.getSoluongmua();
                            Log.d("thien idFood tren", idFood);
                            Log.d("thien soLuongMonDat tren", soLuongMonDat+"");
                            for (int i = 0; i < lists.size(); i++) {
                                if (lists.get(i).getIdfood().equalsIgnoreCase(idFood)) {
                                    soLuongCuaFood = lists.get(i).getSoLuong();
                                    idCuaFood = lists.get(i).getIdfood();
                                    tenCuaFood = lists.get(i).getTenSanPham();
                                    giaCuaFood = lists.get(i).getGiaTien();
                                    anhCuaFood = lists.get(i).getAnh();
                                    diaChiCuaFood = lists.get(i).getDiaChi();
                                    moTaCuaFood = lists.get(i).getMota();
                                    statusCuaFood = lists.get(i).getStatus();
                                    maTheLoaiCuaFood = lists.get(i).getMatheloai();
                                    idCuaHangCuaFood = lists.get(i).getIdCuaHang();
                                    idDanhMucCuaFood = lists.get(i).getIdDanhMuc();
                                    idSanPhamCuaFood = lists.get(i).getIdSanPham();
                                    khuyenMaiCuaFood = lists.get(i).getKhuyenMai();
                                    trangThaiCuaFood = lists.get(i).getTrangThai();
                                    tokenStoreCuaFood = lists.get(i).getTokenstore();
                                    soLuongConLai = soLuongCuaFood - soLuongMonDat;

                                    Log.d("thien idFood duoi", idCuaFood);
                                    Log.d("thien soLuongMonDat duoi", soLuongConLai+"");
                                    Log.d("thien soLuongMon Fire duoi", soLuongCuaFood+"");
                                    Log.d("BBB", idFood + " /" + soLuongConLai + "");
                                    Food food = new Food(idCuaFood, tenCuaFood, giaCuaFood, soLuongConLai, anhCuaFood, diaChiCuaFood,
                                            moTaCuaFood, statusCuaFood, maTheLoaiCuaFood, idCuaHangCuaFood, idDanhMucCuaFood,
                                            idSanPhamCuaFood, khuyenMaiCuaFood, trangThaiCuaFood, tokenStoreCuaFood);
                                    databaseFood.updateSoLuongMon(food);
                                }
                            }
                        }
                        Log.d("CCCC", idFood + " /" + soLuongConLai + "");
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
                startActivity(new Intent(getApplicationContext(), ThanhToanActivity.class));
            }
        });

    }

    public ArrayList<Order> getCartList() {
        if (localStorage.getCart() != null) {
            String jsonCart = localStorage.getCart();
            Log.d("CART : ", jsonCart);
            Type type = new TypeToken<List<Order>>() {
            }.getType();
            orderArrayList = gson.fromJson(jsonCart, type);

            return orderArrayList;
        }
        return orderArrayList;
    }
}