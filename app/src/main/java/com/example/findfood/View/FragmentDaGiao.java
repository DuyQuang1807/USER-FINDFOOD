package com.example.findfood.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.findfood.CallBack.HDCTCallBack;
import com.example.findfood.Databases.DatabaseHDCT;
import com.example.findfood.HelperClasses.XacNhanAdapter;
import com.example.findfood.R;
import com.example.findfood.model.HDCT;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FragmentDaGiao extends Fragment {

    XacNhanAdapter xacNhanAdapter;
    DatabaseHDCT databaseHDCT;
    RecyclerView rcvdagiao;
    ArrayList<HDCT> arrayList;
    FirebaseUser firebaseUser;
    private String check = "success";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_da_giao,container,false);
        rcvdagiao = view.findViewById(R.id.rcvdagiao);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcvdagiao.setLayoutManager(linearLayoutManager);
        databaseHDCT = new DatabaseHDCT(getActivity());
        arrayList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseHDCT.getAll(new HDCTCallBack() {
            @Override
            public void onSuccess(ArrayList<HDCT> lists) {
                arrayList.clear();
                if (lists.size() != 0) {

                }
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getIdUser().equalsIgnoreCase(firebaseUser.getUid()) && lists.get(i).getCheck().equalsIgnoreCase(check)) {
                        arrayList.add(lists.get(i));
                        xacNhanAdapter = new XacNhanAdapter(arrayList, getActivity());
                        rcvdagiao.setAdapter(xacNhanAdapter);
                    }
                }
            }

            @Override
            public void onError(String message) {

            }
        });


        return view;
    }
}