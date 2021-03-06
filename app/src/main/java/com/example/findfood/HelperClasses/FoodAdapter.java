package com.example.findfood.HelperClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findfood.DangNhapActivity;
import com.example.findfood.Databases.DatabaseFood;
import com.example.findfood.FoodProfileActivity;
import com.example.findfood.CallBack.FoodCallBack;
import com.example.findfood.R;
import com.example.findfood.model.Favorite;
import com.example.findfood.model.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder1> {

    ArrayList<Food> categoryList;
    Context context;
    String pLikes;
    DatabaseReference databaseReference,fvrtref,fvrt_listRef;
    FirebaseUser user;
    DatabaseFood databaseFood;
    Food food1 = null ;
    Boolean mProcessLike =false;
    String Tag;

    public FoodAdapter(ArrayList<Food> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {
        ImageView imageView, likeBtn;
        TextView title, txtdiachi, txtgia, txtTrangThai,txtPriceDiscoount,txtPhanTramKM;
        ProgressBar progressBar;
        CardView cardView, cardView1, card_view4, vien, cardview_Nhan;
        LinearLayout khung;

        public MyViewHolder1(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgfood);
            title = itemView.findViewById(R.id.txtnamefood);
            progressBar = itemView.findViewById(R.id.progressbar);
            txtdiachi = itemView.findViewById(R.id.txtdiachi);
            txtgia = itemView.findViewById(R.id.txtgia);
            khung = itemView.findViewById(R.id.khung);
            vien = itemView.findViewById(R.id.vien);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            txtPriceDiscoount = itemView.findViewById(R.id.txtPriceDiscoount);
            txtPhanTramKM = itemView.findViewById(R.id.txtPhanTramKM);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            cardview_Nhan = itemView.findViewById(R.id.cardview_Nhan);
        }
    }


    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemfood, parent, false);
        return new MyViewHolder1(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder1 holder, final int position) {
        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###,###,###");
        Food categories = categoryList.get(position);
        holder.title.setText(categories.getTenSanPham());
        holder.txtdiachi.setText(categories.getDiaChi());
        holder.txtPhanTramKM.setText(String.valueOf("-" + categories.getKhuyenMai() + "%"));
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseFood = new DatabaseFood(context);
        fvrtref = FirebaseDatabase.getInstance().getReference("favourites");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            fvrt_listRef = FirebaseDatabase.getInstance().getReference("favoriteList").child(user.getUid());
        } else {
            // No user is signed in
        }
        holder.txtTrangThai.setText(categories.getTrangThai());


        holder.txtgia.setText(String.valueOf(decimalFormat.format(categories.getGiaTien()) + " VN??"));
        Picasso.get()
                .load(categories.getAnh())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Error : ", e.getMessage());
                    }
                });

        String giaKhuyenMai = String.valueOf( decimalFormat.format( categories.getGiaTien() * ((100 - categories.getKhuyenMai()) * 0.01))  + " VN??");
        holder.txtPriceDiscoount.setText(giaKhuyenMai);

        String  key = categories.getIdfood();
        if (user != null) {
            // User is signed in
            favouriteChecker(key,holder);
        } else {
            // No user is signed in
            holder.likeBtn.setImageResource(R.drawable.ic_heart);
        }
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    // User is signed in
                    mProcessLike = true;
                    fvrtref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (mProcessLike.equals(true)){
                                if (snapshot.child(key).hasChild(user.getUid())){
                                    fvrtref.child(key).child(user.getUid()).removeValue();
                                    fvrt_listRef.child(key).removeValue();
                                    Toast.makeText(context, "Xo?? Kh???i Danh S??ch Y??u Th??ch", Toast.LENGTH_SHORT).show();
                                    mProcessLike = false;
                                }else {
                                    fvrtref.child(key).child(user.getUid()).setValue(true);
                                    Favorite favorite = new Favorite(fvrt_listRef.push().getKey(), user.getUid(), categories,true);

                                    fvrt_listRef.child(key).setValue(favorite);
                                    mProcessLike = false;

                                    Toast.makeText(context, "Th??m V??o Danh S??ch Y??u Th??ch", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    // No user is signed in

                    context.startActivity(new Intent(context, DangNhapActivity.class));
                }
            }
        });

        holder.cardview_Nhan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (user != null) {
                    // User is signed in
                    Intent i = new Intent(context, FoodProfileActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    i.putExtra("img", categories.getAnh());
                    i.putExtra("gia", decimalFormat.format(categories.getGiaTien()) + "\t VN??");
                    i.putExtra("namefood", categories.getTenSanPham());
                    i.putExtra("idfood", "Id: " + categories.getIdfood());
                    i.putExtra("idstore", categories.getIdCuaHang());
                    i.putExtra("diachi", categories.getDiaChi());
                    i.putExtra("sl", categories.getSoLuong() + "");
                    i.putExtra("matl", categories.getMatheloai());
                    i.putExtra("status", categories.getStatus());
                    i.putExtra("mota", categories.getMota());
                    i.putExtra("trangThai", categories.getTrangThai());
                    i.putExtra("khuyenMai", categories.getKhuyenMai());
                    i.putExtra("idSanPham", categories.getIdSanPham());
                    i.putExtra("idDanhMuc", categories.getIdDanhMuc());
                    i.putExtra("tokenstore", categories.getTokenstore());
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else {
                    // No user is signed in
                    Intent dangNhap = new Intent(context,DangNhapActivity.class);
                    dangNhap.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    dangNhap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dangNhap);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();

    }


    public void favouriteChecker(final String postkey, MyViewHolder1 holder) {
        final String uid = user.getUid();

        fvrtref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey).hasChild(uid)){
                    holder.likeBtn.setImageResource(R.drawable.ic_heart_liked);
                }else {
                    holder.likeBtn.setImageResource(R.drawable.ic_heart);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

