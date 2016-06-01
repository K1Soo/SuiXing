package com.vehicle.suixing.suixing.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.vehicle.suixing.suixing.bean.BmobBean.VehicleInformation;
import com.vehicle.suixing.suixing.bean.musicInfo.BmobMusic;
import com.vehicle.suixing.suixing.bean.musicInfo.Mp3Info;
import com.vehicle.suixing.suixing.common.Config;
import com.vehicle.suixing.suixing.service.MusicPlayService;
import com.vehicle.suixing.suixing.util.Log;
import com.vehicle.suixing.suixing.util.dataBase.DbDao;
import com.vehicle.suixing.suixing.util.musicUtil.MusicUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by KiSoo on 2016/3/24.
 */
public class SuixingApp extends Application {
    public static ArrayList<Activity> activities;
    private static String TAG = SuixingApp.class.getName();
    public static boolean hasUser = false;
    public static List<VehicleInformation> infos;
    public static List<Mp3Info> mp3Infos;
    public static List<Mp3Info> onLineInfos;
    public static List<BmobMusic> bmobMusicList;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * bmob的初始化
         * */
        initBmob();
        /***
         * 百度地图的初始化
         */
        initBaiduMap();
        /**
         * imageloader的初始化
         * */
        initImageLoader();
        /***
         * 初始化全局的List
         */
        initInfoList();
        startService(new Intent(getApplicationContext(), MusicPlayService.class));
    }

    private void initBmob() {
        Bmob.initialize(this, Config.BMOBID);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Config.BMOBID);
    }

    private void initInfoList() {
        activities = new ArrayList<>();
        infos = new ArrayList<>();
        mp3Infos = DbDao.queryPartMusic(getApplicationContext());
        onLineInfos = new ArrayList<>();
        bmobMusicList = MusicUtils.getMusicList();
    }

    private void initBaiduMap() {
        SDKInitializer.initialize(getApplicationContext());
    }



    public synchronized static void addActivity(Activity activity) {
        activities.add(activity);
        Log.e(TAG, activity.toString() + "已经被添加");
    }

    public synchronized static void clearAll() {

        while (activities.size()!=0){
            activities.get(0).finish();
        }
        if (activities.size() == 0)
            activities.clear();
    }

    public synchronized static void removeActivity(Activity activity) {
        activities.remove(activity);
        Log.e(TAG, activity.toString() + "已经被移除");
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .diskCacheFileCount(300)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);

    }


}
