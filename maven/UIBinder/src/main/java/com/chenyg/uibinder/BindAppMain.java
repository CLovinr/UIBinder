package com.chenyg.uibinder;


import com.chenyg.wporter.Config;
import com.chenyg.wporter.InitException;
import com.chenyg.wporter.a.app.AppPorterMain;
import com.chenyg.wporter.a.app.AppPorterSender;
import com.chenyg.wporter.a.app.AppPorterUtil;


/**
 * Created by 宇宙之灵 on 2015/9/10.
 */
public class BindAppMain
{

    private AppPorterMain appPorterMain;

    public BindAppMain()
    {
        appPorterMain = new AppPorterMain("");
    }

    /**
     * 开始
     *
     * @param config
     * @param classLoader
     * @throws InitException
     */
    public void start(Config config, ClassLoader classLoader) throws InitException
    {
        appPorterMain.start(config, null, false, classLoader);
        AppPorterUtil.setAppPorterMain(appPorterMain);
    }



    /**
     * 是否已经启动
     *
     * @return
     */
    public boolean isStarted()
    {
        return appPorterMain.isStarted();
    }

    /**
     * 结束,所有扫描的接口都会被释放。
     */
    public void stop()
    {
        if (appPorterMain != null && appPorterMain.isStarted())
        {
            appPorterMain.stop();
        }

    }

    public AppPorterSender getAppPorterSender()
    {
        return appPorterMain.getAppPorterSender();
    }

    public AppPorterMain getAppPorterMain()
    {
        return appPorterMain;
    }


}
