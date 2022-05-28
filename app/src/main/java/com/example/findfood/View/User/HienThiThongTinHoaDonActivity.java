package com.example.findfood.View.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findfood.CallBack.ShipperCallBack;
import com.example.findfood.CallBack.StoreCallBack;
import com.example.findfood.CallBack.UserCallBack;
import com.example.findfood.Databases.DatabaseHDCT;
import com.example.findfood.Databases.DatabaseHDCT1;
import com.example.findfood.Databases.DatabaseShipper;
import com.example.findfood.Databases.DatabaseStore;
import com.example.findfood.Databases.DatabaseUser;
import com.example.findfood.HelperClasses.CartHDCTAdapter1;
import com.example.findfood.R;
import com.example.findfood.model.HDCT;
import com.example.findfood.model.HDCT1;
import com.example.findfood.model.Order;
import com.example.findfood.model.Order1;
import com.example.findfood.model.Shipper;
import com.example.findfood.model.Store;
import com.example.findfood.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HienThiThongTinHoaDonActivity extends AppCompatActivity {
    TextView txtDiaChiCuaHang, txtTenCuahang, txtphoneShipper,txttongtien;
    Button btnXacNhan;
    String idHDCT;
    RecyclerView recyclesanpham1;
    ArrayList<HDCT> cartList;
    LinearLayout layouttong, layoutTong;

    CartHDCTAdapter1 cartAdapter1 = null;

    double tongtien = 0;
    ArrayList<Order> orderArrayListLoadData;
    ArrayList<Order1> orderArrayList;
    DatabaseUser daoUser;
    DatabaseShipper databaseShipper;
    DatabaseHDCT databaseHDCT;
    DatabaseHDCT1 databaseHDCT1;
    DatabaseStore databaseStore;
    FirebaseUser firebaseUser;
    String tokkenstore = "";
    double tongtien1 = 0;
    double tongtiend = 0;

    String idNguoiDatMon = "";
    String trangThaiDonHang = "";
    String idCuaHoaDon = "";
    String idCuaHoaDon2 = "";
    String payment = "";
    String thoiGian = "";
    String idShipperCuaHDCT ="";

    String soLuong = "" ;
    String giaTien = "";
    String tongtiena = "";
    String idShpper = "";
    String diaChi = "";
    String tenCuaHang = "";
    String phoneShipper = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hien_thi_thong_tin_hoa_don);

        txtDiaChiCuaHang = findViewById(R.id.txtDiaChiCuaHang);
        txtTenCuahang = findViewById(R.id.txtTenCuahang);
        txtphoneShipper = findViewById(R.id.txtphoneShipper);
        txttongtien = findViewById(R.id.txttongtien);
        recyclesanpham1 = findViewById(R.id.recyclesanpham1);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        layouttong = findViewById(R.id.layouttong);
        layoutTong = findViewById(R.id.layoutTong);

        Intent intent = getIntent();
        idHDCT = intent.getStringExtra("IdHDCTQuangTrong");

        LoadData();

        cartList = new ArrayList<>();

        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###,###");

        databaseShipper = new DatabaseShipper(getApplicationContext());
        databaseHDCT = new DatabaseHDCT(getApplicationContext());
        databaseHDCT1 = new DatabaseHDCT1(getApplicationContext());

        databaseStore = new DatabaseStore(getApplicationContext());
        daoUser = new DatabaseUser(getApplicationContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        orderArrayList = new ArrayList<>();
        orderArrayListLoadData = new ArrayList<>();

        LoadDataUser();
        LoadDataStore();
        LoadDataShipper();
        HienThiDSMA();

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference datahoadon = FirebaseDatabase.getInstance().getReference("HDCT");
                datahoadon.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            HDCT1 hdct = dataSnapshot.getValue(HDCT1.class);
                            if (hdct.getIdHDCT().equalsIgnoreCase(idHDCT)){
                                idNguoiDatMon = hdct.getIdUser();
                                trangThaiDonHang = hdct.getCheck();
                                idCuaHoaDon = hdct.getIdHDCT();
                                idCuaHoaDon2 = hdct.getIdHoaDon();
                                orderArrayList.clear();
                                orderArrayList.addAll(hdct.getOrderArrayList());
                                payment = hdct.getPayment();
                                thoiGian = hdct.getThoigian();
                                idShpper = hdct.getIdShipper();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Log.d("Thien IDHDCT", idCuaHoaDon);

                HDCT1 hdcttest = new HDCT1(idCuaHoaDon, idCuaHoaDon2, thoiGian, "success", idNguoiDatMon, payment, idShpper, orderArrayList);
                databaseHDCT1.update(hdcttest);
                Toast.makeText(getApplicationContext(), "Nhận đơn Thành Công", Toast.LENGTH_SHORT).show();
                layoutTong.setVisibility(View.GONE);
                layouttong.setBackgroundResource( R.drawable.dathangtc);
                layouttong.setPadding(0, 100,0,0);
            }
        });
    }

    private void HienThiDSMA() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HDCT");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HDCT1 hdct = dataSnapshot.getValue(HDCT1.class);
                    if (hdct.getIdHDCT().equalsIgnoreCase(idHDCT)){
                        orderArrayList.addAll(hdct.getOrderArrayList());
                        cartAdapter1 = new CartHDCTAdapter1(hdct.getOrderArrayList(), getApplicationContext());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                        recyclesanpham1.setLayoutManager(linearLayoutManager);
                        recyclesanpham1.setAdapter(cartAdapter1);
                        Log.d("Thien", "vao duoc adpter");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadDataStore() {
        DatabaseStore databaseStore = new DatabaseStore(getApplicationContext());
        databaseStore.getAll(new StoreCallBack() {
            @Override
            public void onSuccess(ArrayList<Store> lists) {
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getTokenstore().equalsIgnoreCase(tokkenstore)) {
                        lists.get(i).getEmail();
                        lists.get(i).getPass();
                        lists.get(i).getTenCuaHang();
                        lists.get(i).getPhone();
                        lists.get(i).getImage();
                        lists.get(i).getDiaChi();
                        lists.get(i).getTrangThai();
                        lists.get(i).getMoTa();
                        lists.get(i).getCv();
                        txtDiaChiCuaHang.setText(lists.get(i).getDiaChi());
                        diaChi = lists.get(i).getDiaChi();
                        txtTenCuahang.setText(lists.get(i).getTenCuaHang());
                        tenCuaHang = lists.get(i).getTenCuaHang();
                    }
                }
                Log.d("thien dia chi", diaChi );
                Log.d("Thien ten cua hang", tenCuaHang);
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void LoadDataUser() {
        daoUser.getAll(new UserCallBack() {
            @Override
            public void onSuccess(ArrayList<User> lists) {
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getToken().equalsIgnoreCase(idNguoiDatMon)) {
                        lists.get(i).getEmail();
                        lists.get(i).getPassword();
                        lists.get(i).getName();
                        lists.get(i).getPhone();
                        lists.get(i).getImage();
                        lists.get(i).getDiachi();
                        lists.get(i).getTrangThai();
                        lists.get(i).getName();
                        lists.get(i).getDiachi();
                    }
                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }   //User

    public void LoadDataShipper() {
        DatabaseShipper databaseShipper = new DatabaseShipper(getApplicationContext());
        databaseShipper.getAll(new ShipperCallBack() {
            @Override
            public void onSuccess(ArrayList<Shipper> lists) {
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getToken().equalsIgnoreCase(idShipperCuaHDCT)) {
                        lists.get(i).getEmail();
                        lists.get(i).getPassword();
                        lists.get(i).getHoTen();
                        lists.get(i).getPhone();
                        lists.get(i).getAnh();
                        lists.get(i).getDiaChi();
                        lists.get(i).getNgaysinh();
                        lists.get(i).getTrangThai();
                        lists.get(i).getGioitinh();
                        lists.get(i).getCv();
                        txtphoneShipper.setText(lists.get(i).getPhone());
                        phoneShipper = lists.get(i).getPhone();
                    }
                }
                Log.d("thien so DienThoaiShipper", phoneShipper);
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void LoadData() {
        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###,###");
        DatabaseReference datahoadon = FirebaseDatabase.getInstance().getReference("HDCT");
        datahoadon.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderArrayListLoadData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HDCT hdct = dataSnapshot.getValue(HDCT.class);
                    if (hdct.getIdHDCT().equalsIgnoreCase(idHDCT)) {
                        idNguoiDatMon = hdct.getIdUser();
                        trangThaiDonHang = hdct.getCheck();
                        idCuaHoaDon = hdct.getIdHDCT();
                        idCuaHoaDon2 = hdct.getIdHoaDon();
                        payment = hdct.getPayment();
                        thoiGian = hdct.getThoigian();
                        idShipperCuaHDCT = hdct.getIdShipper();

                        orderArrayListLoadData.clear();
                        orderArrayListLoadData.addAll(hdct.getOrderArrayList());
                        for (Order order : orderArrayListLoadData) {
                            soLuong = String.valueOf(order.getSoluongmua());
                            giaTien = String.valueOf(order.getFood().getGiaTien());
                            tongtiena = String.valueOf((order.getFood().getGiaTien() * ((100 - order.getFood().getKhuyenMai()) * 0.01)));
                            tongtien1 = tongtien1 + order.getSoluongmua() * (order.getFood().getGiaTien() * ((100 - order.getFood().getKhuyenMai()) * 0.01));
                            tokkenstore = order.getFood().getTokenstore();
                            tongtiend = tongtien + order.getSoluongmua() * (order.getFood().getGiaTien() * ((100 - order.getFood().getKhuyenMai()) * 0.01));
                            Log.d("thientongtienHT", String.valueOf(tongtiend));

                        }
                        txttongtien.setText("Tổng Tiền: \t" + decimalFormat.format(tongtien1) + "VNĐ");
                    }
                }
                Log.d("thientongtienKM", tongtiena);

                Log.d("thienSL",soLuong);
                Log.d("thienGT",giaTien);
                Log.d("thientongTien", decimalFormat.format(tongtien1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}