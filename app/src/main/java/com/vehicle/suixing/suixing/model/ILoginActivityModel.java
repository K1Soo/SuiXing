package com.vehicle.suixing.suixing.model;

import android.content.Context;

import com.vehicle.suixing.suixing.callback.BmobListener;

/**
 * Created by KiSoo on 2016/4/20.
 */
public interface ILoginActivityModel {
    void login(Context context,String username, String password, BmobListener listener);
}
