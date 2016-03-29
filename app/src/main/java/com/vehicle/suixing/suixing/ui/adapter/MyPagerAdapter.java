package com.vehicle.suixing.suixing.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vehicle.suixing.suixing.bean.BmobBean.VehicleInformation;

import java.util.List;

/**
 * Created by KiSoo on 2016/3/20.
 */
public class MyPagerAdapter extends PagerAdapter {
    private List<VehicleInformation> info;
    private Context context;
    private String TAG = "MyPagerAdapter";
    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(context);
        ImageLoader.getInstance()
                .displayImage(info.get(position).getUrl(), imageView);
//        BmobFile bmobFile = new BmobFile();
//        bmobFile.setUrl(info.get(position).getUrl());
//        bmobFile.loadImage(context, imageView);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG,"点击了第"+position+"个");
//            }
//        });
        container.addView(imageView, position);
        return imageView;

    }
    public void itemClick(){

    }

//    @Override
//    public Object instantiateItem(View container, int position) {
//        return super.instantiateItem(container, position);
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((ImageView)object);
    }

    public MyPagerAdapter(Context context, List<VehicleInformation> info) {
        this.context = context;
        this.info = info;
    }
}
