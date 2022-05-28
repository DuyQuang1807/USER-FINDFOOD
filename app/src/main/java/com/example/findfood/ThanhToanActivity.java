package com.example.findfood;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.findfood.CallBack.UserCallBack;
import com.example.findfood.Databases.DatabaseHDCT;
import com.example.findfood.Databases.DatabaseShipper;
import com.example.findfood.Databases.DatabaseUser;
import com.example.findfood.HelperClasses.CartAdapterSoLuongMon;
import com.example.findfood.Notification.DataHoaDon;
import com.example.findfood.Notification.SenderHoaDon;
import com.example.findfood.local.LocalStorage;
import com.example.findfood.model.HDCT;
import com.example.findfood.model.Order;
import com.example.findfood.model.Token;
import com.example.findfood.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ThanhToanActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    RecyclerView rcvcart;
    CartAdapterSoLuongMon cartAdapter = null;
    LocalStorage localstorage;
    RadioGroup rdbGroup;

    RadioButton rdbTien, rdbNganHang, radioButton;

    Gson gson;
    Toolbar toolbar;
    TextView titletoolbar, txtaddress, txttientong, txtPayment, pttt;
    RelativeLayout linearbackground;
    DatabaseHDCT daoHDCT;
    DatabaseUser daoUser;
    User userNode;
    Button btnthanhtoan;
    double tongtien = 0;
    double tongtien1 = 0;
    double tienThue = 0;
    LinearLayout linearLayout;
    ScrollView scrollView;
    FirebaseUser user;
    ArrayList<Order> orderArrayList;
    ArrayList<HDCT> hdctArrayList;
    private RequestQueue requestQueue;
    String namefood, nameuser, namestore, nameShipper;
    int slm;
    double gia;

    DatabaseShipper databaseShipper;
    String Check = "active";
    String trangThaiHDCT = "waiting";
    String idShip, idShip1;
    String payment = "";
    String payment1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_thanh_toan);

        rcvcart = findViewById(R.id.rcvcart);
        localstorage = new LocalStorage(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(ThanhToanActivity.this, R.color.cam));
        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linear1);
        txtaddress = findViewById(R.id.txtaddress);
        txttientong = findViewById(R.id.txttientong);
        btnthanhtoan = findViewById(R.id.btn_insertcart);

        rdbGroup = findViewById(R.id.rdbGroup);

        rdbTien = findViewById(R.id.rdbTien);
        rdbNganHang = findViewById(R.id.rdbNganHang);

        pttt = findViewById(R.id.pttt);
        linearbackground = findViewById(R.id.linearbackground);
        titletoolbar = findViewById(R.id.toolbar_title);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###,###");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titletoolbar.setText("Đặt Hàng");
        titletoolbar.setTextSize(30);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        gson = new Gson();
        hdctArrayList = new ArrayList<>();
        orderArrayList = new ArrayList<>();
        orderArrayList = getCartList();
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HDCT");
        String keyhdct = databaseReference.push().getKey();
        SimpleDateFormat tg = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String thoigian = tg.format(new Date());
        hdctArrayList.clear();


        for (Order order : getCartList()) {
            if (order.getUser().getEmail() != null) {
                nameuser = order.getUser().getEmail();
            }
        }
        nameShipper = "7ZqRKu4cuIeXYsvyAhPtRH5Eu5j2";


        HDCT hoadonchitiet = new HDCT(keyhdct, keyhdct, thoigian, trangThaiHDCT, user.getUid(), payment, "null", getCartList());

        hdctArrayList.add(hoadonchitiet);

        /*
            Lấy cái đoạn if (getCartList().size() != 0) vào handler để tự động reload giá tiền trong thanh toán
        */
        if (getCartList().size() != 0) {
            cartAdapter = new CartAdapterSoLuongMon(orderArrayList, ThanhToanActivity.this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ThanhToanActivity.this, LinearLayoutManager.VERTICAL, false);
            rcvcart.setLayoutManager(linearLayoutManager);
            rcvcart.setAdapter(cartAdapter);

//            final Handler handler = new Handler();
//            Runnable refresh = new Runnable() {
//                @Override
//                public void run() {
            for (Order order : getCartList()) {
                tongtien1 = tongtien1 + order.getSoluongmua() * (order.getFood().getGiaTien() * ((100 - order.getFood().getKhuyenMai()) * 0.01));
                tienThue = tongtien1 * 0.1;
                tongtien = tongtien1 + tienThue;
            }

            txttientong.setText(decimalFormat.format(tongtien) + " VNĐ");
//                    handler.postDelayed(this, 2000);
//                }
//            };
//            handler.postDelayed(refresh, 2000);
            btnthanhtoan.setVisibility(View.GONE);
            rdbGroup.check(rdbTien.getId());
            String text = "Thanh Toán Bằng Tiền Mặt";
            pttt.setText(text);
            payment = pttt.getText().toString();
            btnthanhtoan.setVisibility(View.VISIBLE);

            rdbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i) {
                        case R.id.rdbTien:
                            String text = "Thanh Toán Bằng Tiền Mặt";
                            pttt.setText(text);
                            payment = pttt.getText().toString();
                            btnthanhtoan.setVisibility(View.VISIBLE);
                            break;
                        case R.id.rdbNganHang:
                            String text1 = "Thanh Toán Trực Tuyến";
                            pttt.setText(text1);
                            payment = pttt.getText().toString();
                            btnthanhtoan.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
            int selectedId = rdbGroup.getCheckedRadioButtonId();


            Log.d("thien thanh toan", payment);

            btnthanhtoan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        daoHDCT = new DatabaseHDCT(ThanhToanActivity.this);
                        hdctArrayList.clear();
                        HDCT hoadonchitiet1 = new HDCT(keyhdct, keyhdct, thoigian, trangThaiHDCT, user.getUid(), payment, "null", getCartList());
                        hdctArrayList.add(hoadonchitiet1);
                        Log.d("thien hdct", hoadonchitiet1.getPayment());
                        for (HDCT hdct : hdctArrayList) {
                            daoHDCT.insert(hdct);
                            sendNotifiaction(nameShipper, nameuser, "Xác nhận đơn hàng", user.getUid(), hdct.getIdHDCT());
                        }
                        localstorage.deleteCart();
                        rcvcart.setVisibility(View.GONE);
                        titletoolbar.setVisibility(View.GONE);
                        linearbackground.setBackgroundResource(R.drawable.dathangtc);
                        linearbackground.setPadding(0, 100, 0, 0);
                        scrollView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                    } else {
                        // No user is signed in
                        startActivity(new Intent(getApplicationContext(), DangNhapActivity.class));
                    }
                }
            });
        } else {

            rcvcart.setVisibility(View.GONE);
            linearbackground.setBackgroundResource(R.drawable.empty_cart);
            scrollView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);

        }

        daoUser = new DatabaseUser(ThanhToanActivity.this);
        daoUser.getAll(new UserCallBack() {
            @Override
            public void onSuccess(ArrayList<User> lists) {
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getToken().matches(user.getUid())) {
                        txtaddress.setText(lists.get(i).getDiachi());
                    }
                }
            }

            @Override
            public void onError(String message) {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ThanhToanActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                finish();
            }
        });
    }

    public ArrayList<Order> getCartList() {
        if (localstorage.getCart() != null) {
            String jsonCart = localstorage.getCart();
            Log.d("CART : ", jsonCart);
            Type type = new TypeToken<List<Order>>() {
            }.getType();
            orderArrayList = gson.fromJson(jsonCart, type);

            return orderArrayList;
        }
        return orderArrayList;
    }

    private String checkPayment (String t) {
        if (t.equalsIgnoreCase("1")) {
            t = "Thanh Toán Bằng Tiền Mặt";
        } else {
            t = "Thanh Toán Trực Tuyến";
        }

        return t;
    }

    private void sendNotifiaction(String receiver, final String username, final String message, final String tokensStore, String listhdct) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        Log.d("Token cua query", String.valueOf(query));
        Log.d("Token cua query", tokens.toString());
        Log.d("Token cua query", receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Log.d("token getValue", token.toString());
                    Log.d("token getValue ", String.valueOf(token));
                    Toast.makeText(ThanhToanActivity.this, "Đã gửi", Toast.LENGTH_SHORT).show();
                    DataHoaDon data = new DataHoaDon(user.getUid(), R.mipmap.ic_launcher_round, username + ": " + message, "Đơn Đặt Hàng",
                            tokensStore, listhdct);
                    Log.d("token id user " + user.getUid(), "tokenStore nghi van là iduser" + tokensStore);
                    Log.d(" tokenlistHDCT", listhdct);
                    SenderHoaDon sender = new SenderHoaDon(data, token.getToken());
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //response of the request
                                        Log.d("JSON_RESPONSE", "onResponse: " + response.toString());

                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: " + error.toString());
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                //put params
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAlXNfPmA:APA91bHV9yJpknpfX9O97Ga02t-6RoJHFL8JqBFKg9gM7rYZjdJEdp1bQPXzMF1cF0tpbDfAM99Wm1maNty0YnMb6DOeQNd4rqVHrENFo-XE-ff4TDuvtgLHvvKIraka54v5sPtQmdBG");
                                return headers;
                            }
                        };

                        //add this request to queue
                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean iCheckPayment() {
        if (payment.isEmpty()) {
            thongBao();
            return false;
        } else {
            return true;
        }
    }

    public void thongBao() {
        Toast.makeText(getApplicationContext(), "Vui lòng chọn phương thúc thanh toán !!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user ==null){
//            Intent intent = new Intent(ThanhToanActivity.this, DangNhapActivity.class);
//            startActivity(intent);
//        }
    }
}