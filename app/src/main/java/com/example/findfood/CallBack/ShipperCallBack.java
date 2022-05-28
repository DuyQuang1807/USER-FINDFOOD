package com.example.findfood.CallBack;

import com.example.findfood.model.Shipper;

import java.util.ArrayList;

public interface ShipperCallBack {
    void onSuccess(ArrayList<Shipper> lists);
    void onError(String message);
}
