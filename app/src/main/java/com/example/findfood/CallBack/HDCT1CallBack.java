package com.example.findfood.CallBack;

import com.example.findfood.model.HDCT;
import com.example.findfood.model.HDCT1;

import java.util.ArrayList;

public interface HDCT1CallBack {
    void onSuccess(ArrayList<HDCT1> lists);
    void onError(String message);
}
