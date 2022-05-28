package com.example.findfood;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findfood.CallBack.FoodCallBack;
import com.example.findfood.CallBack.StoreCallBack;
import com.example.findfood.CallBack.UserCallBack;
import com.example.findfood.Databases.DatabaseFood;
import com.example.findfood.Databases.DatabaseStore;
import com.example.findfood.Databases.DatabaseUser;
import com.example.findfood.HelperClasses.FoodProfileAdapter;
import com.example.findfood.View.CartActivity;
import com.example.findfood.local.LocalStorage;
import com.example.findfood.model.Food;
import com.example.findfood.model.Order;
import com.example.findfood.model.Shipper;
import com.example.findfood.model.Store;
import com.example.findfood.model.User;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.findfood.MainActivity.emailuser;

public class FoodProfileActivity extends AppCompatActivity {
    TextView tv_detail_rating, tvsl, tv_detail_vote_count, txtsoluong, txtdiachi, txtmota, txtstatus, txtmatl, txtTrangThai;
    Toolbar toolbar;
//    TextView tv_detail_release_date;
    ImageView iv_backdrop, iv_detail_poster;
    int vohan = 0;

    User userNodeFoodProfile;
    Shipper shipperNodeFoodProfile;
    int sluongmua = 0;
    String idfood = "";
    DatabaseFood databaseFood;
    ArrayList<Food> foodArrayList;
    ArrayList<Food> categoryList;
    ArrayList<Food> dsfoodall = new ArrayList<>();
    ArrayList<Order> orderArrayList = new ArrayList<>();
    ArrayList<Store> storeArrayList = new ArrayList<>();
    RecyclerView rv_reviews;
    FoodProfileAdapter foodAdapter;
    CollapsingToolbarLayout collapsingToolbarLayout;
    LinearLayout plush, minus;
    DatabaseReference databaseReference;
    DatabaseStore databaseStore;
    ArrayList<User> dsUser = new ArrayList<>();
    DatabaseUser databaseUser;
    Button btn_insertcart;
    String trangThai = "";
    String check = "Còn Hàng";
    String outstock = "outstock";
    String banned = "banned";

    String idstore = "";
    String tokenstore = "";
    String idFoodDatHang ="";
    int soLuongMonDat =0;
    int soLuongMonDaDatMoi =0;
    int soLuongDauTien =0;
    String soLuong = "";

    Gson gson;
    LocalStorage localStorage;
    double showtien = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_profile);

        iv_backdrop = findViewById(R.id.iv_backdrop);
        tv_detail_rating = findViewById(R.id.tv_detail_rating);
        iv_detail_poster = findViewById(R.id.iv_detail_poster);
        tv_detail_vote_count = findViewById(R.id.tv_detail_vote_count);
//        tv_detail_release_date = findViewById(R.id.tv_detail_release_date);
        rv_reviews = findViewById(R.id.rv_reviews);
        btn_insertcart = findViewById(R.id.btn_insertcart);
        plush = findViewById(R.id.plush);
        minus = findViewById(R.id.minus);
        tvsl = findViewById(R.id.tvsl);
        txtsoluong = findViewById(R.id.txtsoluong);
        txtdiachi = findViewById(R.id.txtdiachi);
        txtmota = findViewById(R.id.txtmota);
        txtstatus = findViewById(R.id.txtstatus);
        txtmatl = findViewById(R.id.txtmatl);
        txtTrangThai = findViewById(R.id.txtTrangthaiActivityFoodProfile);
        toolbar = findViewById(R.id.toolbar);

        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###,###");

        // Khai báo phần header của file activity_food_profile
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().setStatusBarColor(ContextCompat.getColor(FoodProfileActivity.this, R.color.cam));
        Intent intent = getIntent();
        Picasso.get().load(intent.getStringExtra("img")).into(iv_backdrop);
        Picasso.get().load(intent.getStringExtra("img")).into(iv_detail_poster);
        collapsingToolbarLayout.setTitle(intent.getStringExtra("namefood"));
        tv_detail_rating.setText(intent.getStringExtra("gia"));
