package com.chenyg.uibinder.android;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;

/**
 * Created by 刚帅 on 2015/11/22.
 */
public class AndroidUtil
{
    /**
     * 得到屏幕尺寸（状态栏+标题栏+窗口）
     * @param activity
     * @return
     */
    public static int[] sizeScreen(Activity activity)
    {
        int[] size = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

    /**
     * 得到标题栏+窗口的尺寸
     * @param activity
     * @return
     */
    public static int[] sizeWithTitle(Activity activity)
    {
        Rect outRect = new Rect();
        int[] size = new int[2];
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        size[0] = outRect.width();
        size[1] = outRect.height();
        return size;
    }

    /**
     * 得到窗口尺寸
     * @param activity
     * @return
     */
    public static int[] sizeActivity(Activity activity)
    {
        int[] size = new int[2];
        Rect outRect = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
        size[0] = outRect.width();
        size[1] = outRect.height();
        return size;
    }
}
