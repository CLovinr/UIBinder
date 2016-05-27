package com.chenyg.uibinder.base;

import com.chenyg.wporter.a.app.AppPorterUtil;
import com.chenyg.wporter.base.AppValues;

import java.io.Serializable;

/**
 * Created by ZhuiFeng on 2015/6/12.
 */
public class PorterCallback implements Serializable
{
    private String prefix;
    private String method;

    private String ex;
    private AppValues appValues;


    /**
     * 异常信息对应的参数名称
     */
    public static final String EX_NAME = "ex";

    public PorterCallback(String prefix, String method)
    {
        this.prefix = prefix;
        this.method = method;
    }

    /**
     * 设置参数值。
     *
     * @param appValues
     */
    public void setAppValues(AppValues appValues)
    {
        this.appValues = appValues;
    }

    /**
     * 设置异常信息表示出现问题。
     *
     * @param ex 异常描述
     */
    public void setEx(String ex)
    {
        this.ex = ex;
    }


    /**
     * 调用该函数，会进行发送。
     */
    public void call()
    {
        String[] names;
        Object[] values;
        if (appValues != null)
        {
            String[] strings = appValues.getNames();
            Object[] objs = appValues.getValues();
            names = new String[1 + strings.length];
            values = new Object[names.length];
            for (int i = 1; i < names.length; i++)
            {
                names[i] = strings[i - 1];
                values[i] = objs[i - 1];
            }
        } else
        {
            names = new String[1];
            values = new Object[1];
        }
        names[0] = EX_NAME;
        values[0] = ex;
        AppPorterUtil.getPorterObject(prefix, method, names, values);
    }
}