//        tv_detail_release_date.setText(intent.getStringExtra("idfood"));
        idfood = intent.getStringExtra("idfood");
        tokenstore = intent.getStringExtra("tokenstore");
        txtdiachi.setText("Địa Chỉ:\t" + intent.getStringExtra("diachi"));
        txtsoluong.setText("Số Lượng:\t" + intent.getStringExtra("sl"));
        soLuongDauTien = Integer.parseInt(intent.getStringExtra("sl"));
        txtmatl.setText("Loại:\t" + intent.getStringExtra("matl"));
        txtstatus.setText("Buổi:\t" + intent.getStringExtra("status"));
        txtmota.setText("Mô Tả:\t" + intent.getStringExtra("mota"));
        txtTrangThai.setText("Trạng Thái:\t" + intent.getStringExtra("trangThai"));
        tv_detail_vote_count.setText("Đăng bởi " + intent.getStringExtra("idstore"));

        soLuong = intent.getStringExtra("sl");
        trangThai = intent.getStringExtra("trangThai");
        Log.d("thien TrangThai", trangThai);
        Log.d("Thien trangThai", check);

        databaseFood = new DatabaseFood(FoodProfileActivity.this);
        foodArrayList = new ArrayList<>();
        foodAdapter = new FoodProfileAdapter(foodArrayList, FoodProfileActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FoodProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rv_reviews.setLayoutManager(linearLayoutManager);
        rv_reviews.setHasFixedSize(true);
        rv_reviews.setAdapter(foodAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");

        idstore = intent.getStringExtra("idstore");

        String keyhdct = databaseReference.push().getKey();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        SimpleDateFormat tg = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String thoigian = tg.format(new Date());

        checkSoLuongMon();

        localStorage = new LocalStorage(this);
        gson = new Gson();
        orderArrayList.clear();
        plush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!trangThai.equalsIgnoreCase(outstock) || !trangThai.equalsIgnoreCase(banned)) {
                    int sluongnhap = Integer.parseInt(tvsl.getText().toString());

                    Log.d("thien sluongnhap", String.valueOf(sluongnhap));

                    if (sluongnhap >= 0 && sluongnhap <=(soLuongDauTien-1)) {

                        int sluongmua_item = Integer.parseInt(tvsl.getText().toString());
                        sluongmua_item++;

                        Log.d("thien sluongmua_item", String.valueOf(sluongmua_item));

                        tvsl.setText(sluongmua_item + "");
                        try {
                            Food food = null;
                            User user = null;
                            Shipper shipper = null;
                            sluongmua = Integer.parseInt(tvsl.getText().toString());

                            Log.d("thien sluongmua", String.valueOf(sluongmua));

                            for (int i = 0; i < dsfoodall.size(); i++) {
                                if (dsfoodall.get(i).getIdfood().matches(idfood.substring(4))) {
                                    food = dsfoodall.get(i);
                                    Log.d("thien dsfoodall.get(i).toString()", dsfoodall.get(i).toString());
                                    break;
                                }
                            }

                            databaseFood = new DatabaseFood(FoodProfileActivity.this);
                            databaseUser = new DatabaseUser(FoodProfileActivity.this);

                            FirebaseUser userbase = FirebaseAuth.getInstance().getCurrentUser();

                            for (int i = 0; i < dsUser.size(); i++) {
                                if (dsUser.get(i).getToken().matches(userbase.getUid())) {
                                    user = dsUser.get(i);
                                    break;
                                }
                            }

                            String idfoodcheck = idfood.substring(4);
                            int check = checkmahdct(orderArrayList, idfoodcheck);
                            Order order = new Order(keyhdct, food, sluongmua_item, user, shipper);
                            Log.i("Check", String.valueOf(check));
                            if (check >= 0) {
                                int sluongmua = orderArrayList.get(check).getSoluongmua();

                                order.setSoluongmua(sluongmua + 1);
                                orderArrayList.set(check, order);
                                String cartStr = gson.toJson(orderArrayList);
                                localStorage.setCart(cartStr);
                            } else {
                                int postion = -1;
                                for (int i = 0; i < getCartList().size(); i++) {
                                    if (getCartList().get(i).getFood().getIdfood().matches(idfoodcheck)) {
                                        postion = i;
                                        break;
                                    }
                                }
                                if (postion < 0) {
                                    orderArrayList.add(order);
                                    String cartStr = gson.toJson(orderArrayList);
                                    localStorage.setCart(cartStr);
                                    Log.d("thien cartStr", cartStr);
                                }
                            }
                        } catch (Exception e) {
                            Log.i("fiX", e.getMessage());
                        }

                        try {
                            for (Order hdct : orderArrayList) {
                                showtien = hdct.getSoluongmua() * hdct.getFood().getGiaTien();
                                idFoodDatHang = hdct.getFood().getIdfood();
                                soLuongMonDat = hdct.getSoluongmua();
                                Log.d("thien hdct.getSoluongmua", String.valueOf(soLuongMonDat));
                                Log.d("thien hdct.getFood().getGiaTien()", String.valueOf(hdct.getFood().getGiaTien()));
                            }
                        } catch (Exception e) {
                            Log.i("Error", e.toString());
                        }
                        Log.d("thien showtien", String.valueOf(showtien));
                        btn_insertcart.setText("THÊM VÀO GIỎ HÀNG -\t" + decimalFormat.format(showtien) + "\tVNĐ");
                    } else{
                        Toast.makeText(getApplicationContext(), "Ban khong the dat qua so luong mon!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Món hiện đã hết!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int sluongnhap = Integer.parseInt(tvsl.getText().toString());
                if (sluongnhap != 0) {
                    int sluongmua_item = Integer.parseInt(tvsl.getText().toString());
                    sluongmua_item--;
                    tvsl.setText(sluongmua_item + "");
                    String idfoodcheck = idfood.substring(4);
                    int check1 = checkmahdct(orderArrayList, idfoodcheck);
                    Food food = null;
                    Store store = null;
                    User user = null;
                    for (int i = 0; i < dsfoodall.size(); i++) {
                        if (dsfoodall.get(i).getIdfood().matches(idfood.substring(4))) {
                            food = dsfoodall.get(i);
                            break;
                        }
                    }
                    for (int i = 0; i < storeArrayList.size(); i++) {
                        if (storeArrayList.get(i).getEmail() != null && storeArrayList.get(i).getEmail().matches(idstore)) {
                            store = storeArrayList.get(i);
                            break;
                        }
                    }
                    for (int i = 0; i < dsUser.size(); i++) {
                        if (dsUser.get(i).getEmail() != null && dsUser.get(i).getEmail().matches(emailuser)) {
                            user = dsUser.get(i);
                            break;
                        }
                    }
                    Order orderminus = new Order(keyhdct, food, sluongmua_item, userNodeFoodProfile, shipperNodeFoodProfile);
                    if (check1 >= 0) {
                        int sluongmua = orderArrayList.get(check1).getSoluongmua();
                        orderminus.setSoluongmua(sluongmua - 1);
                        orderArrayList.set(check1, orderminus);
                        String cartStr = gson.toJson(orderArrayList);
                        localStorage.setCart(cartStr);
                        Log.i("TAG", String.valueOf(orderminus.getSoluongmua()));


                    } else {
                    }

                    try {
                        for (Order hdct1 : orderArrayList) {
                            showtien = hdct1.getSoluongmua() * hdct1.getFood().getGiaTien();
                        }
                        btn_insertcart.setText("THÊM VÀO GIỎ HÀNG -\t" + decimalFormat.format(showtien) + "\tVNĐ");
                    } catch (Exception e) {
                    }
                }
            }
        });

        btn_insertcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sluongnhap = Integer.parseInt(tvsl.getText().toString());
                Log.d("thien sluongnhap" , sluongnhap+"");
                if (trangThai.equalsIgnoreCase(outstock)) {
                    Toast.makeText(getApplicationContext(), "Thêm Vào Giỏ Hàng Thất Bại", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(FoodProfileActivity.this, MainActivity.class));
                } else if (trangThai.equalsIgnoreCase(banned)) {
                    Toast.makeText(getApplicationContext(), "Thêm Vào Giỏ Hàng Thất Bại", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(FoodProfileActivity.this, MainActivity.class));
                } else if (sluongnhap ==0 ) {
                    Toast.makeText(getApplicationContext(), "Thêm Vào Giỏ Hàng Thất Bại", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(FoodProfileActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Thêm Đơn Vào Giỏ Hàng Thành Công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FoodProfileActivity.this, MainActivity.class));
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(FoodProfileActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
                finish();
            }
        });

        databaseFood.getAll(new FoodCallBack() {
            @Override
            public void onSuccess(ArrayList<Food> lists) {
                dsfoodall.clear();
                dsfoodall.addAll(lists);
            }

            @Override
            public void onError(String message) {

            }
        });
        databaseStore = new DatabaseStore(FoodProfileActivity.this);
        databaseUser = new DatabaseUser(FoodProfileActivity.this);

        databaseStore.getAll(new StoreCallBack() {
            @Override
            public void onSuccess(ArrayList<Store> lists) {
                storeArrayList.clear();
                storeArrayList.addAll(lists);
            }

            @Override
            public void onError(String message) {

            }
        });

        databaseUser.getAll(new UserCallBack() {
            @Override
            public void onSuccess(ArrayList<User> lists) {
                dsUser.clear();
                dsUser.addAll(lists);
            }

            @Override
            public void onError(String message) {

            }
        });
        databaseFood.getAll(new FoodCallBack() {
            @Override
            public void onSuccess(ArrayList<Food> lists) {
                foodArrayList.clear();
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getIdCuaHang() != null && lists.get(i).getIdCuaHang().equalsIgnoreCase(idstore)) {
                        foodArrayList.add(lists.get(i));
                        foodAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onError(String message) {

            }
        });


    }

    private void checkSoLuongMon() {
        if(trangThai.equalsIgnoreCase(outstock) || trangThai.equalsIgnoreCase(banned) || Integer.parseInt(soLuong) == 0 ) {
            btn_insertcart.setVisibility(View.GONE);
            plush.setVisibility(View.GONE);
            minus.setVisibility(View.GONE);
            tvsl.setVisibility(View.GONE);
        }
    }

    public ArrayList<Order> getCartList() {
        if (localStorage.getCart() != null) {
            String jsonCart = localStorage.getCart();
            Log.d("CART : ", jsonCart);
            Type type = new TypeToken<List<Order>>() {
            }.getType();
            orderArrayList = gson.fromJson(jsonCart, type);
            return orderArrayList;
        } else {

        }
        return orderArrayList;
    }

    public int checkmahdct(ArrayList<Order> dsOrder, String mafood) {

        int poss = -1;
        Log.i("Size", String.valueOf(dsOrder.size()));
        for (int i = 0; i < dsOrder.size(); i++) {

            Log.i("dshct", String.valueOf(dsOrder.get(i).getFood()));
            if (dsOrder.get(i).getFood().getIdfood().matches(mafood)) {
                poss = i;
                break;
            }
        }
        return poss;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_detail_chat) {
            Intent intent = new Intent(FoodProfileActivity.this, MessegerActivity.class);
            intent.putExtra("userId", tokenstore);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}