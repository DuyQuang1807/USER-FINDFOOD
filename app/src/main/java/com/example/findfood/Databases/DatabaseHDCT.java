package com.example.findfood.Databases;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.findfood.CallBack.HDCTCallBack;
import com.example.findfood.model.HDCT;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseHDCT {
    Context context;
    DatabaseReference mRef;
    String key;

    public DatabaseHDCT(Context context) {
        this.context = context;
        this.mRef = FirebaseDatabase.getInstance().getReference("HDCT");
    }
    public void getAll(final HDCTCallBack callback) {
        final ArrayList<HDCT> dataloai = new ArrayList<>();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dataloai.clear();
                    for (DataSnapshot data : snapshot.getChildren()){
                        HDCT categories = data.getValue(HDCT.class);
                        dataloai.add(categories);
                    }
                    callback.onSuccess(dataloai);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }
    public void insert(HDCT item){
        // push cây theo mã tự tạo
        // string key lấy mã push
        key = mRef.push().getKey();

        HDCT hdctTest = new HDCT(key, key, item.getThoigian(), item.getCheck(), item.getIdUser(),item.getPayment(), "null", item.getOrderArrayList());

        //insert theo child mã key setvalue theo item
        mRef.child(key).setValue(hdctTest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Đặt Hàng Thành Công", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Đặt Hàng Thất Bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean update(final HDCT item){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.child("idHDCT").getValue(String.class).equalsIgnoreCase(item.getIdHDCT())){
                        key=dataSnapshot.getKey();
                        mRef.child(key).setValue(item);
                        Toast.makeText(context, "Update Thành Công", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
    }
    public void delete(final String matheloai){
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("idHDCT").getValue(String.class).equalsIgnoreCase(matheloai)){
                        key=dataSnapshot.getKey();
                        mRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Delete Thành Công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
