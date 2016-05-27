package com.chenyg.uibinder.base;

import com.chenyg.uibinder.*;
import com.chenyg.wporter.WPObject;
import com.chenyg.wporter.WebPorter;
import com.chenyg.wporter.annotation.ChildIn;

/**
 * Created by 宇宙之灵 on 2015/9/14.
 */
public abstract class BinderPorter extends WebPorter
{
    private String porterPrefix;
    private Object view;
    private BinderDataSender binderDataSender;

    public static final String CALLBACK = "callback";
    /**
     * 回调传入的接口前缀。
     */
    public static final String CALLBACK_PORTER_PREFIX = "porterPrefix";
    /**
     * 回调传入的view。
     */
    public static final String CALLBACK_VIEW = "view";

    public static final String CALLBACK_BINDER_DATA_SENDER = "binderDataSender";

    public static final String ON_READY = "onReady";


    /**
     * 设置触发控件的可用性
     *
     * @param porterPrefix
     * @param tiedName
     * @param enable
     */
    protected void enableBtn(String porterPrefix, String tiedName, boolean enable)
    {
        enable(porterPrefix, tiedName, tiedName, enable);
    }

    protected void enable(String porterPrefix, String tiedFun, String tiedName, boolean enable)
    {
        BinderData binderData = new BinderData();
        binderData.addSetTask(new BinderSet(tiedFun, tiedName, AttrEnum.ATTR_ENABLE, enable));
        sendBinderData(porterPrefix, binderData, false);
    }

    protected void sendBinderData(String porterPrefix, BinderData binderData, boolean toAll)
    {
        if (binderDataSender == null)
        {
            BaseUI.getBaseUI().sendBinderData(porterPrefix, binderData, toAll);
        } else
        {
            binderDataSender.sendBinderData(porterPrefix, binderData, toAll);
        }
    }

    protected void onValueChange(String porterPrefix, String tiedFun, String tiedName,
            OnValueChangedListener onValueChangedListener)
    {
        BinderData binderData = new BinderData();
        binderData.addSetTask(
                new BinderSet(tiedFun, tiedName, AttrEnum.ATTR_VALUE_CHANGE_LISTENER, onValueChangedListener));
        sendBinderData(porterPrefix, binderData, false);
    }

    /**
     * 设置值。
     *
     * @param porterPrefix
     * @param tiedName
     * @param varName
     * @param value
     */
    protected void setValue(String porterPrefix, String tiedName, String varName, Object value)
    {
        BinderData binderData = new BinderData();
        binderData.addSetTask(new BinderSet(tiedName, varName, AttrEnum.ATTR_VALUE, value));
        sendBinderData(porterPrefix, binderData, false);
    }

    private static class Temp
    {
        Object value;
    }

    /**
     * 必须支持同步获取值。
     *
     * @return
     */
    protected Object getValue(String porterPrefix, String tiedName, String varName)
    {
        final Temp temp = new Temp();
        BinderData binderData = new BinderData();
        binderData.addGetListener(new BinderGetListener()
        {
            @Override
            public void onGet(Object[] values)
            {
                temp.value = values[0];
            }
        }, new BinderGet(tiedName, varName, AttrEnum.ATTR_VALUE));
        sendBinderData(porterPrefix, binderData, false);

        return temp.value;
    }

    protected void setValueOther(String porterPrefix, String tiedName, String varName, Object value)
    {
        BinderData binderData = new BinderData();
        binderData.addSetTask(new BinderSet(tiedName, varName, AttrEnum.ATTR_VALUE_OTHER, value));
        sendBinderData(porterPrefix, binderData, false);
    }

    /**
     * 设置接口前缀。
     *
     * @param porterPrefix
     */
    protected void setPorterPrefix(String porterPrefix)
    {
        this.porterPrefix = porterPrefix;
    }

    /**
     * 得到接口前缀
     *
     * @return
     */
    protected String getPorterPrefix()
    {
        return porterPrefix;
    }

    /**
     * 得到view.
     *
     * @return
     */
    protected Object getView()
    {
        return view;
    }

    /**
     * 得到Prefix
     *
     * @param c
     * @return
     */
    public static Prefix getPrefix(Class<? extends WebPorter> c)
    {
        String porterPrefix = WebPorter.getTiedName(c);
        Prefix prefix = new Prefix(
                porterPrefix.substring(0, 1).toLowerCase() + porterPrefix.substring(1, porterPrefix.length() - 1) + "_",
                porterPrefix, CALLBACK, null);

        return prefix;
    }


    /**
     * 设置view
     *
     * @param view
     */
    protected void setView(Object view)
    {
        this.view = view;
    }

    /**
     * 会设置PorterPrefix.见：{@linkplain #getPorterPrefix()}.
     *
     * @param wpObject
     */
    @ChildIn(tiedName = CALLBACK, neceParams = {CALLBACK_PORTER_PREFIX, CALLBACK_VIEW}, unneceParams =
            {CALLBACK_BINDER_DATA_SENDER})
    public void callback(WPObject wpObject)
    {
        for (int i = 0; i < wpObject.inNames.cnNames.length; i++)
        {
            if (wpObject.inNames.cnNames[i].equals(CALLBACK_PORTER_PREFIX))
            {
                this.porterPrefix = (String) wpObject.cns[i];
                break;
            }
        }
        for (int i = 0; i < wpObject.inNames.cnNames.length; i++)
        {
            if (wpObject.inNames.cnNames[i].equals(CALLBACK_VIEW))
            {
                this.view = wpObject.cns[i];
                break;
            }
        }
        for (int i = 0; i < wpObject.inNames.cuNames.length; i++)
        {
            if (wpObject.inNames.cuNames[i].equals(CALLBACK_BINDER_DATA_SENDER))
            {
                this.binderDataSender = (BinderDataSender) wpObject.cus[i];
                break;
            }
        }

    }


    @ChildIn(tiedName = "")
    public void back()
    {
        BaseUI.getBaseUI().popBinder();
    }

    /**
     * 界面显示完成
     */
    @ChildIn(tiedName = ON_READY)
    public void onReady()
    {

    }


}
