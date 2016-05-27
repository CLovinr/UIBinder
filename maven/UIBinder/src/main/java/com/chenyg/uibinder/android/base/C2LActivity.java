package com.chenyg.uibinder.android.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import com.chenyg.wporter.Config;
import com.chenyg.wporter.WPMain;
import com.chenyg.wporter.WebPorter;

import java.util.Set;


/**
 * Created by ZhuiFeng on 2015/6/10.
 */
public class C2LActivity extends Activity {
    private Class<?> classR;
    private Handler handler = new Handler();
    private Config config;

    /**
     *
     * @param classR
     * @param config 用于初始化
     */
    public C2LActivity(Class<?> classR,Config config) {
        this(classR);
        this.config=config;
    }

    public C2LActivity(Class<?> classR) {
        setClassR(classR);
    }

    private void setAppMain() {
        AppMain.setC2LActivity(this);
        AppMain.setClassR(classR);
    }


    private void init() {
        try {
            AppMain.init();
            if (config==null){
                AppMain.getAppMain().start();
            }else{
                AppMain.getAppMain().start(config);
                config=null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置R.class
     *
     * @param classR
     */
    void setClassR(Class<?> classR) {
        this.classR = classR;
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppMain();
        if (!AppMain.isStarted()) {
            init();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAppMain();
    }

    /**
     * @param isSys
     * @param sets
     */
    protected void seekPorter(boolean isSys, Set<WebPorter>... sets) {
        WPMain wpMain = AppMain.getAppMain().getAppPorterMain().getWpMain();
        for (Set<WebPorter> set : sets) {
            wpMain.seekPorters(set, isSys);
        }
    }

    /**
     * 先清理所有接口，再添加
     *
     * @param isSys
     */
    protected void clearPorter(boolean isSys) {
        setAppMain();
        WPMain wpMain = AppMain.getAppMain().getAppPorterMain().getWpMain();
        wpMain.clearAllPorters(isSys);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 退出
     */
    protected void stopPorter() {
        if (AppMain.getAppMain().isStarted()) {
            AppMain.getAppMain().stop();
        }
    }
}
