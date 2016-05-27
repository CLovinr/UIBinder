package com.chenyg.uibinder.android.base;

import android.os.Handler;
import com.chenyg.uibinder.BindAppMain;
import com.chenyg.wporter.Config;
import com.chenyg.wporter.InitException;

import com.chenyg.wporter.a.app.AppPorterMain;
import com.chenyg.wporter.a.app.AppPorterSender;
import com.chenyg.wporter.util.FileTool;

public class AppMain
{
    private BindAppMain bindAppMain;
    private static AppMain _appMain;
    private static C2LActivity activity;
    private static Class<?> classR;

    /**
     * 设置R.class
     *
     * @param classR
     */
    public static void setClassR(Class<?> classR)
    {
        AppMain.classR = classR;
    }

    /**
     * 得到R.class
     *
     * @return
     */
    public static Class<?> getClassR()
    {
        return classR;
    }

    /**
     * 得到Activity的Handler
     *
     * @return
     */
    public static Handler getHandler()
    {
        return activity.getHandler();
    }

    /**
     * 设置Activity
     *
     * @param activity
     */
    public static void setC2LActivity(C2LActivity activity)
    {
        AppMain.activity = activity;
    }


    /**
     * 得到Activity
     *
     * @return
     */
    public static C2LActivity getActivity()
    {
        return activity;
    }

    /**
     * 初始化
     */
    static void init()
    {

        _appMain = new AppMain();
    }

    /**
     * 得到AppMain，必须先构造
     *
     * @return
     */
    public static AppMain getAppMain()
    {
        return _appMain;
    }


    private AppMain()
    {
        bindAppMain = new BindAppMain();
    }


    void start(Config config) throws InitException
    {
        bindAppMain.start(config, new MyAndroidClassLoader(getActivity()));
    }

    /**
     * 开始
     *
     * @throws InitException
     */
    void start() throws InitException
    {
        String config = getConfig();
        bindAppMain.start(Config.toConfig(config), new MyAndroidClassLoader(getActivity()));
    }

    /**
     * 是否已经启动
     *
     * @return
     */
    public static boolean isStarted()
    {
        return _appMain != null && _appMain.bindAppMain.isStarted();
    }

    /**
     * 结束,所有扫描的接口都会被释放。
     */
   public void stop()
    {
        if (bindAppMain.isStarted())
        {
            bindAppMain.stop();
            activity = null;
        }

    }

    public AppPorterSender getAppPorterSender()
    {
        return bindAppMain.getAppPorterSender();
    }

    public AppPorterMain getAppPorterMain()
    {
        return bindAppMain.getAppPorterMain();
    }

    private String getConfig()
    {
        // TODO Auto-generated method stub
        System.err.println("AppMain.getConfig()");
        System.err.println(Thread.currentThread().getContextClassLoader());
        String config = FileTool.getString(getClass().getResourceAsStream("/wpconfig/AppPorter.json"));
        return config;
    }

}
