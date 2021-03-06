package com.example.findfood.Databases;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.findfood.CallBack.FoodCallBack;
import com.example.findfood.model.Food;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseFood {
    Context context;
    DatabaseReference mRef;
    String key;

    public DatabaseFood(Context context) {
        this.context = context;
        this.mRef = FirebaseDatabase.getInstance().getReference("Food");
    }
    public void getAll(final FoodCallBack callback) {
        final ArrayList<Food> dataloai = new ArrayList<>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dataloai.clear();

                    for (DataSnapshot data : snapshot.getChildren()){
                        Food categories = data.getValue(Food.class);
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

    public void getAllSoLuongMon(final FoodCallBack callback) {
        final ArrayList<Food> dataloai = new ArrayList<>();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dataloai.clear();

                    for (DataSnapshot data : snapshot.getChildren()){
                        Food categories = data.getValue(Food.class);
                        dataloai.add(categories);
                    }
                    callback.onSuccess(dataloai);
                }
                mRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }

    public void insert(Food item){
        // push c??y theo m?? t??? t???o
        // string key l???y m?? push
        key = mRef.push().getKey();
        //insert theo child m?? key setvalue theo item
        mRef.child(key).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Insert Th??nh C??ng", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Insert Th???t B???i", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean update(final Food item){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.child("idfood").getValue(String.class).equalsIgnoreCase(item.getIdfood())){
                        key=dataSnapshot.getKey();
                        mRef.child(key).setValue(item);
//                        Toast.makeText(context, "Update Th??nh C??ng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
    }

    public boolean updateSoLuongMon(final Food item){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.child("idfood").getValue(String.class).equalsIgnoreCase(item.getIdfood())){
                        key=dataSnapshot.getKey();
                        Log.d("AAAA", "chay");
                        mRef.child(key).child("soLuong").setValue(item.getSoLuong());
//                        Toast.makeText(context, "Update Th??nh C??ng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        public boolean updateSoLuong (final Food item) {
//            mRef.
//        }

        return true;
    }
    public void delete(final String matheloai){
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("idfood").getValue(String.class).equalsIgnoreCase(matheloai)){
                        key=dataSnapshot.getKey();
                        mRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Delete Th??nh C??ng", Toast.LENGTH_SHORT).show();
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
