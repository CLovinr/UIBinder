package com.chenyg.uibinder;


import com.chenyg.uibinder.base.BinderPorter;
import com.chenyg.uibinder.j2se.BindJFrame;
import com.chenyg.uibinder.simple.SimpleErrDealt;
import com.chenyg.wporter.WebPorter;

/**
 * Created by ZhuiFeng on 2015/6/13.
 */
public class Prefix
{


    /**
     * id名称前缀
     */
    public String idPrefix;

    /**
     * 接口前缀
     */
    public String porterPrefix;

    public ErrListener errListener;

    /**
     * 绑定完成后用于调用的接口,且会传人view;只调用一次。
     */
    public String bindCallbackMethod;

    private String showAgainCallback;

    @Override
    public String toString()
    {
        return idPrefix + "" + porterPrefix + "(" + bindCallbackMethod + "," + showAgainCallback + ")";
    }

    /**
     * @param idPrefix           代表id的内容前缀
     * @param porterPrefix       调用的接口前缀
     * @param bindCallbackMethod 绑定完成后用于调用的接口,
     *                           且会传人非必需参数{@linkplain BinderPorter#CALLBACK_BINDER_DATA_SENDER
     *                           }，必需参数{@linkplain BinderPorter#CALLBACK_PORTER_PREFIX}和{@linkplain
     *                           BinderPorter#CALLBACK_VIEW}(不同平台，类型不同,安卓为View，而j2se为{@linkplain
     *                           BindJFrame}或{@linkplain javax.swing.JDialog});只调用一次。
     * @param errListener        错误监听器,若为null；则会设置成{@link SimpleErrDealt}
     */
    public Prefix(String idPrefix, String porterPrefix, String bindCallbackMethod, ErrListener errListener)
    {
        this.idPrefix = idPrefix;
        this.porterPrefix = porterPrefix;
        this.bindCallbackMethod = bindCallbackMethod;
        this.errListener = errListener == null ? SimpleErrDealt.getSimpleErrDealt() : errListener;
    }

    /**
     * 见{@linkplain #Prefix(String, String, String, ErrListener)}和{@linkplain #setShowAgainCallback(String)}
     *
     * @param idPrefix
     * @param porterPrefix
     * @param bindCallbackMethod
     * @param errListener
     * @param showAgainCallback
     */
    public Prefix(String idPrefix, String porterPrefix, String bindCallbackMethod, ErrListener errListener,
            String showAgainCallback)
    {
        this(idPrefix, porterPrefix, bindCallbackMethod, errListener);
        setShowAgainCallback(showAgainCallback);
    }


    /**
     * 规则为:
     * <pre>
     *     绑定名为："TiedName"
     *     1.idPrefix:"tiedName_"
     *     2.porterPrefix:"TiedName/"
     *     3.callback:{@linkplain BinderPorter#CALLBACK}
     * </pre>
     *
     * @param c 接口类
     * @return 返回构造的对象
     */
    public static Prefix buildPrefix(Class<? extends WebPorter> c)
    {
        String porterPrefix = WebPorter.getTiedName(c);
        Prefix prefix = new Prefix(
                porterPrefix.substring(0, 1).toLowerCase() + porterPrefix.substring(1, porterPrefix.length() - 1) + "_",
                porterPrefix, BinderPorter.CALLBACK, null);
        return prefix;
    }

    /**
     * 得到再次显示的回调函数
     *
     * @return
     */
    public String getShowAgainCallback()
    {
        return showAgainCallback;
    }

    /**
     * 设置再次显示的回调函数。，调用时，会传人view参数。
     *
     * @param showAgainCallback
     */
    public void setShowAgainCallback(String showAgainCallback)
    {
        this.showAgainCallback = showAgainCallback;
    }
}
