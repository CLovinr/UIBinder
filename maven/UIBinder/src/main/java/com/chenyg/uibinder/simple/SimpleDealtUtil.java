package com.chenyg.uibinder.simple;


import com.chenyg.uibinder.*;
import com.chenyg.uibinder.base.HttpDelivery;
import com.chenyg.uibinder.base.HttpMethod;
import com.chenyg.uibinder.base.JRCallback;
import com.chenyg.wporter.WPObject;
import com.chenyg.wporter.WebPorter;
import com.chenyg.wporter.annotation.ThinkType;
import com.chenyg.wporter.base.AppValues;
import com.chenyg.wporter.base.JResponse;
import com.chenyg.wporter.base.ResultCode;
import com.chenyg.wporter.log.LogUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class SimpleDealtUtil
{
    private HttpDelivery _httpDelivery;
    private SimpleDealt simpleDealt = AutoPrefix.getSimpleDealt();
    private ThinkType thinkType;
    private ErrListener errListener = SimpleErrDealt.getSimpleErrDealt();
    private Executor executor;


    public SimpleDealtUtil(HttpDelivery httpDelivery, ThinkType thinkType, Executor executor)
    {
        this._httpDelivery = httpDelivery;
        this.thinkType = thinkType;
        this.executor = executor;
    }

    public SimpleDealtUtil(HttpDelivery httpDelivery, ThinkType thinkType)
    {
        this(httpDelivery, thinkType, 3);
    }

    public SimpleDealtUtil(HttpDelivery httpDelivery, ThinkType thinkType, int fixedThreadCount)
    {
        this(httpDelivery, thinkType, Executors.newFixedThreadPool(fixedThreadCount));
    }


    /**
     * 异步回调，会开启一个线程;并且,会禁用触发控件，请求返回后自动恢复。
     *
     * @param stack
     * @param httpMethod
     * @param wpObject
     * @param porterPrefix 该ui绑定对应的接口前缀。
     * @param simpleDealt
     */
    public void deliveryAsyn(int stack, final HttpMethod httpMethod, final WPObject wpObject, final String porterPrefix,
            final SimpleDealt simpleDealt)
    {

        try
        {
            Object[] names = LogUtil.methodAndClass(2 + stack);
            final Class<? extends WebPorter> c = (Class<? extends WebPorter>) Class.forName((String) names[1]);
            final String method = (String) names[0];
            final String tiedFunName = HttpDelivery.getTiedFunName(c, method);

            BinderData binderData = new BinderData();
            binderData.addSetTask(new BinderSet(tiedFunName, tiedFunName, AttrEnum.ATTR_ENABLE, false));
            BaseUI.getBaseUI().sendBinderData(porterPrefix, binderData, false);

            executor.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    _delivery_(httpMethod, wpObject, c, method, 0, simpleDealt);
                    BinderData binderData = new BinderData();
                    binderData.addSetTask(new BinderSet(tiedFunName, tiedFunName, AttrEnum.ATTR_ENABLE, true));
                    BaseUI.getBaseUI().sendBinderData(porterPrefix, binderData, false);

                }
            });
        } catch (ClassNotFoundException e)
        {
            throw new HttpDelivery.DeliveryException("getClass EX:" + e.toString());
        } catch (NoSuchMethodException e)
        {
            throw new HttpDelivery.DeliveryException("getMethod EX:" + e.toString());
        }
    }


    /**
     * 同步
     *
     * @param httpMethod
     * @param appValues
     * @param porterPrefix 如果提供了，则会禁用相关按钮
     * @param tiedFun
     * @param showWaiting
     * @return
     */
    public JResponse delivery(HttpMethod httpMethod, AppValues appValues, String porterPrefix, String tiedFun,
            boolean showWaiting, SimpleDealt simpleDealt, JRCallback jrCallback)
    {
        JResponse jResponse = _delivery(httpMethod, appValues == null ? null : new WPObject(appValues),
                porterPrefix, tiedFun, showWaiting,
                simpleDealt, jrCallback);
        return jResponse;
    }

    public JResponse delivery(HttpMethod httpMethod, WPObject wpObject, String porterPrefix, String tiedFun,
            boolean showWaitting, SimpleDealt simpleDealt, JRCallback jrCallback)
    {
        JResponse jResponse = _delivery(httpMethod, wpObject, porterPrefix, tiedFun, showWaitting,
                simpleDealt, jrCallback);
        return jResponse;
    }

    /**
     * 异步回调，会开启一个线程.
     *
     * @param stack       直接调用则为0，间隔一个函数则为1.
     * @param httpMethod
     * @param wpObject
     * @param simpleDealt
     */
    public void deliveryAsyn(int stack, final HttpMethod httpMethod, final WPObject wpObject,
            final SimpleDealt simpleDealt)
    {

        try
        {
            Object[] names = LogUtil.methodAndClass(2 + stack);
            final Class<? extends WebPorter> c = (Class<? extends WebPorter>) Class.forName((String) names[1]);
            final String method = (String) names[0];
            executor.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    _delivery_(httpMethod, wpObject, c, method, 0, simpleDealt);
                }
            });
        } catch (ClassNotFoundException e)
        {
            throw new HttpDelivery.DeliveryException("getClass EX:" + e.toString());
        }
    }

    /**
     * 阻塞模式，若是在ui线程中调用，则会阻塞ui。
     *
     * @param httpMethod
     * @param wpObject
     * @return
     */
    public JResponse delivery(int stack, HttpMethod httpMethod, WPObject wpObject, SimpleDealt simpleDealt)
    {
        return _delivery_(httpMethod, wpObject, null, null, stack + 1, simpleDealt);
    }


    private JResponse _delivery_(HttpMethod httpMethod, WPObject wpObject,
            Class<? extends WebPorter> webPorterClass, String method, int stack, SimpleDealt simpleDealt)
    {
        JResponse jr;
        HttpDelivery.Param forParam = new HttpDelivery.Param();
        try
        {

            jr = webPorterClass == null ? _httpDelivery
                    ._delivery(httpMethod, wpObject, stack + 1, forParam, null) : _httpDelivery
                    .delivery(httpMethod, wpObject, webPorterClass, method, forParam, null);
            if (simpleDealt == null)
            {
                simpleDealt = this.simpleDealt;
            }
            SimpleDealtPrefix
                    .dealResponse(httpMethod, _httpDelivery, jr, errListener, simpleDealt, forParam.porterPrefix,
                            forParam.tiedFun);
        } catch (HttpDelivery.DeliveryException e)
        {
            jr = showEx(e, forParam.porterPrefix, forParam.tiedFun);
            jr.setExCause(e);
        }
        return jr;
    }


    private JResponse showEx(HttpDelivery.DeliveryException e, String porterPrefix, String tiedFun)
    {
        BaseUI.getBaseUI().alert(LangMap.getLangMap().get(LangMap.CommonStr.EXCEPTION) + ":\n" + e.toString());
        JResponse jr = new JResponse(ResultCode.EXCEPTION);
        jr.setDescription(e.toString());
        if (simpleDealt != null)
        {
            simpleDealt.onException(porterPrefix, tiedFun, e);
        }
        return jr;
    }


    private JResponse _delivery(final HttpMethod httpMethod, WPObject wpObject, final String porterPrefix,
            final String tiedFun,
            final boolean showWaiting, SimpleDealt simpleDealt, final JRCallback jrCallback)
    {
        JResponse jr = null;
        if (porterPrefix != null)
        {
            BinderData binderData = new BinderData();
            binderData.addSetTask(new BinderSet(tiedFun, tiedFun, AttrEnum.ATTR_ENABLE, false));
            BaseUI.getBaseUI().sendBinderData(porterPrefix, binderData, false);
        }
        try
        {
            if (simpleDealt == null)
            {
                simpleDealt = this.simpleDealt;
            }
            if (showWaiting)
            {
                BaseUI.getBaseUI().waitingShow();
            }
            if (jrCallback == null)
            {
                jr = _httpDelivery
                        .delivery(httpMethod, wpObject, porterPrefix, thinkType == ThinkType.REST ? "" : tiedFun, null);
                SimpleDealtPrefix
                        .dealResponse(httpMethod, _httpDelivery, jr, errListener, simpleDealt, porterPrefix, tiedFun);
            } else
            {
                final SimpleDealt finalSimpleDealt = simpleDealt;
                JRCallback callback = new JRCallback()
                {
                    @Override
                    public void onResult(JResponse jResponse)
                    {
                        SimpleDealtPrefix
                                .dealResponse(httpMethod, _httpDelivery, jResponse, errListener, finalSimpleDealt,
                                        porterPrefix, tiedFun);
                        jrCallback.onResult(jResponse);
                        if (showWaiting)
                        {
                            BaseUI.getBaseUI().waitingDisShow();
                        }
                    }
                };
                jr = _httpDelivery
                        .delivery(httpMethod, wpObject, porterPrefix, thinkType == ThinkType.REST ? "" : tiedFun,
                                callback);
            }


        } catch (HttpDelivery.DeliveryException e)
        {
            jr = showEx(e, porterPrefix, tiedFun);
            jr.setExCause(e);
            if (jrCallback != null)
            {
                jrCallback.onResult(jr);
            }
            SimpleDealtPrefix.restoreEnableState(porterPrefix, tiedFun);
            if (showWaiting)
            {
                BaseUI.getBaseUI().waitingDisShow();
            }
        }
        return jr;
    }

}
