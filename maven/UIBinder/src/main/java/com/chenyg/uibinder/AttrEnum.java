package com.chenyg.uibinder;

/**
 * 与控件属性有关的
 * Created by ZhuiFeng on 2015/6/11.
 */
public enum AttrEnum
{
    /**
     * enable控件,传入boolean
     */
    ATTR_ENABLE,
    /**
     * 与焦点有关的
     */
    ATTR_FOCUS_REQUEST,
    /**
     * 可见性
     */
    ATTR_VISIBLE,
    /**
     * 控件值
     */
    ATTR_VALUE,

    /**
     * 用于获取控件相对于窗口的绝对坐标以及宽度和高度:[x,y,width,height]
     */
    ATTR_BOUNDS,

    /**
     * 其他类型值的设定
     */
    ATTR_VALUE_OTHER,

    /**
     * 内容改变监听器
     */
    ATTR_VALUE_CHANGE_LISTENER,
    /**
     * 直接设置则{@linkplain com.chenyg.uibinder.BinderData.Task#data}为List&#60;{@link BinderSet}&#62;
     */
    METHOD_SET,

    /**
     * 异步设置:则{@linkplain com.chenyg.uibinder.BinderData.Task#data}为{@linkplain com.chenyg.uibinder.AsynSetListener.Receiver}
     */
    METHOD_ASYN_SET,
    /**
     * 用于得到值
     */
    METHOD_GET
}
