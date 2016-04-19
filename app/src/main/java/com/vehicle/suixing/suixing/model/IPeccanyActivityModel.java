package com.vehicle.suixing.suixing.model;

import com.vehicle.suixing.suixing.bean.BmobBean.VehicleInformation;
import com.vehicle.suixing.suixing.bean.WeiZhang1.WeizhangDate;
import com.vehicle.suixing.suixing.callback.GetWeizhangInfo;
import com.vehicle.suixing.suixing.callback.SwitchDate;

import java.util.List;

/**
 * Created by KiSoo on 2016/4/20.
 */
public interface IPeccanyActivityModel {
    void beginQuery(VehicleInformation info,GetWeizhangInfo callback);

    void switchDate(List<WeizhangDate> list,SwitchDate callback);
}
